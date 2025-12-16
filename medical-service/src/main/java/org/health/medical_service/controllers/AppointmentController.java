package org.health.medical_service.controllers;

import org.health.medical_service.dto.RecordAppointmentResult;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.mappers.AppointmentMapper;
import org.health.medical_service.services.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/medical-service/appointments")
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
                true,
                "Appointment booked",
                appointmentMapper.toDto(
                        appointmentService.bookAppointment(dto)
                )
        );
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseDto cancel(@PathVariable UUID appointmentId,
                              @RequestParam UUID actorId) {
        return new ResponseDto(
                true,
                "Appointment cancelled",
                appointmentMapper.toDto(
                        appointmentService.cancelAppointment(appointmentId, actorId)
                )
        );
    }

    @PatchMapping("/{appointmentId}/begin")
    public ResponseDto begin(
            @PathVariable UUID appointmentId,
            @RequestParam UUID doctorId
    ) {
        appointmentService.beginAppointment(appointmentId, doctorId);
        return new ResponseDto(true, "Appointment started", null);
    }

    @PatchMapping("/{appointmentId}/complete")
    public ResponseDto complete(
            @PathVariable UUID appointmentId,
            @RequestParam UUID doctorId,
            @RequestBody RecordAppointmentResult result
    ) {
        return new ResponseDto(
                true,
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
                true,
                "Follow-up booked",
                appointmentMapper.toDto(
                        appointmentService.bookFollowUp(appointmentId, appointmentDto)
                )
        );
    }
}
