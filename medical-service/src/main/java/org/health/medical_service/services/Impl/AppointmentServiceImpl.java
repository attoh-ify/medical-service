package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.exceptions.BadRequestException;
import org.health.medical_service.exceptions.ForbiddenException;
import org.health.medical_service.exceptions.NotFoundException;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.AppointmentService;
import org.health.medical_service.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Logger log =
            LoggerFactory.getLogger(AppointmentServiceImpl.class);

    public AppointmentServiceImpl(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    @Override
    public Appointment bookAppointment(RequestAppointmentDto dto) {
        log.info("Booking appointment patientId={} doctorId={} time={}",
                dto.patientId(), dto.doctorId(), dto.appointmentTime());

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> {
                    log.warn("Patient not found patientId={}", dto.patientId());
                    return new NotFoundException("Patient not found.");
                });

        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> {
                    log.warn("Doctor not found doctorId={}", dto.doctorId());
                    return new NotFoundException("Doctor not found.");
                });

        List<TimeRange> freeRanges =
                helpers.calculateFreeTimeRanges(
                        doctor, dto.appointmentTime().toLocalDate(), 60
                );

        helpers.validateAppointmentTime(dto.appointmentTime(), freeRanges);

        Appointment saved = appointmentRepository.save(
                new Appointment(
                        null,
                        patient,
                        doctor,
                        dto.appointmentTime(),
                        AppointmentStatus.AWAITING,
                        null,
                        AppointmentType.CONSULTATION,
                        null
                )
        );

        log.info("Appointment booked appointmentId={}", saved.getId());
        return saved;
    }

    @Transactional
    @Override
    public Appointment cancelAppointment(UUID appointmentId, UUID doctorId) {
        log.info("Cancelling appointment appointmentId={} doctorId={}",
                appointmentId, doctorId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found appointmentId={}", appointmentId);
                    throw new NotFoundException("Appointment not found");
                });

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            log.warn("Unauthorized cancellation attempt appointmentId={} doctorId={}",
                    appointmentId, doctorId);
            throw new ForbiddenException("Doctor not authorized for this appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            log.warn("Invalid appointment state appointmentId={} status={}",
                    appointmentId, appointment.getStatus());
            throw new BadRequestException("Appointment can no longer be updated.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        log.info("Appointment cancelled appointmentId={}", appointmentId);
        return saved;
    }

    @Transactional
    @Override
    public void beginAppointment(UUID appointmentId, UUID doctorId) {
        log.info("Beginning appointment appointmentId={} doctorId={}",
                appointmentId, doctorId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found appointmentId={}", appointmentId);
                    return new NotFoundException("Appointment not found.");
                });

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            log.warn("Doctor mismatch appointmentId={} doctorId={}",
                    appointmentId, doctorId);
            throw new ForbiddenException("Doctor does not match appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            log.warn("Invalid appointment state appointmentId={} status={}",
                    appointmentId, appointment.getStatus());
            throw new BadRequestException("Appointment is not awaiting");
        }

        if (appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
            log.warn("Appointment time not reached appointmentId={} time={}",
                    appointmentId, appointment.getAppointmentTime());
            throw new BadRequestException("Appointment time has not arrived");
        }

        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointmentRepository.save(appointment);

        log.info("Appointment started appointmentId={}", appointmentId);
    }

    @Transactional
    @Override
    public Appointment completeAppointment(
            UUID appointmentId,
            UUID doctorId,
            RecordAppointmentResult result
    ) {
        log.info("Completing appointment appointmentId={} doctorId={}",
                appointmentId, doctorId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found appointmentId={}", appointmentId);
                    return new NotFoundException("Appointment not found");
                });

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            log.warn("Doctor mismatch appointmentId={} doctorId={}",
                    appointmentId, doctorId);
            throw new ForbiddenException("Doctor does not match appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            log.warn("Invalid appointment state appointmentId={} status={}",
                    appointmentId, appointment.getStatus());
            throw new BadRequestException("Appointment not in progress");
        }

        if (helpers.isBlank(result.result())) {
            log.warn("Empty result appointmentId={}", appointmentId);
            throw new BadRequestException("Result is required");
        }

        appointment.setResult(result.result());
        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment saved = appointmentRepository.save(appointment);

        log.info("Appointment completed appointmentId={}", appointmentId);
        return saved;
    }

    @Transactional
    @Override
    public Appointment bookFollowUp(UUID appointmentId, RequestAppointmentDto appointmentDto) {
        log.info("Booking follow-up appointment previousAppointmentId={}",
                appointmentId);

        Appointment previous = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Previous appointment not found appointmentId={}",
                            appointmentId);
                    return new NotFoundException("Appointment not found.");
                });

        if (previous.getStatus() != AppointmentStatus.COMPLETED) {
            log.warn("Follow-up requested for non-completed appointment appointmentId={} status={}",
                    appointmentId, previous.getStatus());
            throw new BadRequestException("Follow-up requires completed appointment");
        }

        Patient patient = patientRepository.findById(appointmentDto.patientId())
                .orElseThrow(() -> {
                    log.warn("Patient not found patientId={}",
                            appointmentDto.patientId());
                    return new NotFoundException("Patient not found.");
                });

        Doctor doctor = doctorRepository.findById(appointmentDto.doctorId())
                .orElseThrow(() -> {
                    log.warn("Doctor not found doctorId={}",
                            appointmentDto.doctorId());
                    return new NotFoundException("Doctor not found.");
                });

        if (!previous.getPatient().getId().equals(patient.getId())
                || !previous.getDoctor().getId().equals(doctor.getId())) {
            log.warn("Appointment mismatch follow-up previousAppointmentId={}",
                    appointmentId);
            throw new BadRequestException("Appointment mismatch");
        }

        List<TimeRange> freeRanges =
                helpers.calculateFreeTimeRanges(
                        doctor, appointmentDto.appointmentTime().toLocalDate(), 60
                );

        helpers.validateAppointmentTime(
                appointmentDto.appointmentTime(), freeRanges
        );

        Appointment followUpAppointment = appointmentRepository.save(
                new Appointment(
                        null,
                        patient,
                        doctor,
                        appointmentDto.appointmentTime(),
                        AppointmentStatus.AWAITING,
                        null,
                        appointmentDto.followUpAppointmentType(),
                        null
                )
        );

        previous.setFollowUpAppointment(followUpAppointment.getId());
        appointmentRepository.save(previous);

        log.info("Follow-up appointment booked followUpAppointmentId={} previousAppointmentId={}",
                followUpAppointment.getId(), appointmentId);

        return followUpAppointment;
    }
}
