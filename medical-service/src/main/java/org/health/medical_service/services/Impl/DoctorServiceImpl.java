package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.*;
import org.health.medical_service.events.DoctorCreated;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorAvailabilityRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.services.DoctorService;
import org.health.medical_service.utils.helpers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher publisher;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorAvailabilityRepository doctorAvailabilityRepository, AppointmentRepository appointmentRepository, ApplicationEventPublisher publisher) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.publisher = publisher;
    }

    @Override
    public Doctor registerDoctor(Doctor doctor) {
        validateDoctor(doctor);
        Doctor createdDoctor = doctorRepository.save(doctor);
//        publisher.publishEvent(new DoctorCreated(createdDoctor));
        return createdDoctor;
    }

    @Override
    public DoctorAvailability addAvailableTime(UUID doctorId, DoctorAvailability availability) {
        Doctor doctor = validateDoctorAvailability(availability, doctorId);
        return doctorAvailabilityRepository.save(
                new DoctorAvailability(
                        null,
                        doctor,
                        availability.getDay(),
                        availability.getStartTime(),
                        availability.getEndTime()
                )
        );
    }

    @Override
    public List<Appointment> getAppointments(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public Appointment getNextAppointment(UUID doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AWAITING)
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .toList();
        if (appointments.isEmpty()) return null;
        return appointments.get(0);
    }

    @Transactional
    private void validateDoctor(Doctor d) {
        if (d.getId() != null) throw new IllegalArgumentException("Doctor ID is system generated");
        if (helpers.isBlank(d.getFullName())) throw new IllegalArgumentException("Full name required");
        if (helpers.isBlank(d.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(d.getPhone())) throw new IllegalArgumentException("Phone required");
        if (d.getSpecialization() == null) throw new IllegalArgumentException("Specialization required");
        if (helpers.isBlank(d.getBio())) throw new IllegalArgumentException("Bio required");

        doctorRepository.findByEmail(d.getEmail())
                .ifPresent(doc -> { throw new IllegalArgumentException("This email is already registered to a doctor."); });

        doctorRepository.findByPhone(d.getPhone())
                .ifPresent(doc -> { throw new IllegalArgumentException("This phone is already registered to a doctor."); });
    }

    @Transactional
    private Doctor validateDoctorAvailability(DoctorAvailability availability, UUID doctorId) {
        if (availability.getId() != null) throw new IllegalArgumentException("Doctor availability ID is system generated");

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        if (availability.getDay() == null) throw new IllegalArgumentException("Day of the week is required.");
        if (availability.getStartTime() == null) throw new IllegalArgumentException("Start time is required.");
        if (availability.getEndTime() == null || availability.getEndTime().isBefore(availability.getStartTime()))
            throw new IllegalArgumentException("End time is required and should be after start time");

        List<DoctorAvailability> doctorAvailabilities = doctorAvailabilityRepository.findByDoctorId(doctorId);

        for (DoctorAvailability existing : doctorAvailabilities) {
            if (existing.getDay() == availability.getDay() &&
                    !availability.getEndTime().isBefore(existing.getStartTime()) &&
                    !availability.getStartTime().isAfter(existing.getEndTime())) {
                throw new IllegalArgumentException("Selected time conflicts with existing availability");
            }
        }

        return doctor;
    }
}
