package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.DoctorDailySlotResponse;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.events.AppointmentCreated;
import org.health.medical_service.events.PatientCreated;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.PatientService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.health.medical_service.utils.helpers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher publisher;

    public PatientServiceImpl(PatientRepository patientRepository, DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, ApplicationEventPublisher publisher) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.publisher = publisher;
    }

    @Override
    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        Patient createdPatient = patientRepository.save(patient);
//        publisher.publishEvent(new PatientCreated(createdPatient));
        return createdPatient;
    }

    @Transactional(readOnly = true)
    @Override
    public Patient getPatientDetails(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with this ID is not registered."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<DayGroupedAvailabilityResponse> getAvailableDoctors(
            Specialization specialization,
            DayOfTheWeek requestedDay,
            String doctorFullName
    ) {
        List<Doctor> doctors = doctorRepository.searchDoctors(specialization, requestedDay, doctorFullName);
        List<DayGroupedAvailabilityResponse> finalResponse = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = 0; i < 14; i++) {
            LocalDate date = today.plusDays(i);
            DayOfTheWeek dow = DayOfTheWeek.valueOf(date.getDayOfWeek().name());

            // If user explicitly requested a day, skip other days
            if (requestedDay != null && requestedDay != dow) continue;

            List<DoctorDailySlotResponse> doctorsForThisDay = new ArrayList<>();

            for (Doctor doctor : doctors) {
                boolean worksToday = doctor.getDoctorAvailabilities()
                        .stream()
                        .anyMatch(a -> a.getDay() == dow);

                if (!worksToday) continue;

                int totalBookedHours = helpers.computeTotalBookedHours(doctor, date);
                List<TimeRange> freeRanges = helpers.calculateFreeTimeRanges(doctor, date, 60);

                // If doctor is working but has no free time, still include him
                doctorsForThisDay.add(new DoctorDailySlotResponse(
                        doctor.getId(),
                        doctor.getFullName(),
                        totalBookedHours,
                        freeRanges
                ));
            }

            // Skip this date if no doctor works on this date
            if (!doctorsForThisDay.isEmpty()) {
                finalResponse.add(new DayGroupedAvailabilityResponse(date, doctorsForThisDay));
            }
        }

        return finalResponse;
    }

    @Transactional
    @Override
    public List<Appointment> getAppointments(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentTrail(UUID patientId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("Appointment does not match the patient");
        }

        Set<UUID> visited = new HashSet<>();
        return appointmentRecursion(appointmentId, patientId, visited);
    }

    private List<Appointment> appointmentRecursion(UUID appointmentId, UUID patientId, Set<UUID> visited) {
        if (!visited.add(appointmentId)) return Collections.emptyList();

        List<Appointment> appointments = new ArrayList<>();
        appointmentRepository.findById(appointmentId).ifPresent(app -> {
            if (!app.getPatient().getId().equals(patientId)) return; // skip if patient mismatch
            appointments.add(app);
            if (app.getFollowUpAppointmentId() != null) {
                appointments.addAll(appointmentRecursion(app.getFollowUpAppointmentId(), patientId, visited));
            }
        });
        return appointments;
    }

    @Transactional
    private void validatePatient(Patient p) {
        if (p.getId() != null) throw new IllegalArgumentException("Patient ID is system generated");
        if (helpers.isBlank(p.getFullName())) throw new IllegalArgumentException("Full name required");
        if (helpers.isBlank(p.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(p.getPhone())) throw new IllegalArgumentException("Phone required");
        if (p.getDob() == null || p.getDob().isAfter(LocalDate.now())) throw new IllegalArgumentException("Invalid DOB");
        if (p.getGender() == null) throw new IllegalArgumentException("Gender required");
        if (helpers.isBlank(p.getAddress())) throw new IllegalArgumentException("Address required");

        patientRepository.findByEmail(p.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        });

        patientRepository.findByPhone(p.getPhone()).ifPresent(existing -> {
            throw new IllegalArgumentException("This phone is already registered to a patient.");
        });
    }
}
