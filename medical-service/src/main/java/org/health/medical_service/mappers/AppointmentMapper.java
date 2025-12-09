package org.health.medical_service.mappers;

import org.health.medical_service.dto.AppointmentDto;
import org.health.medical_service.entities.Appointment;

public interface AppointmentMapper {
    Appointment fromDto(AppointmentDto appointmentDto);
    AppointmentDto toDto(Appointment appointment);
}
