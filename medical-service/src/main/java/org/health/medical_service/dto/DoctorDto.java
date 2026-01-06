package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.Specialization;

import java.util.List;
import java.util.UUID;

@Schema(description = "Represents a doctor's profile, including personal details, specialization, availabilities, and appointments")
public record DoctorDto(
        @Schema(description = "Unique identifier of the doctor", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Full name of the doctor", example = "Dr. Jane Smith")
        String fullName,

        @Schema(description = "Email address of the doctor", example = "jane.smith@example.com")
        String email,

        @Schema(description = "Contact phone number of the doctor", example = "+2348012345678")
        String phone,

        @Schema(description = "Doctor's specialization", example = "CARDIOLOGY")
        Specialization specialization,

        @Schema(description = "Brief biography or profile description of the doctor", example = "Experienced cardiologist with 10 years of practice")
        String bio,

        @Schema(description = "List of availabilities for the doctor")
        List<DoctorAvailabilityDto> doctorAvailabilities,

        @Schema(description = "List of appointments associated with the doctor")
        List<AppointmentDto> appointments
) {}