package org.health.medical_service.dto;

import org.health.medical_service.entities.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestAppointmentDto(
        String patientEmail,
        LocalDateTime appointmentTime,
        UUID doctorId,
        UUID previousAppointmentID,
        AppointmentType followUpAppointmentType
) {
}
