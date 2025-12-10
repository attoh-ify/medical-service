package org.health.medical_service.dto;

import java.time.LocalDate;
import java.util.List;

public record DayGroupedAvailabilityResponse(
        LocalDate date,
        List<DoctorDailySlotResponse> doctors
) {}
