package org.health.medical_service.services.Impl;

import org.health.medical_service.dto.DayGroupedAvailabilityResponse;
import org.health.medical_service.dto.DoctorDailySlotResponse;
import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;
import org.health.medical_service.exceptions.BadRequestException;
import org.health.medical_service.repositories.AppointmentRepository;
import org.health.medical_service.repositories.DoctorRepository;
import org.health.medical_service.repositories.PatientRepository;
import org.health.medical_service.services.PatientService;
import org.health.medical_service.utils.helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Logger log =
            LoggerFactory.getLogger(PatientServiceImpl.class);

    public PatientServiceImpl(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        log.info("PatientServiceImpl initialized");
    }

    @Override
    public Patient registerPatient(Patient patient) {
        log.info("Registering new patient email={}", patient.getEmail());
        validatePatient(patient);
        Patient saved = patientRepository.save(patient);
        log.info("Patient registered successfully patientId={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Patient getPatientDetails(String email) {
        log.debug("Fetching patient details email={}", email);
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Patient not found email={}", email);
                    return new BadRequestException("Patient with this email is not registered.");
                });
    }

    @Transactional(readOnly = true)
    @Override
    public List<DayGroupedAvailabilityResponse> getAvailableDoctors(
            Specialization specialization,
            DayOfTheWeek requestedDay,
            String doctorFullName
    ) {
        log.info("Searching available doctors specialization={} day={} name={}",
                specialization, requestedDay, doctorFullName);

        if (specialization == null) {
            log.warn("Doctor availability search failed: specialization missing");
            throw new BadRequestException("Specialization required.");
        }

        List<Doctor> doctors =
                doctorRepository.searchDoctors(specialization, requestedDay, doctorFullName);

        log.debug("Found {} matching doctors", doctors.size());

        List<DayGroupedAvailabilityResponse> finalResponse = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 14; i++) {
            LocalDate date = today.plusDays(i);
            DayOfTheWeek dow = DayOfTheWeek.valueOf(date.getDayOfWeek().name());

            if (requestedDay != null && requestedDay != dow) continue;

            List<DoctorDailySlotResponse> doctorsForThisDay = new ArrayList<>();

            for (Doctor doctor : doctors) {
                boolean worksToday = doctor.getDoctorAvailabilities()
                        .stream()
                        .anyMatch(a -> a.getDay() == dow);

                if (!worksToday) continue;

                int totalBookedHours =
                        helpers.computeTotalBookedHours(doctor, date);

                List<TimeRange> freeRanges =
                        helpers.calculateFreeTimeRanges(doctor, date, 60);

                doctorsForThisDay.add(new DoctorDailySlotResponse(
                        doctor.getId(),
                        doctor.getFullName(),
                        totalBookedHours,
                        freeRanges
                ));
            }

            if (!doctorsForThisDay.isEmpty()) {
                finalResponse.add(
                        new DayGroupedAvailabilityResponse(date, doctorsForThisDay)
                );
            }
        }

        log.info("Doctor availability search completed results={}", finalResponse.size());
        return finalResponse;
    }

    @Transactional
    @Override
    public List<Appointment> getAppointments(UUID patientId) {
        log.debug("Fetching appointments for patientId={}", patientId);
        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientId);
        log.debug("Found {} appointments for patientId={}",
                appointments.size(), patientId);
        return appointments;
    }

    @Override
    public List<Appointment> getAppointmentTrail(UUID patientId, UUID appointmentId) {
        log.debug("Fetching appointment trail patientId={} appointmentId={}",
                patientId, appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found appointmentId={}", appointmentId);
                    return new BadRequestException("Appointment not found");
                });

        if (!appointment.getPatient().getId().equals(patientId)) {
            log.warn("Appointment does not belong to patient patientId={} appointmentId={}",
                    patientId, appointmentId);
            throw new BadRequestException("Appointment does not match the patient");
        }

        Set<UUID> visited = new HashSet<>();
        List<Appointment> trail =
                appointmentRecursion(appointmentId, patientId, visited);

        log.debug("Appointment trail resolved length={}", trail.size());
        return trail;
    }

    private List<Appointment> appointmentRecursion(
            UUID appointmentId,
            UUID patientId,
            Set<UUID> visited
    ) {
        if (!visited.add(appointmentId)) {
            log.debug("Detected cycle in appointment trail appointmentId={}", appointmentId);
            return Collections.emptyList();
        }

        List<Appointment> appointments = new ArrayList<>();
        appointmentRepository.findById(appointmentId).ifPresent(app -> {
            if (!app.getPatient().getId().equals(patientId)) return;
            appointments.add(app);
            if (app.getFollowUpAppointmentId() != null) {
                appointments.addAll(
                        appointmentRecursion(
                                app.getFollowUpAppointmentId(),
                                patientId,
                                visited
                        )
                );
            }
        });

        return appointments;
    }

    @Transactional
    private void validatePatient(Patient p) {
        log.debug("Validating patient email={}", p.getEmail());

        if (p.getId() != null) throw new BadRequestException("Patient ID is system generated");
        if (helpers.isBlank(p.getFullName())) throw new BadRequestException("Full name required");
        if (helpers.isBlank(p.getEmail())) throw new BadRequestException("Email required");
        if (helpers.isBlank(p.getPhone())) throw new BadRequestException("Phone required");
        if (p.getDob() == null || p.getDob().isAfter(LocalDate.now()))
            throw new BadRequestException("Invalid DOB");
        if (p.getGender() == null) throw new BadRequestException("Gender required");
        if (helpers.isBlank(p.getAddress())) throw new BadRequestException("Address required");

        patientRepository.findByEmail(p.getEmail()).ifPresent(existing -> {
            log.warn("Patient email already exists email={}", p.getEmail());
            throw new BadRequestException("This email is already registered to a patient.");
        });

        patientRepository.findByPhone(p.getPhone()).ifPresent(existing -> {
            log.warn("Patient phone already exists phone={}", p.getPhone());
            throw new BadRequestException("This phone is already registered to a patient.");
        });
    }
}
