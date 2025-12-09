package org.health.medical_service.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(nullable = true)
    private String result;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;

    @Column(nullable = true, unique = true)
    private UUID followUpAppointmentId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Appointment() {}

    public Appointment(UUID id, Patient patient, Doctor doctor, LocalDateTime appointmentTime, AppointmentStatus status, String result, AppointmentType appointmentType, UUID followUpAppointmentId) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.result = result;
        this.appointmentType = appointmentType;
        this.followUpAppointmentId = followUpAppointmentId;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public UUID getFollowUpAppointmentId() {
        return followUpAppointmentId;
    }

    public void setFollowUpAppointment(UUID followUpAppointmentId) {
        this.followUpAppointmentId = followUpAppointmentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + (patient != null ? patient.getId() : null) +
                ", doctorId=" + (doctor != null ? doctor.getId() : null) +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                ", result='" + result + '\'' +
                ", appointmentType=" + appointmentType +
                ", followUpAppointmentId=" + followUpAppointmentId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
