package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.AppointmentDto;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.mappers.AppointmentMapper;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapperImpl implements AppointmentMapper {
    @Override
    public Appointment fromDto(AppointmentDto appointmentDto) {
        return new Appointment(
                appointmentDto.id(),
                null,
                null,
                appointmentDto.appointmentTime(),
                appointmentDto.status(),
                appointmentDto.result(),
                appointmentDto.appointmentType(),
                appointmentDto.followUpAppointmentId()
        );
    }

    @Override
    public AppointmentDto toDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getResult(),
                appointment.getAppointmentType(),
                appointment.getFollowUpAppointmentId()
        );
    }
}
