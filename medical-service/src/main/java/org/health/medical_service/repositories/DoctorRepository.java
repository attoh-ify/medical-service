package org.health.medical_service.repositories;

import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByPhone(String phone);
    @Query("""
            SELECT DISTINCT d FROM Doctor d
            LEFT JOIN d.doctorAvailabilities da
            WHERE d.specialization = :specialization
            AND (:fullName IS NULL OR LOWER(d.fullName) LIKE LOWER (CONCAT('%', :fullName, '%')))
            AND (:day IS NULL OR da.day = :day)
            """)
    List<Doctor> searchDoctors(
            @Param("specialization") Specialization specialization,
            @Param("day") DayOfTheWeek day,
            @Param("fullName") String fullName
    );
}
