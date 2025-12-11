package org.health.medical_service.utils;

import org.health.medical_service.dto.TimeRange;
import org.health.medical_service.entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class helpers {
    public helpers() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static List<TimeRange> calculateFreeTimeRanges(
            Doctor doctor,
            LocalDate date,
            long minFreeMinutes
    ) {
        List<TimeRange> freeRanges = new ArrayList<>();

        // Find today’s availability window for that day
        doctor.getDoctorAvailabilities().stream()
                .filter(a -> a.getDay() == DayOfTheWeek.valueOf(date.getDayOfWeek().name()))
                .forEach(avail -> {

                    LocalDateTime dayStart = date.atTime(avail.getStartTime());
                    LocalDateTime dayEnd = date.atTime(avail.getEndTime());

                    // Appointments for today
                    List<Appointment> todaysAppointments = doctor.getAppointments()
                            .stream()
                            .filter(appointment -> appointment.getAppointmentTime().toLocalDate().equals(date))
                            .filter(appointment -> List.of(AppointmentStatus.AWAITING, AppointmentStatus.IN_PROGRESS).contains(appointment.getStatus()))
                            .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                            .toList();

                    LocalDateTime cursor = dayStart;

                    for (Appointment app : todaysAppointments) {
                        LocalDateTime appStart = app.getAppointmentTime();
                        LocalDateTime appEnd = appStart.plusHours(app.getAppointmentType().getDurationHours());

                        // Check gap BEFORE the appointment
                        if (!cursor.plusMinutes(minFreeMinutes).isAfter(appStart)) {
                            freeRanges.add(new TimeRange(cursor, appStart));
                        }

                        cursor = appEnd;
                    }

                    // Check gap AFTER last appointment
                    if (!cursor.plusMinutes(minFreeMinutes).isAfter(dayEnd)) {
                        freeRanges.add(new TimeRange(cursor, dayEnd));
                    }
                });

        return freeRanges;
    }

    public static int computeTotalBookedHours(Doctor doctor, LocalDate date) {
        return doctor.getAppointments().stream()
                .filter(app -> app.getAppointmentTime().toLocalDate().equals(date))
                .mapToInt(app -> app.getAppointmentType().getDurationHours())
                .sum();
    }

    public static void validateAppointmentTime(LocalDateTime appointmentTime, List<TimeRange> freeRanges) {
        if (freeRanges.isEmpty()) {
            throw new IllegalArgumentException("Doctor does not have free time");
        }

        // Duration of a consultation (60 mins for now)
        long durationMinutes = AppointmentType.CONSULTATION.getDurationHours();

        LocalDateTime appointmentEndTime = appointmentTime.plusMinutes(durationMinutes);

        boolean insideValidRange = freeRanges.stream().anyMatch(timeRange ->
                !appointmentTime.isBefore(timeRange.start()) &&
                        !appointmentEndTime.isAfter(timeRange.end())
        );

        if (!insideValidRange) {
            throw new IllegalArgumentException("Selected time is not within the free range.");
        }
    }
}
