package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.UserRole;

import java.util.UUID;

@Schema(description = "Represents a system user with credentials and role")
public record UserDto(
        @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Full name of the user", example = "John Doe")
        String fullName,

        @Schema(description = "Email address of the user", example = "user@example.com")
        String email,

        @Schema(description = "Phone number of the user", example = "080123456789")
        String phone,

        @Schema(description = "Password for authentication", example = "StrongP@ssw0rd")
        String password,

        @Schema(description = "Role of the user in the system")
        UserRole role
) {}