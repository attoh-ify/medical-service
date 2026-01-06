package org.health.medical_service.mappers;

import org.health.medical_service.dto.AppointmentResultDto;
import org.health.medical_service.entities.AppointmentResult;

public interface AppointmentResultMapper {
    AppointmentResult fromDto(AppointmentResultDto appointmentResultDto);
    AppointmentResultDto toDto(AppointmentResult appointmentResult);
}
