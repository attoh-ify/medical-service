package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.health.medical_service.entities.Appointment;

import java.util.UUID;

@Schema(description = "Detailed result of an appointment, including notes, prescriptions, and follow-up instructions")
public record AppointmentResultDto(
        @Schema(
                description = "Unique identifier of the appointment result",
                example = "b7f1d3c5-6a7d-4f21-8c4e-2e5f9a7d3b1c"
        )
        UUID id,

        @Schema(
                description = "Short summary of the appointment outcome",
                example = "Patient diagnosed with mild hypertension"
        )
        String summary,

        @Schema(
                description = "Detailed notes recorded by the doctor, including observations and test results",
                example = "Blood pressure measured at 140/90 mmHg, recommended lifestyle changes and prescribed medication X"
        )
        String detailedNotes,

        @Schema(
                description = "Indicates if a follow-up appointment is recommended",
                example = "true"
        )
        boolean followUpRecommended,

        @Schema(
                description = "Instructions for the follow-up or next steps for the patient",
                example = "Return in 2 weeks for blood pressure check"
        )
        String followUpInstructions,

        @Schema(
                description = "Prescriptions or therapies provided during the appointment",
                example = "Medication X 10mg once daily for 2 weeks"
        )
        String prescriptions,

        @Schema(
                description = "Lab tests ordered or results noted",
                example = "Blood test: cholesterol 200 mg/dL, glucose 90 mg/dL"
        )
        String labTests,

        @Schema(
                description = "Reference to the associated appointment for which this result was recorded"
        )
        Appointment appointment
) {}
