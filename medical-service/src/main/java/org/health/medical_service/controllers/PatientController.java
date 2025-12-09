package org.health.medical_service.controllers;

import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.mappers.PatientMapper;
import org.health.medical_service.services.PatientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Patient regidteredPatient = patientService.registerPatient(patientMapper.fromDto(patientDto));
        return patientMapper.toDto(regidteredPatient);
    }
}
