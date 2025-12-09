package org.health.medical_service.mappers;

import org.health.medical_service.dto.DoctorDto;
import org.health.medical_service.entities.Doctor;

public interface DoctorMapper {
    Doctor fromDto(DoctorDto doctorDto);
    DoctorDto toDto(Doctor doctor);
}
