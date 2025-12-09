package org.health.medical_service.mappers;

import org.health.medical_service.dto.DoctorAvailabilityDto;
import org.health.medical_service.entities.DoctorAvailability;

public interface DoctorAvailabilityMapper {
    DoctorAvailability fromDto(DoctorAvailabilityDto doctorAvailabilityDto);
    DoctorAvailabilityDto toDto(DoctorAvailability doctorAvailability);
}
