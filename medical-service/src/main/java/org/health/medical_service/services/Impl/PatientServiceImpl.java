package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.Patient;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.PatientService;
import org.springframework.stereotype.Service;
import org.health.medical_service.utils.helpers;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    @Override
    public Optional<Patient> getPatientDetails(String email) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient with this email is not registered with us");
        }
        return patient;
    }

    private void validatePatient(Patient p) {
        if (p.getId() != null) throw new IllegalArgumentException("Patient ID is system generated");
        if (helpers.isBlank(p.getFullName())) throw new IllegalArgumentException("Full name required");
        if (helpers.isBlank(p.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(p.getPhone())) throw new IllegalArgumentException("Phone required");
        if (p.getDob() == null || p.getDob().isAfter(LocalDate.now())) throw new IllegalArgumentException("Invalid DOB");
        if (p.getGender() == null) throw new IllegalArgumentException("Gender required");
        if (helpers.isBlank(p.getAddress())) throw new IllegalArgumentException("Address required");
        Optional<Patient> emailExists = patientRepository.findByEmail(p.getEmail());
        if (emailExists.isPresent()) {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        }
        Optional<Patient> phoneExists = patientRepository.findByPhone(p.getPhone());
        if (phoneExists.isPresent()) {
            throw new IllegalArgumentException("This phone is already registered to a patient.");
        }
    }
}
