package org.health.medical_service.services;

import org.health.medical_service.entities.User;

public interface UserService {
    User registerUser(User user);
    User getUserDetails(String email);
    String loginUser(User user);
}
