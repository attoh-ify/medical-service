package org.health.medical_service.events;

import org.health.medical_service.entities.Appointment;
import org.health.medical_service.entities.Doctor;
import org.health.medical_service.entities.DoctorAvailability;

import java.util.List;
import java.util.UUID;

public class DoctorCreated {
    private final UUID doctorId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String specialization;
    private final String bio;
    private final List<DoctorAvailability> doctorAvailabilities;
    private final List<Appointment> appointments;

    public DoctorCreated(Doctor doctor) {
        this.doctorId = doctor.getId();
        this.fullName = doctor.getFullName();
        this.email = doctor.getEmail();
        this.phone = doctor.getPhone();
        this.specialization = doctor.getSpecialization().toString();
        this.bio = doctor.getBio();
        this.doctorAvailabilities = doctor.getDoctorAvailabilities();
        this.appointments = doctor.getAppointments();
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getBio() {
        return bio;
    }

    public List<DoctorAvailability> getDoctorAvailabilities() {
        return doctorAvailabilities;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
