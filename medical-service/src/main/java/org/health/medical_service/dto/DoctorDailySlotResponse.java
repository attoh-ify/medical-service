package org.health.medical_service.dto;

import java.util.List;
import java.util.UUID;

public record DoctorDailySlotResponse(
        UUID doctorId,
        String fullName,
        int totalBookedHours,
        List<TimeRange> freeRanges
) {}