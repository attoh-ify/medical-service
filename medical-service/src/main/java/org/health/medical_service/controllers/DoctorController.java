package org.health.medical_service.controllers;

import org.health.medical_service.dto.*;
import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;
import org.health.medical_service.mappers.AppointmentMapper;
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
    private final AppointmentMapper appointmentMapper;

    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, DoctorAvailabilityMapper doctorAvailabilityMapper, AppointmentMapper appointmentMapper) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.doctorAvailabilityMapper = doctorAvailabilityMapper;
        this.appointmentMapper = appointmentMapper;
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

    @GetMapping(path = "/appointments/cancel/{doctor_email}/{appointment_id}")
    public AppointmentDto cancelAppointment(@PathVariable("doctor_email") String doctorEmail, @PathVariable("appointment_id") UUID appointmentId) {
        Appointment appointment = doctorService.cancelAppointment(doctorEmail, appointmentId);
        return appointmentMapper.toDto(appointment);
    }

    @GetMapping(path = "/appointments/{doctor_email}")
    public AppointmentDto getNextAppointment(@PathVariable("doctor_email") String doctorEmail) {
        Appointment appointment = doctorService.getNextAppointment(doctorEmail);
        return appointmentMapper.toDto(appointment);
    }

    @GetMapping(path = "/appointments/{doctor_email}/{appointment_id}")
    public void beginAppointment(@PathVariable("doctor_email") String doctorEmail, @PathVariable("appointment_id") UUID appointmentId) {
        doctorService.beginAppointment(doctorEmail, appointmentId);
    }

    @PostMapping(path = "/appointments/{doctor_email}/{appointment_id}")
    public AppointmentDto completeAppointment(@RequestBody RecordAppointmentResult recordAppointmentResult, @PathVariable("doctor_email") String doctorEmail, @PathVariable("appointment_id") UUID appointmentId) {
        Appointment appointment = doctorService.completeAppointment(recordAppointmentResult, doctorEmail, appointmentId);
        return appointmentMapper.toDto(appointment);
    }

    @PostMapping(path = "/doctor/appointments/bookFollowUp")
    public AppointmentDto bookFollowUpAppointment(@RequestBody RequestAppointmentDto requestAppointmentDto) {
        Appointment followUpAppointment = doctorService.bookFollowUpAppointment(requestAppointmentDto);
        return appointmentMapper.toDto(followUpAppointment);
    }
}
