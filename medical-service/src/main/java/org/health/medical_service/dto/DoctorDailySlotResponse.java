package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Represents a doctor's daily schedule including booked hours and available time slots")
public record DoctorDailySlotResponse(
        @Schema(
                description = "Unique identifier of the doctor",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID doctorId,

        @Schema(
                description = "Full name of the doctor",
                example = "Dr. Jane Smith"
        )
        String fullName,

        @Schema(
                description = "Total hours already booked for the day",
                example = "5"
        )
        int totalBookedHours,

        @Schema(
                description = "List of free time ranges the doctor is available for appointments"
        )
        List<TimeRange> freeRanges
) {}