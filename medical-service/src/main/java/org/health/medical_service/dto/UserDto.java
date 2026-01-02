package org.health.medical_service.dto;

import org.health.medical_service.entities.UserRole;

import java.util.UUID;

public record UserDto (
        UUID id,
        String email,
        String password,
        UserRole role
) {
}
