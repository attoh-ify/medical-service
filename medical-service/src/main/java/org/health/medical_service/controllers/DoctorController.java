package org.health.medical_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.health.medical_service.dto.DoctorAvailabilityDto;
import org.health.medical_service.dto.DoctorDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.mappers.DoctorAvailabilityMapper;
import org.health.medical_service.mappers.DoctorMapper;
import org.health.medical_service.services.DoctorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@Tag(
        name = "Doctors",
        description = "Doctor registration, availability management, and appointment access"
)
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
    @Operation(
            summary = "Register a new doctor",
            description = "Creates a new doctor profile in the system"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto register(
            @RequestBody DoctorDto dto
    ) {
        return new ResponseDto(
                "Doctor registered",
                doctorMapper.toDto(
                        doctorService.registerDoctor(
                                doctorMapper.fromDto(dto)
                        )
                )
        );
    }

    @PostMapping("/{doctorId}/availabilities")
    @Operation(
            summary = "Add doctor availability",
            description = "Adds a new available time slot for a doctor"
    )
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseDto addAvailability(
            @Parameter(
                    description = "Unique identifier of the doctor",
                    example = "d2b7a15e-98f1-4e02-bc7a-5c3c2a3f4b91",
                    required = true
            )
            @PathVariable UUID doctorId,

            @RequestBody DoctorAvailabilityDto dto
    ) {
        return new ResponseDto(
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
    @Operation(
            summary = "Get all doctor appointments",
            description = "Retrieves all appointments assigned to a doctor"
    )
    public ResponseDto getAppointments(
            @Parameter(
                    description = "Unique identifier of the doctor",
                    example = "d2b7a15e-98f1-4e02-bc7a-5c3c2a3f4b91",
                    required = true
            )
            @PathVariable UUID doctorId
    ) {
        return new ResponseDto(
                "Doctor appointments",
                doctorService.getAppointments(doctorId)
                        .stream()
                        .map(appointmentMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{doctorId}/appointments/next")
    @Operation(
            summary = "Get next appointment",
            description = "Retrieves the next upcoming appointment for a doctor"
    )
    public ResponseDto getNext(
            @Parameter(
                    description = "Unique identifier of the doctor",
                    example = "d2b7a15e-98f1-4e02-bc7a-5c3c2a3f4b91",
                    required = true
            )
            @PathVariable UUID doctorId
    ) {
        return new ResponseDto(
                "Next appointment",
                appointmentMapper.toDto(
                        doctorService.getNextAppointment(doctorId)
                )
        );
    }
}
