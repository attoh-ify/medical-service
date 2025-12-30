package org.health.medical_service.controllers;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.services.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
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
    public ResponseDto book(@RequestBody RequestAppointmentDto dto) {
        return new ResponseDto(
                "Appointment booked",
                appointmentMapper.toDto(
                        appointmentService.bookAppointment(dto)
                )
        );
    }

    @PatchMapping("/{appointmentId}/cancel/{doctorId}")
    public ResponseDto cancel(@PathVariable UUID appointmentId,
                              @PathVariable UUID doctorId) {
        return new ResponseDto(
                "Appointment cancelled",
                appointmentMapper.toDto(
                        appointmentService.cancelAppointment(appointmentId, doctorId)
                )
        );
    }

    @PatchMapping("/{appointmentId}/begin")
    public ResponseDto begin(
            @PathVariable UUID appointmentId,
            @RequestParam UUID doctorId
    ) {
        appointmentService.beginAppointment(appointmentId, doctorId);
        return new ResponseDto("Appointment started", null);
    }

    @PatchMapping("/{appointmentId}/complete/{doctorId}")
    public ResponseDto complete(
            @PathVariable UUID appointmentId,
            @PathVariable UUID doctorId,
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
    public ResponseDto followUp(
            @PathVariable UUID appointmentId,
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
