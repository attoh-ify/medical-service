package org.health.medical_service.config;

import org.health.medical_service.filters.JwtFilter;
import org.health.medical_service.services.Impl.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Enables Spring Security filter chain integration
public class SecurityConfig {
    // Custom UserDetailsService used by Spring Security to load users from DB
    private final MyUserDetailsService userDetailsService;

    // Custom JWT filter that validates JWT on each request
    private final JwtFilter jwtFilter;

    public SecurityConfig(MyUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Defines the main Spring Security filter chain.
     * This controls:
     *  - which routes require authentication
     *  - which authentication mechanisms are enabled
     *  - session behavior
     *  - custom filters (JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (safe because we use stateless JWTs, not sessions)
                .csrf(AbstractHttpConfigurer::disable)

                // Authorization rules for HTTP requests
                .authorizeHttpRequests(
                        request -> request
                                // Public endpoints (no authentication required)
                                .requestMatchers("/api/users/register", "/api/users/login")
                                .permitAll()

                                // All other endpoints require authentication
                                .anyRequest().authenticated()
                )

                // Enables default Spring Security form login (useful for testing / fallback)
                .formLogin(Customizer.withDefaults())

                // Enables HTTP Basic authentication (mainly for debugging / tools like Postman)
                .httpBasic(Customizer.withDefaults())

                // Configure session management
                .sessionManagement(
                        session ->
                                // Do not create or use HTTP sessions
                                // Each request must be authenticated independently (JWT)
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register JWT filter BEFORE username/password authentication filter
                // This ensures JWT is processed first on every request
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Build the security filter chain
                .build();
    }

    /**
     * AuthenticationProvider responsible for:
     *  - loading users via UserDetailsService
     *  - validating passwords using BCrypt
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Password encoder used to compare raw password with hashed password in DB
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));

        // Custom user lookup logic (database-backed)
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    /**
     * AuthenticationManager used during login
     * Delegates authentication to configured AuthenticationProviders
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
