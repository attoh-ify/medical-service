package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.DoctorDto;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.DoctorAvailabilityMapper;
import org.health.medical_service.mappers.DoctorMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorMapperImpl implements DoctorMapper {
    private final DoctorAvailabilityMapper doctorAvailabilityMapper;
    private final AppointmentMapper appointmentMapper;

    public DoctorMapperImpl(DoctorAvailabilityMapper doctorAvailabilityMapper, AppointmentMapper appointmentMapper) {
        this.doctorAvailabilityMapper = doctorAvailabilityMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Doctor fromDto(DoctorDto doctorDto) {
        return new Doctor(
                doctorDto.id(),
                doctorDto.fullName(),
                doctorDto.email(),
                doctorDto.phone(),
                doctorDto.specialization(),
                doctorDto.bio(),
                Optional.ofNullable(doctorDto.doctorAvailabilities())
                        .map(doctorAvailabilities -> doctorAvailabilities.stream()
                                .map(doctorAvailabilityMapper::fromDto)
                                .toList()
                        ).orElse(null),
                Optional.ofNullable(doctorDto.appointments())
                        .map(appointments -> appointments.stream()
                                .map(appointmentMapper::fromDto)
                                .toList()
                        ).orElse(null)
        );
    }

    @Override
    public DoctorDto toDto(Doctor doctor) {
        return new DoctorDto(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getSpecialization(),
                doctor.getBio(),
                Optional.ofNullable(doctor.getDoctorAvailabilities())
                        .map(doctorAvailabilities -> doctorAvailabilities.stream()
                                .map(doctorAvailabilityMapper::toDto)
                                .toList())
                        .orElse(null),
                Optional.ofNullable(doctor.getAppointments())
                        .map(appointments -> appointments.stream()
                                .map(appointmentMapper::toDto)
                                .toList())
                        .orElse(null)
        );
    }
}
