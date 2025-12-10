package org.health.medical_service.controllers;

import org.health.medical_service.dto.DoctorAvailabilityDto;
import org.health.medical_service.dto.DoctorDto;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;
import org.health.medical_service.mappers.DoctorAvailabilityMapper;
import org.health.medical_service.mappers.DoctorMapper;
import org.health.medical_service.services.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/medical-service/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorAvailabilityMapper doctorAvailabilityMapper;

    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, DoctorAvailabilityMapper doctorAvailabilityMapper) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.doctorAvailabilityMapper = doctorAvailabilityMapper;
    }

    @PostMapping
    public DoctorDto registerDoctor(@RequestBody DoctorDto doctorDto) {
        Doctor registeredDoctor = doctorService.registerDoctor(doctorMapper.fromDto(doctorDto));
        return doctorMapper.toDto(registeredDoctor);
    }

    @PostMapping(path = {"/availability/{doctor_id}"})
    public DoctorAvailabilityDto addAvailableTime(@RequestBody DoctorAvailabilityDto doctorAvailabilityDto, @PathVariable("doctor_id") UUID doctorId) {
        DoctorAvailability doctorAvailability = doctorService.addAvailableTime(doctorAvailabilityMapper.fromDto(doctorAvailabilityDto), doctorId);
        return doctorAvailabilityMapper.toDto(doctorAvailability);
    }
}
