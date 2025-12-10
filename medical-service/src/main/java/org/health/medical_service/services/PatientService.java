package org.health.medical_service.services;

import org.health.medical_service.entities.Patient;

import java.util.Optional;

public interface PatientService {
    Patient registerPatient(Patient patient);
    Optional<Patient> getPatientDetails(String email);
}
