package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.DayOfTheWeek;

import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Represents a doctor's availability on a specific day")
public record DoctorAvailabilityDto(
        @Schema(
                description = "Unique identifier of the availability entry",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID id,

        @Schema(
                description = "Day of the week this availability applies to",
                example = "MONDAY"
        )
        DayOfTheWeek day,

        @Schema(
                description = "Start time of the availability slot",
                example = "09:00"
        )
        LocalTime startTime,

        @Schema(
                description = "End time of the availability slot",
                example = "17:00"
        )
        LocalTime endTime
) {}