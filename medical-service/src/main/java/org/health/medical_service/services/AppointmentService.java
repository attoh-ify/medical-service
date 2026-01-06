package org.health.medical_service.services;

import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.AppointmentResult;

import java.util.UUID;

public interface AppointmentService {
    Appointment bookAppointment(RequestAppointmentDto dto);
    Appointment cancelAppointment(UUID appointmentId, UUID doctorId);
    void beginAppointment(UUID appointmentId, UUID doctorId);
    Appointment completeAppointment(
            UUID appointmentId,
            UUID doctorId,
            AppointmentResult result
    );
    Appointment bookFollowUp(UUID appointmentId, RequestAppointmentDto appointmentDto);
}
