package org.health.medical_service.repositories;

import org.health.medical_service.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByPatientEmail(String email);
    List<Appointment> findByDoctorEmail(String email);
}
