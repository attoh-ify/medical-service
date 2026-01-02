package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.AppointmentService;
import org.health.medical_service.utils.helpers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(PatientRepository patientRepository, DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    @Override
    public Appointment bookAppointment(RequestAppointmentDto dto) {
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        List<TimeRange> freeRanges =
                helpers.calculateFreeTimeRanges(
                        doctor, dto.appointmentTime().toLocalDate(), 60
                );

        helpers.validateAppointmentTime(dto.appointmentTime(), freeRanges);

        return appointmentRepository.save(
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
    }

    @Transactional
    @Override
    public Appointment cancelAppointment(UUID appointmentId, UUID doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        boolean isDoctor = appointment.getDoctor().getId().equals(doctorId);
        if (!isDoctor) {
            throw new IllegalArgumentException("Doctor not authorized for this appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            throw new IllegalArgumentException("Appointment can no longer be updated.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public void beginAppointment(UUID appointmentId, UUID doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Doctor does not match appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            throw new IllegalArgumentException("Appointment is not awaiting");
        }

        if (appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time has not arrived");
        }
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public Appointment completeAppointment(
            UUID appointmentId,
            UUID doctorId,
            RecordAppointmentResult result
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Doctor does not match appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Appointment not in progress");
        }

        if (helpers.isBlank(result.result())) {
            throw new IllegalArgumentException("Result is required");
        }

        appointment.setResult(result.result());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public Appointment bookFollowUp(UUID appointmentId, RequestAppointmentDto appointmentDto) {
        Appointment previous = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (previous.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Follow-up requires completed appointment");
        }

        Patient patient = patientRepository.findById(appointmentDto.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        Doctor doctor = doctorRepository.findById(appointmentDto.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        if (!previous.getPatient().getId().equals(patient.getId())
                || !previous.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("Appointment mismatch");
        }

        List<TimeRange> freeRanges =
                helpers.calculateFreeTimeRanges(
                        doctor, appointmentDto.appointmentTime().toLocalDate(), 60
                );

        helpers.validateAppointmentTime(appointmentDto.appointmentTime(), freeRanges);

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
//        publisher.publishEvent(new AppointmentCreated(followUpAppointment));
        return followUpAppointment;
    }
}
