package org.health.medical_service.dto;

import org.health.medical_service.entities.AppointmentStatus;
import org.health.medical_service.entities.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDto(
        UUID id,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        String result,
        AppointmentType appointmentType,
        UUID followUpAppointmentId
) {
}
