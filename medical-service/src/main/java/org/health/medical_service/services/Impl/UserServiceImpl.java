package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.LoginDto;
import org.health.medical_service.entities.User;
import org.health.medical_service.exceptions.BadRequestException;
import org.health.medical_service.repositories.UserRepository;
import org.health.medical_service.services.UserService;
import org.health.medical_service.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        log.info("UserServiceImpl initialized");
    }

    @Override
    public User registerUser(User user) {
        log.info("Registering user email={}", user.getEmail());

        validateUser(user);
        user.setPassword(encoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

        log.info("User registered successfully userId={} email={}",
                saved.getId(), saved.getEmail());

        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserDetails(String email) {
        log.debug("Fetching user details email={}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found email={}", email);
                    return new BadRequestException(
                            "User with this email is not registered."
                    );
                });
    }

    @Override
    public String loginUser(LoginDto user) {
        log.info("Login attempt email={}", user.email());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.email(), user.password()
                        )
                );

        if (authentication.isAuthenticated()) {
            log.info("Authentication successful email={}", user.email());
            return jwtService.generateToken(user.email());
        }

        log.warn("Authentication failed email={}", user.email());
        throw new BadRequestException("Invalid username or password.");
    }

    private void validateUser(User user) {
        log.debug("Validating user registration email={}", user.getEmail());

        if (user.getId() != null)
            throw new BadRequestException("User ID is system generated");
        if (helpers.isBlank(user.getEmail()))
            throw new BadRequestException("Email required");
        if (helpers.isBlank(user.getPassword()))
            throw new BadRequestException("Password required");
        if (user.getRole() == null)
            throw new BadRequestException("Role required");

        userRepository.findByEmail(user.getEmail()).ifPresent(existing -> {
            log.warn("Duplicate user registration email={}", user.getEmail());
            throw new BadRequestException(
                    "This email is already registered to a patient."
            );
        });
    }
}
