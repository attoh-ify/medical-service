package org.health.medical_service.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
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
    private DayOfTheWeek dayOfTheWeek;

    @Column(updatable = false, nullable = false)
    private LocalDateTime startTime;

    @Column(updatable = false, nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean isBooked;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public DoctorAvailability() {}

    public DoctorAvailability(Doctor doctor, DayOfTheWeek dayOfTheWeek, LocalDateTime startTime, LocalDateTime endTime) {
        this.doctor = doctor;
        this.dayOfTheWeek = dayOfTheWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = false;
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

    public DayOfTheWeek getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "DoctorAvailability{" +
                "id=" + id +
                ", doctorId=" + (doctor != null ? doctor.getId() : null) +
                ", dayOfTheWeek=" + dayOfTheWeek +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", isBooked=" + isBooked +
                ", createdAt=" + createdAt +
                '}';
    }
}
