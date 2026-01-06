package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.Gender;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Data transfer object representing a patient and their profile information")
public record PatientDto(
        @Schema(description = "Unique identifier of the patient", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID id,

        @Schema(description = "Full name of the patient", example = "John Doe")
        String fullName,

        @Schema(description = "Email address of the patient", example = "johndoe@example.com")
        String email,

        @Schema(description = "Phone number of the patient", example = "+2348012345678")
        String phone,

        @Schema(description = "Date of birth of the patient", example = "1990-01-01")
        LocalDate dob,

        @Schema(description = "Gender of the patient", example = "MALE")
        Gender gender,

        @Schema(description = "Home or mailing address of the patient", example = "123 Main St, Lagos, Nigeria")
        String address,

        @Schema(description = "List of appointments associated with the patient")
        List<AppointmentDto> appointments
) {}