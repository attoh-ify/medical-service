package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.PatientMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientMapperImpl implements PatientMapper {
    private final AppointmentMapper appointmentMapper;

    public PatientMapperImpl(AppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Patient fromDto(PatientDto patientDto) {
        return new Patient(
                patientDto.id(),
                patientDto.fullName(),
                patientDto.email(),
                patientDto.phone(),
                patientDto.dob(),
                patientDto.gender(),
                patientDto.address()
        );
    }

    @Override
    public PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDob(),
                patient.getGender(),
                patient.getAddress(),
                Optional.ofNullable(patient.getAppointments())
                        .map(appointments -> appointments.stream()
                                .map(appointmentMapper::toDto)
                                .toList())
                        .orElse(null)
        );
    }
}
