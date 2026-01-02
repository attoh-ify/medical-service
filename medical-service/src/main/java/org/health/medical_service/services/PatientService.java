package org.health.medical_service.services;

import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.entities.*;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    Patient registerPatient(Patient patient);
    Patient getPatientDetails(String email);
    List<DayGroupedAvailabilityResponse> getAvailableDoctors(
            Specialization specialization,
            DayOfTheWeek day,
            String doctorFullName
    );
    List<Appointment> getAppointments(UUID patientId);
    List<Appointment> getAppointmentTrail(UUID patientId, UUID appointmentId);
}
