package org.health.medical_service.controllers;

import org.health.medical_service.dto.AppointmentDto;
import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.PatientDto;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.entities.Specialization;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.PatientMapper;
import org.health.medical_service.services.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/medical-service/patient")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper, AppointmentMapper appointmentMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.appointmentMapper = appointmentMapper;
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

    @PostMapping(path = "/doctor/appointments")
    public AppointmentDto bookAppointment(@RequestBody RequestAppointmentDto requestAppointmentDto) {
        Appointment appointment = patientService.bookAppointment(requestAppointmentDto);
        return appointmentMapper.toDto(appointment);
    }

    @GetMapping(path = "/doctor/appointments/{patient_email}")
    public List<AppointmentDto> getAppointments(@PathVariable("patient_email") String patientEmail) {
        return patientService.getAppointments(patientEmail)
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @GetMapping(path = "/doctor/appointments/{patient_email}/{appointment_id}")
    public AppointmentDto getAppointment(@PathVariable("patient_email") String patientEmail, @PathVariable("appointment_id") UUID appointmentId) {
        Appointment appointment = patientService.getAppointment(patientEmail, appointmentId);
        return appointmentMapper.toDto(appointment);
    }
}
