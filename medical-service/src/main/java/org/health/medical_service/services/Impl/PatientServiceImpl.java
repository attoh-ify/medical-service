package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.DoctorDailySlotResponse;
import org.health.medical_service.dto.RequestAppointmentDto;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.PatientService;
import org.springframework.stereotype.Service;
import org.health.medical_service.utils.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientServiceImpl(PatientRepository patientRepository, DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatientDetails(String email) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient with this email is not registered with us");
        }
        return patient.get();
    }

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

    @Override
    public Appointment bookAppointment(RequestAppointmentDto dto) {
        Patient patient = patientRepository.findByEmail(dto.patientEmail()).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        Doctor doctor = doctorRepository.findById(dto.doctorId()).orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        List<TimeRange> freeRanges = helpers.calculateFreeTimeRanges(doctor, dto.appointmentTime().toLocalDate(), 60);
        
        helpers.validateAppointmentTime(dto.appointmentTime(), freeRanges);

        return appointmentRepository.save(
                new Appointment(
                        null,
                        patient,
                        doctor,
                        dto.appointmentTime(),
                        AppointmentStatus.AWAITING,
                        null,
                        AppointmentType.CONSULTATION,
                        null
                )
        );
    }

    @Override
    public List<Appointment> getAppointments(String email) {
        return appointmentRepository.findByPatientEmail(email);
    }

    @Override
    public Appointment getAppointment(String patientEmail, UUID appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }
        if (!Objects.equals(appointment.get().getPatient().getEmail(), patientEmail)) {
            throw new IllegalArgumentException("Appointment does not match the user");
        }
        return appointment.get();
    }

    @Override
    public Appointment cancelAppointment(String patientEmail, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!Objects.equals(appointment.getPatient().getEmail(), patientEmail)) {
            throw new IllegalArgumentException("Appointment does not match the patient");
        }
        if (appointment.getStatus() != AppointmentStatus.AWAITING) {
            throw new IllegalArgumentException("Appointment can no longer be updated.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return appointment;
    }

    private void validatePatient(Patient p) {
        if (p.getId() != null) throw new IllegalArgumentException("Patient ID is system generated");
        if (helpers.isBlank(p.getFullName())) throw new IllegalArgumentException("Full name required");
        if (helpers.isBlank(p.getEmail())) throw new IllegalArgumentException("Email required");
        if (helpers.isBlank(p.getPhone())) throw new IllegalArgumentException("Phone required");
        if (p.getDob() == null || p.getDob().isAfter(LocalDate.now())) throw new IllegalArgumentException("Invalid DOB");
        if (p.getGender() == null) throw new IllegalArgumentException("Gender required");
        if (helpers.isBlank(p.getAddress())) throw new IllegalArgumentException("Address required");
        Optional<Patient> emailExists = patientRepository.findByEmail(p.getEmail());
        if (emailExists.isPresent()) {
            throw new IllegalArgumentException("This email is already registered to a patient.");
        }
        Optional<Patient> phoneExists = patientRepository.findByPhone(p.getPhone());
        if (phoneExists.isPresent()) {
            throw new IllegalArgumentException("This phone is already registered to a patient.");
        }
    }
}
