package org.health.medical_service.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_availabilities")
public class DoctorAvailability {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfTheWeek day;

    @Column(updatable = false, nullable = false)
    private LocalTime startTime;

    @Column(updatable = false, nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isBooked;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public DoctorAvailability() {}

    public DoctorAvailability(UUID id, Doctor doctor, DayOfTheWeek day, LocalTime startTime, LocalTime endTime, boolean isBooked) {
        this.id = id;
        this.doctor = doctor;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public DayOfTheWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
