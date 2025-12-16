package org.health.medical_service.controllers;

import org.health.medical_service.dto.DoctorAvailabilityDto;
import org.health.medical_service.dto.DoctorDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.DoctorAvailabilityMapper;
import org.health.medical_service.mappers.DoctorMapper;
import org.health.medical_service.services.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/medical-service/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorAvailabilityMapper availabilityMapper;
    private final AppointmentMapper appointmentMapper;

    public DoctorController(
            DoctorService doctorService,
            DoctorMapper doctorMapper,
            DoctorAvailabilityMapper availabilityMapper,
            AppointmentMapper appointmentMapper
    ) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.availabilityMapper = availabilityMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    public ResponseDto register(@RequestBody DoctorDto dto) {
        return new ResponseDto(
                true,
                "Doctor registered",
                doctorMapper.toDto(
                        doctorService.registerDoctor(
                                doctorMapper.fromDto(dto)
                        )
                )
        );
    }

    @PostMapping("/{doctorId}/availabilities")
    public ResponseDto addAvailability(
            @PathVariable UUID doctorId,
            @RequestBody DoctorAvailabilityDto dto
    ) {
        return new ResponseDto(
                true,
                "Availability added",
                availabilityMapper.toDto(
                        doctorService.addAvailableTime(
                                doctorId,
                                availabilityMapper.fromDto(dto)
                        )
                )
        );
    }

    @GetMapping("/{doctorId}/appointments")
    public ResponseDto getAppointments(@PathVariable UUID doctorId) {
        return new ResponseDto(
                true,
                "Doctor appointments",
                doctorService.getAppointments(doctorId)
                        .stream()
                        .map(appointmentMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{doctorId}/appointments/next")
    public ResponseDto getNext(@PathVariable UUID doctorId) {
        return new ResponseDto(
                true,
                "Next appointment",
                appointmentMapper.toDto(
                        doctorService.getNextAppointment(doctorId)
                )
        );
    }
}
