package org.health.medical_service.controllers;

import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Specialization;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.PatientMapper;
import org.health.medical_service.services.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    public PatientController(
            PatientService patientService,
            PatientMapper patientMapper,
            AppointmentMapper appointmentMapper
    ) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    public ResponseDto register(@RequestBody PatientDto dto) {
        return new ResponseDto(
                "Patient registered",
                patientMapper.toDto(
                        patientService.registerPatient(
                                patientMapper.fromDto(dto)
                        )
                )
        );
    }

    @GetMapping("/{patientId}")
    public ResponseDto getDetails(@PathVariable UUID patientId) {
        return new ResponseDto(
                "Patient fetched",
                patientMapper.toDto(
                        patientService.getPatientDetails(patientId)
                )
        );
    }

    @GetMapping("/{patientId}/appointments")
    public ResponseDto getAppointments(@PathVariable UUID patientId) {
        return new ResponseDto(
                "Appointments fetched",
                patientService.getAppointments(patientId)
                        .stream()
                        .map(appointmentMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{patientId}/appointments/{appointmentId}/trail")
    public ResponseDto getTrail(
            @PathVariable UUID patientId,
            @PathVariable UUID appointmentId
    ) {
        return new ResponseDto(
                "Appointment trail",
                patientService.getAppointmentTrail(patientId, appointmentId)
                        .stream()
                        .map(appointmentMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/doctors")
    public ResponseDto discoverDoctors(
            @RequestParam Specialization specialization,
            @RequestParam(required = false) DayOfTheWeek day,
            @RequestParam(required = false) String doctorFullName
    ) {
        return new ResponseDto(
                "Doctors fetched",
                patientService.getAvailableDoctors(
                        specialization,
                        day,
                        doctorFullName
                )
        );
    }
}
