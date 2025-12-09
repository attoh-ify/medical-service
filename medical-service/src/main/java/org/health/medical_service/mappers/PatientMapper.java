package org.health.medical_service.mappers;

import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.entities.Patient;

public interface PatientMapper {
    Patient fromDto(PatientDto patientDto);
    PatientDto toDto(Patient patient);
}
