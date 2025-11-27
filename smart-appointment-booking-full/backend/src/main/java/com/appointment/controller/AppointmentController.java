package com.appointment.controller;

import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.ApiResponse;
import com.appointment.model.Appointment;
import com.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Appointment Management
 * Handles all appointment-related API endpoints
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {
  /**
   * Get all appointments as a map (id -> AppointmentResponse)
   * GET /api/v1/appointments/map
   * Actors: ADMIN, PROVIDER
   */
  @GetMapping("/map")
  @Operation(summary = "Get all appointments as map", description = "Retrieve all appointments as a map of id to details")
  public ResponseEntity<ApiResponse<Map<Long, AppointmentResponse>>> getAllAppointmentsMap() {
    List<AppointmentResponse> appointments = appointmentService.getAllAppointments();
    Map<Long, AppointmentResponse> map = appointments.stream()
      .collect(Collectors.toMap(AppointmentResponse::getId, a -> a));
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointments map retrieved successfully", map));
  }

  private final AppointmentService appointmentService;

  /**
   * Get all appointments
   * GET /api/v1/appointments
   * Actors: ADMIN, PROVIDER
   */
  @GetMapping
  @Operation(summary = "Get all appointments", description = "Retrieve list of all appointments")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
    List<AppointmentResponse> appointments = appointmentService.getAllAppointments();
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointments retrieved successfully", appointments));
  }

  /**
   * Get appointment by ID
   * GET /api/v1/appointments/{id}
   * Actors: ADMIN, PROVIDER, USER
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get appointment by ID", description = "Retrieve a specific appointment by its ID")
  public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(@PathVariable Long id) {
    AppointmentResponse appointment = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointment found", appointment));
  }

  /**
   * Create new appointment
   * POST /api/v1/appointments
   * Actors: USER
   */
  @PostMapping
  @Operation(summary = "Create appointment", description = "Book a new appointment")
  public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
    @Valid @RequestBody AppointmentRequest request) {
    AppointmentResponse appointment = appointmentService.createAppointment(request);
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(new ApiResponse<>(true, "Appointment created successfully", appointment));
  }

  /**
   * Update appointment
   * PUT /api/appointments/{id}
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update appointment", description = "Update an existing appointment")
  public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(
    @PathVariable Long id,
    @Valid @RequestBody AppointmentRequest request) {
    AppointmentResponse appointment = appointmentService.updateAppointment(id, request);
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointment updated successfully", appointment));
  }

  /**
   * Delete appointment
   * DELETE /api/appointments/{id}
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete appointment", description = "Cancel/delete an appointment")
  public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
    appointmentService.deleteAppointment(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointment deleted successfully", null));
  }

  /**
   * Get appointments by customer ID
   * GET /api/appointments/customer/{customerId}
   */
  @GetMapping("/customer/{customerId}")
  @Operation(summary = "Get customer appointments", description = "Get all appointments for a specific customer")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByCustomer(
    @PathVariable Long customerId) {
    List<AppointmentResponse> appointments = appointmentService.getAppointmentsByCustomer(customerId);
    return ResponseEntity.ok(new ApiResponse<>(true, "Customer appointments retrieved", appointments));
  }

  /**
   * Get appointments by service provider ID
   * GET /api/appointments/provider/{providerId}
   */
  @GetMapping("/provider/{providerId}")
  @Operation(summary = "Get provider appointments", description = "Get all appointments for a service provider")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByProvider(
    @PathVariable Long providerId) {
    List<AppointmentResponse> appointments = appointmentService.getAppointmentsByProvider(providerId);
    return ResponseEntity.ok(new ApiResponse<>(true, "Provider appointments retrieved", appointments));
  }

  /**
   * Get upcoming appointments by customer
   * GET /api/appointments/customer/{customerId}/upcoming
   */
  @GetMapping("/customer/{customerId}/upcoming")
  @Operation(summary = "Get upcoming appointments", description = "Get future appointments for a customer")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUpcomingAppointments(
    @PathVariable Long customerId) {
    List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointmentsByCustomer(customerId);
    return ResponseEntity.ok(new ApiResponse<>(true, "Upcoming appointments retrieved", appointments));
  }

  /**
   * Get appointments by status
   * GET /api/appointments/status/{status}
   */
  @GetMapping("/status/{status}")
  @Operation(summary = "Get appointments by status", description = "Filter appointments by status")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByStatus(
    @PathVariable Appointment.Status status) {
    List<AppointmentResponse> appointments = appointmentService.getAppointmentsByStatus(status);
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointments by status retrieved", appointments));
  }

  /**
   * Update appointment status
   * PATCH /api/appointments/{id}/status
   */
  @PatchMapping("/{id}/status")
  @Operation(summary = "Update status", description = "Change appointment status")
  public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
    @PathVariable Long id,
    @RequestParam String status) {
    try {
      Appointment.Status statusEnum = Appointment.Status.valueOf(status);
      AppointmentResponse appointment = appointmentService.updateAppointmentStatus(id, statusEnum);
      return ResponseEntity.ok(new ApiResponse<>(true, "Status updated successfully", appointment));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid status value: " + status, null));
    }
  }

  /**
   * Search appointments
   * GET /api/appointments/search?keyword=value
   */
  @GetMapping("/search")
  @Operation(summary = "Search appointments", description = "Search appointments by keyword")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> searchAppointments(
    @RequestParam String keyword) {
    List<AppointmentResponse> appointments = appointmentService.searchAppointments(keyword);
    return ResponseEntity.ok(new ApiResponse<>(true, "Search results", appointments));
  }

  /**
   * Get appointments by date range
   * GET /api/appointments/date-range?start=...&end=...
   */
  @GetMapping("/date-range")
  @Operation(summary = "Get appointments by date range", description = "Filter appointments by date range")
  public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByDateRange(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    List<AppointmentResponse> appointments = appointmentService.getAppointmentsByDateRange(start, end);
    return ResponseEntity.ok(new ApiResponse<>(true, "Appointments in date range", appointments));
  }

  /**
   * Get appointment statistics
   * GET /api/appointments/stats
   */
  @GetMapping("/stats")
  @Operation(summary = "Get statistics", description = "Get appointment statistics")
  public ResponseEntity<ApiResponse<Object>> getAppointmentStats() {
    Object stats = appointmentService.getAppointmentStatistics();
    return ResponseEntity.ok(new ApiResponse<>(true, "Statistics retrieved", stats));
  }
}