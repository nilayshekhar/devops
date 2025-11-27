package com.appointment.service;

import com.appointment.model.Appointment;
import com.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentCleanupService {
    private final AppointmentRepository appointmentRepository;

    public int removeExpiredUnconfirmedAppointments() {
        List<Appointment> expired = appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                Appointment.Status.PENDING, LocalDateTime.now());
        int count = expired.size();
        if (count > 0) {
            appointmentRepository.deleteAll(expired);
            log.info("Deleted {} expired unconfirmed appointments", count);
        }
        return count;
    }
}