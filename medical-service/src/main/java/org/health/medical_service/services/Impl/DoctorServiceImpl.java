package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.events.AppointmentCreated;
import org.health.medical_service.events.DoctorCreated;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorAvailabilityRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.DoctorService;
import org.health.medical_service.utils.helpers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher publisher;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorAvailabilityRepository doctorAvailabilityRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository, ApplicationEventPublisher publisher) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.publisher = publisher;
    }

    @Override
    public Doctor registerDoctor(Doctor doctor) {
        validateDoctor(doctor);
        Doctor createdDoctor = doctorRepository.save(doctor);
        publisher.publishEvent(new DoctorCreated(createdDoctor));
        return createdDoctor;
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
                        doctorAvailability.getEndTime()
                )
        );
    }

    @Transactional
    @Override
    public Appointment cancelAppointment(String doctorEmail, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!Objects.equals(appointment.getDoctor().getEmail(), doctorEmail)) {
            throw new IllegalArgumentException("Appointment does not match the doctor");
        }
        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            throw new IllegalArgumentException("Appointment can no longer be updated.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return appointment;
    }

    @Transactional
    @Override
    public Appointment getNextAppointment(String doctorEmail) {
        List<Appointment> appointments = appointmentRepository.findByDoctorEmail(doctorEmail)
                .stream()
                .filter(appointment -> List.of(AppointmentStatus.AWAITING, AppointmentStatus.IN_PROGRESS).contains(appointment.getStatus()))
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .toList();
        return appointments.get(0);
    }

    @Transactional
    @Override
    public void beginAppointment(String doctorEmail, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        Doctor doctor = doctorRepository.findByEmail(doctorEmail).orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        if (appointment.getDoctor() != doctor) {
            throw new IllegalArgumentException("Doctor does not match Appointment.");
        }
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("This appointment does not have status 'IN PROGRESS' and so can not be begun.");
        }
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Its not yet time for the appointment.");
        }
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public Appointment completeAppointment(RecordAppointmentResult dto, String doctorEmail, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        Doctor doctor = doctorRepository.findByEmail(doctorEmail).orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        if (appointment.getDoctor() != doctor) {
            throw new IllegalArgumentException("Doctor does not match Appointment.");
        }
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("You can not record result for this appointment.");
        }
        if (helpers.isBlank(dto.result())) {
            throw new IllegalArgumentException("Result data required to complete appointment.");
        }
        appointment.setResult(dto.result());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public Appointment bookFollowUpAppointment(RequestAppointmentDto dto) {
        Patient patient = patientRepository.findByEmail(dto.patientEmail()).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        Doctor doctor = doctorRepository.findById(dto.doctorId()).orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        Appointment appointment = appointmentRepository.findById(dto.previousAppointmentID()).orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (appointment.getPatient() != patient || appointment.getDoctor() != doctor) {
            throw new IllegalArgumentException("Patient or Doctor does not match Appointment.");
        }
        List<TimeRange> freeRanges = helpers.calculateFreeTimeRanges(doctor, dto.appointmentTime().toLocalDate(), 60);

        helpers.validateAppointmentTime(dto.appointmentTime(), freeRanges);

        Appointment followUpAppointment = appointmentRepository.save(
                new Appointment(
                        null,
                        patient,
                        doctor,
                        dto.appointmentTime(),
                        AppointmentStatus.AWAITING,
                        null,
                        dto.followUpAppointmentType(),
                        null
                )
        );
        appointment.setFollowUpAppointment(followUpAppointment.getId());
        appointmentRepository.save(appointment);
        publisher.publishEvent(new AppointmentCreated(followUpAppointment));
        return followUpAppointment;
    }

    @Transactional
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

    @Transactional
    private Doctor validateDoctorAvailability(DoctorAvailability d, UUID doctorId) {
        if (d.getId() != null) throw new IllegalArgumentException("Doctor availability ID is system generated");
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found.");
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
