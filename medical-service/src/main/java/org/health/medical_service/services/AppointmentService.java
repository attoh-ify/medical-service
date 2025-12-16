package org.health.medical_service.services;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.entities.Appointment;

import java.util.UUID;

public interface AppointmentService {
    Appointment bookAppointment(RequestAppointmentDto dto);
    Appointment cancelAppointment(UUID appointmentId, UUID actorId);
    void beginAppointment(UUID appointmentId, UUID doctorId);
    Appointment completeAppointment(
            UUID appointmentId,
            UUID doctorId,
            RecordAppointmentResult result
    );
    Appointment bookFollowUp(UUID appointmentId, RequestAppointmentDto appointmentDto);
}
