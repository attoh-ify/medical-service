package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "RequestAppointmentDto",
        description = "Request payload for booking or scheduling a medical appointment"
)
public record RequestAppointmentDto(
        @Schema(
                description = "Unique identifier of the patient booking the appointment",
                example = "a12f9c3e-8b91-4d62-9f12-cc93a5d72a11",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID patientId,

        @Schema(
                description = "Scheduled date and time for the appointment",
                example = "2026-01-10T14:30",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime appointmentTime,

        @Schema(
                description = "Unique identifier of the doctor assigned to the appointment",
                example = "b1a9f7d3-8c22-4b92-a8b1-12d98f43c821",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID doctorId,

        @Schema(
                description = "Type of follow-up appointment, required only when booking a follow-up",
                example = "CONSULTATION",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        AppointmentType followUpAppointmentType
) {}
