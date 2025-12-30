package org.health.medical_service.dto;

import org.health.medical_service.entities.Gender;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PatientDto(
        UUID id,
        String fullName,
        String email,
        String phone,
        LocalDate dob,
        Gender gender,
        String address,
        List<AppointmentDto> appointments
) {
}
