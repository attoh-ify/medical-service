package org.health.medical_service.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, updatable = false, nullable = false)
    private String email;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "bio", nullable = false)
    private String bio;

    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<DoctorAvailability> doctorAvailabilities;

    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Appointment> appointments;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Doctor() {}

    public Doctor(String fullName, String email, String phone, String specialization, String bio) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.bio = bio;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<DoctorAvailability> getDoctorAvailabilities() {
        return doctorAvailabilities;
    }

    public void setDoctorAvailabilities(List<DoctorAvailability> doctorAvailabilities) {
        this.doctorAvailabilities = doctorAvailabilities;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", specialization='" + specialization + '\'' +
                ", bio='" + bio + '\'' +
                ", doctorAvailabilities=" + doctorAvailabilities +
                ", appointments=" + appointments +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
