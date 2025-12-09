package org.health.medical_service.dto;

import java.util.List;
import java.util.UUID;

public record DoctorDto(
        UUID id,
        String fullName,
        String email,
        String phone,
        String specialization,
        String bio,
        List<DoctorAvailabilityDto> doctorAvailabilities,
        List<AppointmentDto> appointments
) {
}
