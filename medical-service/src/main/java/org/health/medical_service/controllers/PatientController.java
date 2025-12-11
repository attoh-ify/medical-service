package org.health.medical_service.controllers;

import org.health.medical_service.dto.*;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.DayOfTheWeek;
import org.health.medical_service.entities.Patient;
import org.health.medical_service.entities.Specialization;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.PatientMapper;
import org.health.medical_service.services.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseDto registerPatient(@RequestBody PatientDto patientDto) {
        Patient registeredPatient = patientService.registerPatient(patientMapper.fromDto(patientDto));
        return new ResponseDto(
                true,
                "Patient registered successfully",
                patientMapper.toDto(registeredPatient)
        );
    }

    @GetMapping(path = "/{email}")
    public ResponseDto getPatientDetails(@PathVariable("email") String email) {
        Patient patient = patientService.getPatientDetails(email);
        return new ResponseDto(
                true,
                "Patient details fetched successfully",
                patientMapper.toDto(patient)
        );
    }

    @GetMapping(path = "/doctor/available")
    public ResponseDto getAvailableDoctors(
            @RequestParam("specialization") Specialization specialization,
            @RequestParam(value = "day", required = false) DayOfTheWeek day,
            @RequestParam(value = "doctorFullName", required = false) String doctorFullName) {

        List<DayGroupedAvailabilityResponse> response =
                patientService.getAvailableDoctors(specialization, day, doctorFullName);

        return new ResponseDto(
                true,
                "Available doctors fetched successfully",
                response
        );
    }

    @PostMapping(path = "/doctor/appointments")
    public ResponseDto bookAppointment(@RequestBody RequestAppointmentDto requestAppointmentDto) {
        Appointment appointment = patientService.bookAppointment(requestAppointmentDto);
        return new ResponseDto(
                true,
                "Appointment booked successfully",
                appointmentMapper.toDto(appointment)
        );
    }

    @GetMapping(path = "/doctor/appointments/{patient_email}")
    public ResponseDto getAppointments(@PathVariable("patient_email") String patientEmail) {
        List<AppointmentDto> appointments = patientService.getAppointments(patientEmail)
                .stream()
                .map(appointmentMapper::toDto)
                .toList();

        return new ResponseDto(
                true,
                "Appointments fetched successfully",
                appointments
        );
    }

    @GetMapping(path = "/doctor/appointments/{patient_email}/{appointment_id}")
    public ResponseDto getAppointment(
            @PathVariable("patient_email") String patientEmail,
            @PathVariable("appointment_id") UUID appointmentId) {

        Appointment appointment = patientService.getAppointment(patientEmail, appointmentId);
        return new ResponseDto(
                true,
                "Appointment fetched successfully",
                appointmentMapper.toDto(appointment)
        );
    }

    @GetMapping(path = "/doctor/appointments/cancel/{patient_email}/{appointment_id}")
    public ResponseDto cancelAppointment(
            @PathVariable("patient_email") String patientEmail,
            @PathVariable("appointment_id") UUID appointmentId) {

        Appointment appointment = patientService.cancelAppointment(patientEmail, appointmentId);
        return new ResponseDto(
                true,
                "Appointment cancelled successfully",
                appointmentMapper.toDto(appointment)
        );
    }
}
