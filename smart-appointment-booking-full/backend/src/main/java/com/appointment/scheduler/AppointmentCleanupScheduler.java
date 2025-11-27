package com.appointment.scheduler;

import com.appointment.model.Appointment;
import com.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentCleanupScheduler {
    private final AppointmentRepository appointmentRepository;

    // Run every day at 2am
    @Scheduled(cron = "0 0 2 * * *")
    public void removeExpiredUnconfirmedAppointments() {
        List<Appointment> expired = appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                Appointment.Status.PENDING, LocalDateTime.now());
        if (!expired.isEmpty()) {
            appointmentRepository.deleteAll(expired);
            log.info("Deleted {} expired unconfirmed appointments", expired.size());
        }
    }
}