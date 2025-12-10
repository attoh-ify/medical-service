package org.health.medical_service.entities;

public enum AppointmentStatus {
    AWAITING,  // created but hasn't started
    IN_PROGRESS,  // is currently on going
    COMPLETED,  // has ended
    CANCELLED  // was cancelled by doctor or patient
}
