package org.health.medical_service.dto;

import org.health.medical_service.entities.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestAppointmentDto(
        UUID patientId,
        LocalDateTime appointmentTime,
        UUID doctorId,
        AppointmentType followUpAppointmentType
) {
}
