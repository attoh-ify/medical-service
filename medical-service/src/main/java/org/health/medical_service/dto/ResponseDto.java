package org.health.medical_service.dto;

public record ResponseDto(
        boolean status,
        String message,
        Object data
) {
}
