package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.AppointmentResultDto;
import org.health.medical_service.entities.AppointmentResult;
import org.health.medical_service.mappers.AppointmentResultMapper;
import org.springframework.stereotype.Component;

@Component
public class AppointmentResultMapperImpl implements AppointmentResultMapper {
    @Override
    public AppointmentResult fromDto(AppointmentResultDto appointmentResultDto) {
        return new AppointmentResult(
                appointmentResultDto.id(),
                appointmentResultDto.summary(),
                appointmentResultDto.detailedNotes(),
                appointmentResultDto.followUpRecommended(),
                appointmentResultDto.followUpInstructions(),
                appointmentResultDto.prescriptions(),
                appointmentResultDto.labTests(),
                appointmentResultDto.appointment()
        );
    }

    @Override
    public AppointmentResultDto toDto(AppointmentResult appointmentResult) {
        return new AppointmentResultDto(
                appointmentResult.getId(),
                appointmentResult.getSummary(),
                appointmentResult.getDetailedNotes(),
                appointmentResult.isFollowUpRecommended(),
                appointmentResult.getFollowUpInstructions(),
                appointmentResult.getPrescriptions(),
                appointmentResult.getLabTests(),
                appointmentResult.getAppointment()
        );
    }
}
