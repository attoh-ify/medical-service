package org.health.medical_service.dto;

import org.health.medical_service.entities.Specialization;

import java.util.List;
import java.util.UUID;

public record DoctorDto(
        UUID id,
        String fullName,
        String email,
        String phone,
        Specialization specialization,
        String bio,
        List<DoctorAvailabilityDto> doctorAvailabilities,
        List<AppointmentDto> appointments
) {
}
