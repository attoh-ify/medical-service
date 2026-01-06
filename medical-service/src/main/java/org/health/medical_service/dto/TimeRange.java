package org.health.medical_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Represents a time interval with a start and end datetime")
public record TimeRange(
        @Schema(description = "Start of the time range", example = "2026-01-06T09:00:00")
        LocalDateTime start,

        @Schema(description = "End of the time range", example = "2026-01-06T10:00:00")
        LocalDateTime end
) {}