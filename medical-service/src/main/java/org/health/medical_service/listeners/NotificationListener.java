package org.health.medical_service.listeners;

import org.health.medical_service.events.AppointmentCreated;
import org.health.medical_service.events.DoctorCreated;
import org.health.medical_service.events.PatientCreated;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    @Async
    @EventListener
    public void onPatientCreated(PatientCreated event) {
        System.out.println("\n=== NEW PATIENT REGISTERED NOTIFICATION ===");
        System.out.println("Patient ID:    " + event.getPatientId());
        System.out.println("Full Name:     " + event.getFullName());
        System.out.println("Email:         " + event.getEmail());
        System.out.println("Phone:         " + event.getPhone());
        System.out.println("Date of Birth: " + event.getDob());
        System.out.println("Gender:        " + event.getGender());
        System.out.println("Address:       " + event.getAddress());
        System.out.println("Status:        Registration successful. Welcome message dispatched.");
        System.out.println("===========================================\n");
    }

    @Async
    @EventListener
    public void onDoctorCreated(DoctorCreated event) {
        System.out.println("\n=== NEW DOCTOR REGISTERED NOTIFICATION ===");
        System.out.println("Doctor ID:        " + event.getDoctorId());
        System.out.println("Full Name:        " + event.getFullName());
        System.out.println("Email:            " + event.getEmail());
        System.out.println("Phone:            " + event.getPhone());
        System.out.println("Specialization:   " + event.getSpecialization());
        System.out.println("Bio:              " + event.getBio());
        System.out.println("\nAvailabilities Count: " + event.getDoctorAvailabilities().size());
        System.out.println("Appointments Count:   " + event.getAppointments().size());
        System.out.println("Status: Doctor onboarded successfully.");
        System.out.println("===========================================\n");
    }

    @Async
    @EventListener
    public void onAppointmentCreated(AppointmentCreated event) {
        System.out.println("\n=== NEW APPOINTMENT CREATED ===");
        System.out.println("Appointment ID:     " + event.getAppointmentId());
        System.out.println("Time:               " + event.getAppointmentTime());
        System.out.println("Status:             " + event.getStatus());
        System.out.println("Result:             " + event.getResult());
        System.out.println("Type:               " + event.getAppointmentType());
        System.out.println("Follow-Up ID:       " + event.getFollowUpAppointmentId());
        System.out.println("Status: Appointment successfully logged.");
        System.out.println("===========================================\n");
    }
}
