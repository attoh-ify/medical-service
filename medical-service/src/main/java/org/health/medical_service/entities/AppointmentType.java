package org.health.medical_service.entities;

public enum AppointmentType {
    CONSULTATION(1),
    SCAN(2),
    LAB_TEST(1),
    SURGERY(6);

    private final int durationHours;

    AppointmentType(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getDurationHours() {
        return durationHours;
    }
}
