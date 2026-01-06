package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.AppointmentStatus;
import org.health.medical_service.entities.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Represents an appointment returned by the medical service")
public record AppointmentDto(
        @Schema(
                description = "Unique identifier of the appointment",
                example = "9f1b4c28-8d47-4e2c-bd1f-2c1b91e7a3f9"
        )
        UUID id,

        @Schema(
                description = "Scheduled date and time of the appointment",
                example = "2026-01-10T14:30:00"
        )
        LocalDateTime appointmentTime,

        @Schema(
                description = "Current status of the appointment",
                example = "SCHEDULED"
        )
        AppointmentStatus status,

        @Schema(
                description = "Doctor's notes or outcome of the appointment",
                example = "Patient diagnosed with mild hypertension"
        )
        String result,

        @Schema(
                description = "Type of appointment",
                example = "CONSULTATION"
        )
        AppointmentType appointmentType,

        @Schema(
                description = "Identifier of the follow-up appointment created from this appointment",
                example = "3a6d8e27-4c2f-4b71-9e4a-91c9d2f5c611",
                nullable = true
        )
        UUID followUpAppointmentId
) {}