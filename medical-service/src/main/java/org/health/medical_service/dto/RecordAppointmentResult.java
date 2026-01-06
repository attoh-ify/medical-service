package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object representing the result of an appointment")
public record RecordAppointmentResult(
        @Schema(description = "Summary or notes of the appointment result",
                example = "Patient's condition improved, follow-up in 2 weeks recommended")
        String result
) {}