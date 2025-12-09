package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.Gender;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.PatientService;
import org.springframework.stereotype.Service;

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
        validate(patient);
        return patientRepository.save(patient);
    }

    private void validate(Patient p) {
        if (p.getId() != null) throw new IllegalArgumentException("Patient ID is system generated");
        if (isBlank(p.getFullName())) throw new IllegalArgumentException("Full name required");
        if (isBlank(p.getEmail())) throw new IllegalArgumentException("Email required");
        if (isBlank(p.getPhone())) throw new IllegalArgumentException("Phone required");
        if (p.getDob() == null || p.getDob().isAfter(LocalDate.now())) throw new IllegalArgumentException("Invalid DOB");
        if (p.getGender() == null) throw new IllegalArgumentException("Gender required");
        if (isBlank(p.getAddress())) throw new IllegalArgumentException("Address required");
        Optional<Patient> emailExists = patientRepository.findByEmail(p.getEmail());
        if (emailExists.isPresent()) {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        }
        Optional<Patient> phoneExists = patientRepository.findByPhone(p.getPhone());
        if (phoneExists.isPresent()) {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
