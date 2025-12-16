package org.health.medical_service.services;

import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;

import java.util.List;
import java.util.UUID;

public interface DoctorService {
    Doctor registerDoctor(Doctor doctor);
    DoctorAvailability addAvailableTime(UUID doctorId, DoctorAvailability availability);
    List<Appointment> getAppointments(UUID doctorId);
    Appointment getNextAppointment(UUID doctorId);
}
