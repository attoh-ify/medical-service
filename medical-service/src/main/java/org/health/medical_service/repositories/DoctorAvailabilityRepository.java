package org.health.medical_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorRepository, UUID> {
}
