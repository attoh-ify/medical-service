package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Represents the availability of doctors grouped by a specific day")
public record DayGroupedAvailabilityResponse(
        @Schema(
                description = "The date for which doctor availabilities are provided",
                example = "2026-01-10"
        )
        LocalDate date,

        @Schema(
                description = "List of doctors and their available time slots for this date"
        )
        List<DoctorDailySlotResponse> doctors
) {}