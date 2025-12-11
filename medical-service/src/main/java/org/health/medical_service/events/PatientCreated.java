package org.health.medical_service.events;

import org.health.medical_service.entities.Gender;
import org.health.medical_service.entities.Patient;

import java.time.LocalDate;
import java.util.UUID;

public class PatientCreated {
    private final UUID patientId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final LocalDate dob;
    private final Gender gender;
    private final String address;

    public PatientCreated(Patient patient) {
        this.patientId = patient.getId();
        this.fullName = patient.getFullName();
        this.email = patient.getEmail();
        this.phone = patient.getPhone();
        this.dob = patient.getDob();
        this.gender = patient.getGender();
        this.address = patient.getAddress();
    }

    public UUID getPatientId() {
        return patientId;
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

    public LocalDate getDob() {
        return dob;
    }

    public Gender getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }
}
