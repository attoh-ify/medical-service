package org.health.medical_service.dto;

import org.health.medical_service.entities.DayOfTheWeek;

import java.time.LocalTime;
import java.util.UUID;

public record DoctorAvailabilityDto(
        UUID id,
        DayOfTheWeek day,
        LocalTime startTime,
        LocalTime endTime,
        boolean isBooked
) {
}
