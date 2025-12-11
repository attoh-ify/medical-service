package org.health.medical_service.events;

import org.health.medical_service.entities.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentCreated {
    private final UUID appointmentId;
    private final LocalDateTime appointmentTime;
    private final String status;
    private final String result;
    private final String appointmentType;
    private final UUID followUpAppointmentId;

    public AppointmentCreated(Appointment appointment) {
        this.appointmentId = appointment.getId();
        this.appointmentTime = appointment.getAppointmentTime();
        this.status = appointment.getStatus().toString();
        this.result = appointment.getResult();
        this.appointmentType = appointment.getAppointmentType().toString();
        this.followUpAppointmentId = appointment.getFollowUpAppointmentId();
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public UUID getFollowUpAppointmentId() {
        return followUpAppointmentId;
    }
}
