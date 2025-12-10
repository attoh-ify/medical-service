package org.health.medical_service.controllers;

import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.entities.Specialization;
import org.health.medical_service.mappers.PatientMapper;
import org.health.medical_service.services.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/medical-service/patient")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @PostMapping
    public PatientDto registerPatient(@RequestBody PatientDto patientDto) {
        Patient registeredPatient = patientService.registerPatient(patientMapper.fromDto(patientDto));
        return patientMapper.toDto(registeredPatient);
    }

    @GetMapping(path = "/{email}")
    public Optional<PatientDto> getPatientDetails(@PathVariable("email") String email) {
        return patientService.getPatientDetails(email).map(patientMapper::toDto);
    }

    @GetMapping(path = "/doctor/available")
    public List<DayGroupedAvailabilityResponse> getAvailableDoctors(
            @RequestParam("specialization") Specialization specialization,
            @RequestParam(value = "day", required = false) DayOfTheWeek day,
            @RequestParam(value = "doctorFullName", required = false) String doctorFullName) {
        return patientService.getAvailableDoctors(specialization, day, doctorFullName);
    }
}
