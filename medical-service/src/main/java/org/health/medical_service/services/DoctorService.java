package org.health.medical_service.services;

import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;

import java.util.UUID;

public interface DoctorService {
    Doctor registerDoctor(Doctor doctor);
    DoctorAvailability addAvailableTime(DoctorAvailability doctorAvailability, UUID doctorId);
}
