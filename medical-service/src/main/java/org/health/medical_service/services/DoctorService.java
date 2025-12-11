package org.health.medical_service.services;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;

import java.util.List;
import java.util.UUID;

public interface DoctorService {
    Doctor registerDoctor(Doctor doctor);
    DoctorAvailability addAvailableTime(DoctorAvailability doctorAvailability, UUID doctorId);
    List<Appointment> getAppointments(String doctorEmail);
    Appointment cancelAppointment(String doctorEmail, UUID appointmentId);
    Appointment getNextAppointment(String doctorEmail);
    void beginAppointment(String doctorEmail, UUID appointmentId);
    Appointment completeAppointment(RecordAppointmentResult recordAppointmentResult, String doctorEmail, UUID appointmentId);
    Appointment bookFollowUpAppointment(RequestAppointmentDto requestAppointmentDto);
}
