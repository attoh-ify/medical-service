package org.health.medical_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestAppointmentDto(
        String patientEmail,
        LocalDateTime appointmentTime,
        UUID doctorId
) {
}
