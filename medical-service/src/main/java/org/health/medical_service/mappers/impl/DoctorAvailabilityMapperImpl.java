package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.DoctorAvailabilityDto;
import org.health.medical_service.entities.DoctorAvailability;
import org.health.medical_service.mappers.DoctorAvailabilityMapper;
import org.springframework.stereotype.Component;

@Component
public class DoctorAvailabilityMapperImpl implements DoctorAvailabilityMapper {
    @Override
    public DoctorAvailability fromDto(DoctorAvailabilityDto doctorAvailabilityDto) {
        return new DoctorAvailability(
                doctorAvailabilityDto.id(),
                null,
                doctorAvailabilityDto.day(),
                doctorAvailabilityDto.startTime(),
                doctorAvailabilityDto.endTime()
        );
    }

    @Override
    public DoctorAvailabilityDto toDto(DoctorAvailability doctorAvailability) {
        return new DoctorAvailabilityDto(
                doctorAvailability.getId(),
                doctorAvailability.getDay(),
                doctorAvailability.getStartTime(),
                doctorAvailability.getEndTime()
        );
    }
}
