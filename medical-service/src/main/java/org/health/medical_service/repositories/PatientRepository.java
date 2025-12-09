package org.health.medical_service.repositories;

import org.health.medical_service.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhone(String phone);
}
