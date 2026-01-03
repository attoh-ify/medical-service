package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.User;
import org.health.medical_service.entities.UserPrincipal;
import org.health.medical_service.exceptions.BadRequestException;
import org.health.medical_service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(MyUserDetailsService.class);

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("MyUserDetailsService initialized");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found during authentication email={}", email);
                    return new BadRequestException("User not found");
                });

        log.debug("User loaded successfully email={} userId={}",
                user.getEmail(), user.getId());

        return new UserPrincipal(user);
    }
}
