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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/medical-service/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorAvailabilityMapper doctorAvailabilityMapper;
    private final AppointmentMapper appointmentMapper;

    public DoctorController(
            DoctorService doctorService,
            DoctorMapper doctorMapper,
            DoctorAvailabilityMapper doctorAvailabilityMapper,
            AppointmentMapper appointmentMapper
    ) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.doctorAvailabilityMapper = doctorAvailabilityMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    public ResponseDto registerDoctor(@RequestBody DoctorDto doctorDto) {
        Doctor registeredDoctor = doctorService.registerDoctor(doctorMapper.fromDto(doctorDto));
        return new ResponseDto(true, "Doctor registered successfully", doctorMapper.toDto(registeredDoctor));
    }

    @PostMapping(path = "/availability/{doctor_id}")
    public ResponseDto addAvailableTime(
            @RequestBody DoctorAvailabilityDto doctorAvailabilityDto,
            @PathVariable("doctor_id") UUID doctorId
    ) {
        DoctorAvailability availability = doctorService.addAvailableTime(
                doctorAvailabilityMapper.fromDto(doctorAvailabilityDto),
                doctorId
        );

        return new ResponseDto(true, "Doctor availability added successfully", doctorAvailabilityMapper.toDto(availability));
    }

    @GetMapping(path = "/appointments/{doctor_email}")
    public ResponseDto getAppointments(@RequestParam("doctor_email") String doctorEmail) {
        List<Appointment> appointments = doctorService.getAppointments(doctorEmail);
        return new ResponseDto(true, "Doctor appointments found successfully", appointments);
    }

    @GetMapping(path = "/appointments/cancel/{doctor_email}/{appointment_id}")
    public ResponseDto cancelAppointment(
            @PathVariable("doctor_email") String doctorEmail,
            @PathVariable("appointment_id") UUID appointmentId
    ) {
        Appointment appointment = doctorService.cancelAppointment(doctorEmail, appointmentId);
        return new ResponseDto(true, "Appointment cancelled successfully", appointmentMapper.toDto(appointment));
    }

    @GetMapping(path = "/appointments/{doctor_email}")
    public ResponseDto getNextAppointment(@PathVariable("doctor_email") String doctorEmail) {
        Appointment appointment = doctorService.getNextAppointment(doctorEmail);
        return new ResponseDto(true, "Next appointment retrieved successfully", appointmentMapper.toDto(appointment));
    }

    @GetMapping(path = "/appointments/{doctor_email}/{appointment_id}")
    public ResponseDto beginAppointment(
            @PathVariable("doctor_email") String doctorEmail,
            @PathVariable("appointment_id") UUID appointmentId
    ) {
        doctorService.beginAppointment(doctorEmail, appointmentId);
        return new ResponseDto(true, "Appointment marked as in-progress", null);
    }

    @PostMapping(path = "/appointments/{doctor_email}/{appointment_id}")
    public ResponseDto completeAppointment(
            @RequestBody RecordAppointmentResult recordAppointmentResult,
            @PathVariable("doctor_email") String doctorEmail,
            @PathVariable("appointment_id") UUID appointmentId
    ) {
        Appointment appointment = doctorService.completeAppointment(recordAppointmentResult, doctorEmail, appointmentId);
        return new ResponseDto(true, "Appointment completed successfully", appointmentMapper.toDto(appointment));
    }

    @PostMapping(path = "/appointments/bookFollowUp")
    public ResponseDto bookFollowUpAppointment(@RequestBody RequestAppointmentDto requestAppointmentDto) {
        Appointment followUpAppointment = doctorService.bookFollowUpAppointment(requestAppointmentDto);
        return new ResponseDto(true, "Follow-up appointment booked successfully", appointmentMapper.toDto(followUpAppointment));
    }
}
