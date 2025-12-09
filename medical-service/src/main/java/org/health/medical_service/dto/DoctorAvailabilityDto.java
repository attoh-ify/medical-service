package org.health.medical_service.dto;

import org.health.medical_service.entities.DayOfTheWeek;

import java.time.LocalDateTime;
import java.util.UUID;

public record DoctorAvailabilityDto(
        UUID id,
        DayOfTheWeek day,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean isBooked
) {
}
