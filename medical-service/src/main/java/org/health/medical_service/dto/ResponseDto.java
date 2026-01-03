package org.health.medical_service.dto;

public record ResponseDto(
        boolean status,
        String message,
        Object data
) {
    public ResponseDto(String message, Object data) {
        this(true, message, data);
    }

    public ResponseDto(boolean status, String message) {
        this(status, message, null);
    }

    public ResponseDto(String message) {
        this(true, message, null);
    }
}
