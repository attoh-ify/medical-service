package org.health.medical_service.services.Impl;

import org.health.medical_service.entities.*;
import org.health.medical_service.exceptions.BadRequestException;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorAvailabilityRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.UserRepository;
import org.health.medical_service.services.DoctorService;
import org.health.medical_service.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(DoctorServiceImpl.class);

    public DoctorServiceImpl(
            DoctorRepository doctorRepository,
            DoctorAvailabilityRepository doctorAvailabilityRepository,
            AppointmentRepository appointmentRepository, UserRepository userRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Doctor registerDoctor(Doctor doctor) {
        log.info("Registering doctor email={} specialization={}",
                doctor.getEmail(), doctor.getSpecialization());

        User user = userRepository.findByEmail(doctor.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found email={}. Doctors can only be created for already registered users", doctor.getEmail());
                    return new BadRequestException("User not found");
                });

        user.setRole(UserRole.DOCTOR);
        userRepository.save(user);

        validateDoctor(doctor);

        Doctor saved = doctorRepository.save(
                new Doctor(
                        null,
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone(),
                    doctor.getSpecialization(),
                    doctor.getBio(),
                    null,
                    null
                )
        );

        log.info("Doctor registered successfully doctorId={}", saved.getId());
        return saved;
    }

    @Override
    public DoctorAvailability addAvailableTime(UUID doctorId, DoctorAvailability availability) {
        log.info(
                "Adding doctor availability doctorId={} day={} startTime={} endTime={}",
                doctorId,
                availability.getDay(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        Doctor doctor = validateDoctorAvailability(availability, doctorId);

        DoctorAvailability saved = doctorAvailabilityRepository.save(
                new DoctorAvailability(
                        null,
                        doctor,
                        availability.getDay(),
                        availability.getStartTime(),
                        availability.getEndTime()
                )
        );

        log.info(
                "Doctor availability added successfully doctorId={} availabilityId={}",
                doctorId,
                saved.getId()
        );

        return saved;
    }

    @Override
    public List<Appointment> getAppointments(UUID doctorId) {
        log.info("Fetching appointments for doctor doctorId={}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public Appointment getNextAppointment(UUID doctorId) {
        log.info("Fetching next appointment for doctor doctorId={}", doctorId);

        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AWAITING)
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .toList();

        if (appointments.isEmpty()) {
            log.info("No upcoming appointments found doctorId={}", doctorId);
            return null;
        }

        Appointment next = appointments.get(0);

        log.info(
                "Next appointment found doctorId={} appointmentId={} time={}",
                doctorId,
                next.getId(),
                next.getAppointmentTime()
        );

        return next;
    }

    @Transactional
    private void validateDoctor(Doctor d) {
        if (d.getId() != null)
            throw new BadRequestException("Doctor ID is system generated");

        if (d.getSpecialization() == null)
            throw new BadRequestException("Specialization required");

        if (helpers.isBlank(d.getBio()))
            throw new BadRequestException("Bio required");

        doctorRepository.findByEmail(d.getEmail())
                .ifPresent(existing -> {
                    log.warn("Doctor email already registered email={}", d.getEmail());
                    throw new BadRequestException(
                            "This email is already registered to a doctor."
                    );
                });

        doctorRepository.findByPhone(d.getPhone())
                .ifPresent(existing -> {
                    log.warn("Doctor phone already registered phone={}", d.getPhone());
                    throw new BadRequestException(
                            "This phone is already registered to a doctor."
                    );
                });
    }

    @Transactional
    private Doctor validateDoctorAvailability(
            DoctorAvailability availability,
            UUID doctorId
    ) {

        if (availability.getId() != null)
            throw new BadRequestException(
                    "Doctor availability ID is system generated"
            );

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.warn("Doctor not found doctorId={}", doctorId);
                    return new BadRequestException("Doctor not found.");
                });

        if (availability.getDay() == null)
            throw new BadRequestException("Day of the week is required.");

        if (availability.getStartTime() == null)
            throw new BadRequestException("Start time is required.");

        if (availability.getEndTime() == null
                || availability.getEndTime().isBefore(availability.getStartTime()))
            throw new BadRequestException(
                    "End time is required and should be after start time"
            );

        List<DoctorAvailability> doctorAvailabilities =
                doctorAvailabilityRepository.findByDoctorId(doctorId);

        for (DoctorAvailability existing : doctorAvailabilities) {
            if (existing.getDay() == availability.getDay()
                    && !availability.getEndTime().isBefore(existing.getStartTime())
                    && !availability.getStartTime().isAfter(existing.getEndTime())) {

                log.warn(
                        "Availability conflict doctorId={} day={} newStart={} newEnd={} existingStart={} existingEnd={}",
                        doctorId,
                        availability.getDay(),
                        availability.getStartTime(),
                        availability.getEndTime(),
                        existing.getStartTime(),
                        existing.getEndTime()
                );

                throw new BadRequestException(
                        "Selected time conflicts with existing availability"
                );
            }
        }

        return doctor;
    }
}

