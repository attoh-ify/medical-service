package org.health.medical_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.services.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@Tag(
        name = "Appointments",
        description = "Endpoints for booking, managing, and completing medical appointments"
)
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(
            AppointmentService appointmentService,
            AppointmentMapper appointmentMapper
    ) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    @Operation(
            summary = "Book a new appointment",
            description = "Creates a new medical appointment for a patient with a specified doctor and time slot."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment booked"),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data"),
            @ApiResponse(responseCode = "409", description = "Appointment slot already taken")
    })
    public ResponseDto book(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Appointment booking details",
                    required = true
            )
            @RequestBody RequestAppointmentDto dto
    ) {
        return new ResponseDto(
                "Appointment booked",
                appointmentMapper.toDto(
                        appointmentService.bookAppointment(dto)
                )
        );
    }

    @PatchMapping("/{appointmentId}/cancel/{doctorId}")
    @Operation(
            summary = "Cancel an appointment",
            description = "Cancels an existing appointment. Only the assigned doctor is allowed to cancel it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment cancelled"),
            @ApiResponse(responseCode = "403", description = "Doctor not authorized to cancel this appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseDto cancel(
            @Parameter(
                    description = "Unique identifier of the appointment",
                    example = "c3b8f4e2-1a6d-4b22-9f4c-1e5c2f9a7d11"
            )
            @PathVariable UUID appointmentId,

            @Parameter(
                    description = "Unique identifier of the doctor cancelling the appointment",
                    example = "b1a9f7d3-8c22-4b92-a8b1-12d98f43c821"
            )
            @PathVariable UUID doctorId
    ) {
        return new ResponseDto(
                "Appointment cancelled",
                appointmentMapper.toDto(
                        appointmentService.cancelAppointment(appointmentId, doctorId)
                )
        );
    }

    @PatchMapping("/{appointmentId}/begin")
    @Operation(
            summary = "Begin an appointment",
            description = "Marks an appointment as in progress. Can only be started by the assigned doctor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment successfully started"),
            @ApiResponse(responseCode = "409", description = "Appointment already started or already completed"),
            @ApiResponse(responseCode = "403", description = "Doctor not authorized to begin this appointment")
    })
    public ResponseDto begin(
            @Parameter(
                    description = "Unique identifier of the appointment",
                    example = "c3b8f4e2-1a6d-4b22-9f4c-1e5c2f9a7d11"
            )
            @PathVariable UUID appointmentId,

            @Parameter(
                    description = "Unique identifier of the doctor starting the appointment",
                    example = "b1a9f7d3-8c22-4b92-a8b1-12d98f43c821"
            )
            @RequestParam UUID doctorId
    ) {
        appointmentService.beginAppointment(appointmentId, doctorId);
        return new ResponseDto("Appointment started", null);
    }

    @PatchMapping("/{appointmentId}/complete/{doctorId}")
    @Operation(
            summary = "Complete an appointment",
            description = "Completes an appointment and records the medical consultation result."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment successfully completed"),
            @ApiResponse(responseCode = "400", description = "Invalid consultation result data"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "403", description = "Doctor not authorized to complete this appointment")
    })
    public ResponseDto complete(
            @Parameter(
                    description = "Unique identifier of the appointment",
                    example = "c3b8f4e2-1a6d-4b22-9f4c-1e5c2f9a7d11"
            )
            @PathVariable UUID appointmentId,

            @Parameter(
                    description = "Unique identifier of the doctor completing the appointment",
                    example = "b1a9f7d3-8c22-4b92-a8b1-12d98f43c821"
            )
            @PathVariable UUID doctorId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Medical consultation outcome and notes",
                    required = true
            )
            @RequestBody RecordAppointmentResult result
    ) {
        return new ResponseDto(
                "Appointment completed",
                appointmentMapper.toDto(
                        appointmentService.completeAppointment(
                                appointmentId,
                                doctorId,
                                result
                        )
                )
        );
    }

    @PostMapping("/{appointmentId}/follow-up")
    @Operation(
            summary = "Book a follow-up appointment",
            description = "Creates a follow-up appointment linked to a previously completed appointment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Follow-up appointment successfully booked"),
            @ApiResponse(responseCode = "409", description = "Original appointment has not been completed"),
            @ApiResponse(responseCode = "404", description = "Original appointment not found")
    })
    public ResponseDto followUp(
            @Parameter(
                    description = "Unique identifier of the completed appointment",
                    example = "c3b8f4e2-1a6d-4b22-9f4c-1e5c2f9a7d11"
            )
            @PathVariable UUID appointmentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Follow-up appointment details",
                    required = true
            )
            @RequestBody RequestAppointmentDto appointmentDto
    ) {
        return new ResponseDto(
                "Follow-up booked",
                appointmentMapper.toDto(
                        appointmentService.bookFollowUp(appointmentId, appointmentDto)
                )
        );
    }
}
