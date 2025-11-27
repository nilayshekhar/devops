package com.appointment.service.impl;

import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
import com.appointment.exception.AppointmentException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.model.Appointment;
import com.appointment.model.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.UserRepository;
import com.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Implementation of AppointmentService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getAllAppointments() {
    log.info("Fetching all appointments");
    // Default sorting: most recent appointment first, then by created user
    return appointmentRepository.findAll().stream()
      .sorted(Comparator.comparing(Appointment::getAppointmentDateTime).reversed()
        .thenComparing(a -> a.getCustomer().getId(), Comparator.reverseOrder()))
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AppointmentResponse getAppointmentById(Long id) {
    log.info("Fetching appointment with id: {}", id);
    Appointment appointment = appointmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    return convertToResponse(appointment);
  }

  @Override
  public AppointmentResponse createAppointment(AppointmentRequest request) {
    log.info("Creating new appointment for customer: {}", request.getCustomerId());

    // Validate customer exists
    User customer = userRepository.findById(request.getCustomerId())
      .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

    // Validate service provider exists
    User provider = userRepository.findById(request.getServiceProviderId())
      .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

    // Validate provider is actually a service provider
    if (!provider.isServiceProvider()) {
      throw new AppointmentException("Selected user is not a service provider");
    }

    // Validate appointment is in future
    if (request.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
      throw new AppointmentException("Appointment must be scheduled for a future date");
    }

    // Check for conflicting appointments (optional - prevent double booking)
    List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
      provider.getId(),
      request.getAppointmentDateTime().minusHours(1),
      request.getAppointmentDateTime().plusHours(1)
    );

    if (!conflicts.isEmpty()) {
      throw new AppointmentException("Service provider already has an appointment at this time");
    }

    // Create appointment
    Appointment appointment = new Appointment();
    appointment.setCustomer(customer);
    appointment.setServiceProvider(provider);
    appointment.setServiceType(request.getServiceType());
    appointment.setAppointmentDateTime(request.getAppointmentDateTime());
    appointment.setNotes(request.getNotes());
    appointment.setStatus(Appointment.Status.PENDING);

    Appointment savedAppointment = appointmentRepository.save(appointment);
    log.info("Appointment created successfully with id: {}", savedAppointment.getId());

    return convertToResponse(savedAppointment);
  }

  @Override
  public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
    log.info("Updating appointment with id: {}", id);

    Appointment appointment = appointmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

    // Update fields
    if (request.getServiceType() != null) {
      appointment.setServiceType(request.getServiceType());
    }

    if (request.getAppointmentDateTime() != null) {
      if (request.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
        throw new AppointmentException("Appointment must be scheduled for a future date");
      }
      appointment.setAppointmentDateTime(request.getAppointmentDateTime());
    }

    if (request.getNotes() != null) {
      appointment.setNotes(request.getNotes());
    }

    Appointment updatedAppointment = appointmentRepository.save(appointment);
    log.info("Appointment updated successfully");

    return convertToResponse(updatedAppointment);
  }

  @Override
  public void deleteAppointment(Long id) {
    log.info("Deleting appointment with id: {}", id);

    if (!appointmentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Appointment not found with id: " + id);
    }

    appointmentRepository.deleteById(id);
    log.info("Appointment deleted successfully");
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getAppointmentsByCustomer(Long customerId) {
    log.info("Fetching appointments for customer: {}", customerId);

    if (!userRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer not found");
    }

    return appointmentRepository.findByCustomerId(customerId).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getAppointmentsByProvider(Long providerId) {
    log.info("Fetching appointments for provider: {}", providerId);

    if (!userRepository.existsById(providerId)) {
      throw new ResourceNotFoundException("Provider not found");
    }

    return appointmentRepository.findByServiceProviderId(providerId).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getUpcomingAppointmentsByCustomer(Long customerId) {
    log.info("Fetching upcoming appointments for customer: {}", customerId);

    if (!userRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer not found");
    }

    return appointmentRepository.findUpcomingAppointmentsByCustomer(customerId, LocalDateTime.now())
      .stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getAppointmentsByStatus(Appointment.Status status) {
    log.info("Fetching appointments with status: {}", status);

    return appointmentRepository.findByStatus(status).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  public AppointmentResponse updateAppointmentStatus(Long id, Appointment.Status status) {
    log.info("Updating appointment {} status to: {}", id, status);

    Appointment appointment = appointmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

    appointment.setStatus(status);
    Appointment updatedAppointment = appointmentRepository.save(appointment);

    log.info("Status updated successfully");
    return convertToResponse(updatedAppointment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> searchAppointments(String keyword) {
    log.info("Searching appointments with keyword: {}", keyword);

    return appointmentRepository.searchAppointments(keyword).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
    log.info("Fetching appointments between {} and {}", start, end);

    return appointmentRepository.findByAppointmentDateTimeBetween(start, end).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Object> getAppointmentStatistics() {
    log.info("Fetching appointment statistics");

    Map<String, Object> stats = new HashMap<>();

    long total = appointmentRepository.count();
    long pending = appointmentRepository.findByStatus(Appointment.Status.PENDING).size();
    long confirmed = appointmentRepository.findByStatus(Appointment.Status.CONFIRMED).size();
    long completed = appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size();
    long cancelled = appointmentRepository.findByStatus(Appointment.Status.CANCELLED).size();

    stats.put("total", total);
    stats.put("pending", pending);
    stats.put("confirmed", confirmed);
    stats.put("completed", completed);
    stats.put("cancelled", cancelled);

    return stats;
  }

  // Helper method to convert Entity to Response DTO
  private AppointmentResponse convertToResponse(Appointment appointment) {
    AppointmentResponse response = new AppointmentResponse();
    response.setId(appointment.getId());
    response.setCustomerId(appointment.getCustomer().getId());
    response.setCustomerName(appointment.getCustomer().getName());
    response.setCustomerEmail(appointment.getCustomer().getEmail());
    response.setServiceProviderId(appointment.getServiceProvider().getId());
    response.setServiceProviderName(appointment.getServiceProvider().getName());
    response.setServiceProviderEmail(appointment.getServiceProvider().getEmail());
    response.setServiceType(appointment.getServiceType());
    response.setAppointmentDateTime(appointment.getAppointmentDateTime());
    response.setNotes(appointment.getNotes());
    response.setStatus(appointment.getStatus());
    response.setCreatedAt(appointment.getCreatedAt());
    response.setUpdatedAt(appointment.getUpdatedAt());
    return response;
  }
}