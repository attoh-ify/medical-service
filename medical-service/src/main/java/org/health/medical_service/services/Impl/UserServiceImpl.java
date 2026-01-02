package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.User;
import org.health.medical_service.repositories.UserRepository;
import org.health.medical_service.services.UserService;
import org.health.medical_service.utils.helpers;
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

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public User registerUser(User user) {
        validateUser(user);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserDetails(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email is not registered."));
    }

    @Override
    public String loginUser(User user) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail());
        }
        throw new IllegalArgumentException("Invalid username or password.");
    }

    private void validateUser(User user) {
        if (user.getId() != null) throw new IllegalArgumentException("User ID is system generated");
        if (helpers.isBlank(user.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(user.getPassword())) throw new IllegalArgumentException("Password required");
        if (user.getRole() == null) throw new IllegalArgumentException("Role required");

        userRepository.findByEmail(user.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        });
    }
}
