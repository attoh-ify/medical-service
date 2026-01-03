package org.health.medical_service.services.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private final String secretKey;

    private static final Logger log =
            LoggerFactory.getLogger(JWTService.class);

    public JWTService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
        log.info("JWTService initialized with configured secret");
    }

    public String generateToken(String username) {
        log.info("Generating JWT token for user={}", username);

        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 minutes
                .and()
                .signWith(getKey())
                .compact();

        log.debug("JWT token generated successfully for user={}", username);
        return token;
    }

    private SecretKey getKey() {
        log.trace("Deriving signing key for JWT operations");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from JWT");
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        log.trace("Extracting claim from JWT");
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.trace("Parsing JWT claims");
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        log.debug("Validating JWT token for user={}", userDetails.getUsername());

        final String userName = extractUsername(token);

        boolean valid = userName.equals(extractUsername(token))
                && !isTokenExpired(token);

        log.debug(
                "JWT validation completed user={} valid={}",
                userName,
                valid
        );

        return valid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.trace("JWT expiration check expired={}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        log.trace("Extracting JWT expiration timestamp");
        return extractClaim(token, Claims::getExpiration);
    }
}
