package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;
import org.health.medical_service.repositories.DoctorAvailabilityRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.services.DoctorService;
import org.health.medical_service.utils.helpers;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    @Override
    public Doctor registerDoctor(Doctor doctor) {
        validateDoctor(doctor);
        return doctorRepository.save(doctor);
    }

    @Override
    public DoctorAvailability addAvailableTime(DoctorAvailability doctorAvailability, UUID doctorId) {
        Doctor doctor = validateDoctorAvailability(doctorAvailability, doctorId);
        return doctorAvailabilityRepository.save(
                new DoctorAvailability(
                        null,
                        doctor,
                        doctorAvailability.getDay(),
                        doctorAvailability.getStartTime(),
                        doctorAvailability.getEndTime(),
                        false
                )
        );
    }

    private void validateDoctor(Doctor d) {
        if (d.getId() != null) throw new IllegalArgumentException("Doctor ID is system generated");
        if (helpers.isBlank(d.getFullName())) throw new IllegalArgumentException("Full name required");
        if (helpers.isBlank(d.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(d.getPhone())) throw new IllegalArgumentException("Phone required");
        if (d.getSpecialization() == null) throw new IllegalArgumentException("Specialization required");
        if (helpers.isBlank(d.getBio())) throw new IllegalArgumentException("Bio required");
        Optional<Doctor> emailExists = doctorRepository.findByEmail(d.getEmail());
        if (emailExists.isPresent()) {
            throw new IllegalArgumentException("This email is already registered to a doctor.");
        }
        Optional<Doctor> phoneExists = doctorRepository.findByPhone(d.getPhone());
        if (phoneExists.isPresent()) {
            throw new IllegalArgumentException("This phone is already registered to a doctor.");
        }
    }

    private Doctor validateDoctorAvailability(DoctorAvailability d, UUID doctorId) {
        if (d.getId() != null) throw new IllegalArgumentException("Doctor availability ID is system generated");
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("This phone is already registered to a doctor.");
        }
        if (d.getDay() == null) throw new IllegalArgumentException("Day of the week is required.");
        if (d.getStartTime() == null) throw new IllegalArgumentException("Start time is required.");
        if (d.getEndTime() == null || d.getEndTime().isBefore(d.getStartTime())) throw new IllegalArgumentException("End time is required and should be after start time");
        List<DoctorAvailability> doctorAvailabilities = doctorAvailabilityRepository.findAll();
        for (DoctorAvailability doctorAvailability : doctorAvailabilities) {
            LocalTime startTime = doctorAvailability.getStartTime();
            LocalTime endTime = doctorAvailability.getEndTime();
            DayOfTheWeek day = doctorAvailability.getDay();
            if (day == d.getDay() && d.getStartTime().isAfter(startTime) && d.getStartTime().isBefore(endTime) || day == d.getDay() && d.getStartTime() == startTime) {
                throw new IllegalArgumentException("The start time selected is conflicting with an existing available time");
            }
            if (day == d.getDay() && d.getEndTime().isAfter(startTime) && d.getEndTime().isBefore(endTime) || day == d.getDay() && d.getEndTime() == endTime) {
                throw new IllegalArgumentException("The end time selected is conflicting with an existing available time");
            }
        }
        return doctor.get();
    }
}
