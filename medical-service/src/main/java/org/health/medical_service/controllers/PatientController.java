package org.health.medical_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Patients",
        description = "Patient registration, profile management, and appointment access"
)
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
    @Operation(
            summary = "Register a new patient",
            description = "Creates a new patient profile in the system"
    )
    public ResponseDto register(
            @RequestBody PatientDto dto
    ) {
        return new ResponseDto(
                "Patient registered",
                patientMapper.toDto(
                        patientService.registerPatient(
                                patientMapper.fromDto(dto)
                        )
                )
        );
    }

    @GetMapping("/{email}")
    @Operation(
            summary = "Get patient details",
            description = "Retrieves a patient's profile using their email address"
    )
    public ResponseDto getDetails(
            @Parameter(
                    description = "Patient email address",
                    example = "john.doe@email.com",
                    required = true
            )
            @PathVariable String email
    ) {
        return new ResponseDto(
                "Patient fetched",
                patientMapper.toDto(
                        patientService.getPatientDetails(email)
                )
        );
    }

    @GetMapping("/{patientId}/appointments")
    @Operation(
            summary = "Get patient appointments",
            description = "Retrieves all appointments booked by a patient"
    )
    public ResponseDto getAppointments(
            @Parameter(
                    description = "Unique identifier of the patient",
                    example = "8c6a3df2-2b4f-4d35-9c3a-4f5b6d7e8a91",
                    required = true
            )
            @PathVariable UUID patientId
    ) {
        return new ResponseDto(
                "Appointments fetched",
                patientService.getAppointments(patientId)
                        .stream()
                        .map(appointmentMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{patientId}/appointments/{appointmentId}/trail")
    @Operation(
            summary = "Get appointment history trail",
            description = "Retrieves the full lifecycle history of an appointment"
    )
    public ResponseDto getTrail(
            @Parameter(
                    description = "Unique identifier of the patient",
                    required = true
            )
            @PathVariable UUID patientId,

            @Parameter(
                    description = "Unique identifier of the appointment",
                    required = true
            )
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
    @Operation(
            summary = "Discover available doctors",
            description = "Searches for doctors by specialization, availability day, or name"
    )
    public ResponseDto discoverDoctors(

            @Parameter(
                    description = "Medical specialization",
                    example = "CARDIOLOGY",
                    required = true
            )
            @RequestParam Specialization specialization,

            @Parameter(
                    description = "Preferred day of availability",
                    example = "MONDAY"
            )
            @RequestParam(required = false) DayOfTheWeek day,

            @Parameter(
                    description = "Doctor full name (partial match supported)",
                    example = "Jane"
            )
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
