package com.appointment.repository;

import com.appointment.model.Appointment;
import com.appointment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Appointment entity
 * Provides CRUD operations and custom queries for appointments
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  // Find appointments that are PENDING and in the past
  List<Appointment> findByStatusAndAppointmentDateTimeBefore(Appointment.Status status, LocalDateTime dateTime);

  /**
   * Find all appointments for a specific customer
   * @param customer Customer entity
   * @return List of appointments
   */
  List<Appointment> findByCustomer(User customer);

  /**
   * Find all appointments for a specific customer by ID
   * @param customerId Customer ID
   * @return List of appointments
   */
  List<Appointment> findByCustomerId(Long customerId);

  /**
   * Find all appointments for a specific service provider
   * @param serviceProvider Service provider entity
   * @return List of appointments
   */
  List<Appointment> findByServiceProvider(User serviceProvider);

  /**
   * Find all appointments for a specific service provider by ID
   * @param providerId Service provider ID
   * @return List of appointments
   */
  List<Appointment> findByServiceProviderId(Long providerId);

  /**
   * Find appointments by status
   * @param status Appointment status
   * @return List of appointments with specified status
   */
  List<Appointment> findByStatus(Appointment.Status status);

  /**
   * Find appointments by service type
   * @param serviceType Type of service
   * @return List of appointments
   */
  List<Appointment> findByServiceType(Appointment.ServiceType serviceType);

  /**
   * Find appointments between specific dates
   * @param start Start date/time
   * @param end End date/time
   * @return List of appointments in date range
   */
  List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

  /**
   * Find appointments for a customer with specific status
   * @param customerId Customer ID
   * @param status Appointment status
   * @return List of appointments
   */
  List<Appointment> findByCustomerIdAndStatus(Long customerId, Appointment.Status status);

  /**
   * Find appointments for a provider with specific status
   * @param providerId Provider ID
   * @param status Appointment status
   * @return List of appointments
   */
  List<Appointment> findByServiceProviderIdAndStatus(Long providerId, Appointment.Status status);

  /**
   * Find upcoming appointments for a customer (after current time)
   * @param customerId Customer ID
   * @param now Current date/time
   * @return List of upcoming appointments ordered by date
   */
  @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId " +
    "AND a.appointmentDateTime > :now " +
    "AND (a.status = 'PENDING' OR a.status = 'CONFIRMED') " +
    "ORDER BY a.appointmentDateTime ASC")
  List<Appointment> findUpcomingAppointmentsByCustomer(
    @Param("customerId") Long customerId,
    @Param("now") LocalDateTime now
  );

  /**
   * Find upcoming appointments for a service provider
   * @param providerId Provider ID
   * @param now Current date/time
   * @return List of upcoming appointments ordered by date
   */
  @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
    "AND a.appointmentDateTime > :now " +
    "AND (a.status = 'PENDING' OR a.status = 'CONFIRMED') " +
    "ORDER BY a.appointmentDateTime ASC")
  List<Appointment> findUpcomingAppointmentsByProvider(
    @Param("providerId") Long providerId,
    @Param("now") LocalDateTime now
  );

  /**
   * Find past appointments for a customer
   * @param customerId Customer ID
   * @param now Current date/time
   * @return List of past appointments ordered by date descending
   */
  @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId " +
    "AND a.appointmentDateTime < :now " +
    "ORDER BY a.appointmentDateTime DESC")
  List<Appointment> findPastAppointmentsByCustomer(
    @Param("customerId") Long customerId,
    @Param("now") LocalDateTime now
  );

  /**
   * Find past appointments for a provider
   * @param providerId Provider ID
   * @param now Current date/time
   * @return List of past appointments
   */
  @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
    "AND a.appointmentDateTime < :now " +
    "ORDER BY a.appointmentDateTime DESC")
  List<Appointment> findPastAppointmentsByProvider(
    @Param("providerId") Long providerId,
    @Param("now") LocalDateTime now
  );

  /**
   * Count appointments by status for a service provider
   * @param providerId Provider ID
   * @param status Appointment status
   * @return Count of appointments
   */
  @Query("SELECT COUNT(a) FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
    "AND a.status = :status")
  Long countByProviderAndStatus(
    @Param("providerId") Long providerId,
    @Param("status") Appointment.Status status
  );

  /**
   * Count appointments by status for a customer
   * @param customerId Customer ID
   * @param status Appointment status
   * @return Count of appointments
   */
  Long countByCustomerIdAndStatus(Long customerId, Appointment.Status status);

  /**
   * Find conflicting appointments (same provider, overlapping time)
   * Used to prevent double booking
   * @param providerId Provider ID
   * @param startTime Start of time window
   * @param endTime End of time window
   * @return List of conflicting appointments
   */
  @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
    "AND a.status != 'CANCELLED' " +
    "AND a.appointmentDateTime BETWEEN :startTime AND :endTime")
  List<Appointment> findConflictingAppointments(
    @Param("providerId") Long providerId,
    @Param("startTime") LocalDateTime startTime,
    @Param("endTime") LocalDateTime endTime
  );

  /**
   * Search appointments by customer name or service type
   * @param keyword Search keyword
   * @return List of matching appointments
   */
  @Query("SELECT a FROM Appointment a WHERE " +
    "LOWER(a.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(a.serviceProvider.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(CAST(a.serviceType AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(a.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Appointment> searchAppointments(@Param("keyword") String keyword);

  /**
   * Get appointment statistics by provider
   * Returns count of appointments grouped by status
   * @param providerId Provider ID
   * @return List of [status, count] arrays
   */
  @Query("SELECT a.status, COUNT(a) FROM Appointment a " +
    "WHERE a.serviceProvider.id = :providerId GROUP BY a.status")
  List<Object[]> getAppointmentStatsByProvider(@Param("providerId") Long providerId);

  /**
   * Find appointments for today for a provider
   * @param providerId Provider ID
   * @param startOfDay Start of current day
   * @param endOfDay End of current day
   * @return List of today's appointments
   */
  @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
    "AND a.appointmentDateTime BETWEEN :startOfDay AND :endOfDay " +
    "ORDER BY a.appointmentDateTime ASC")
  List<Appointment> findTodayAppointmentsByProvider(
    @Param("providerId") Long providerId,
    @Param("startOfDay") LocalDateTime startOfDay,
    @Param("endOfDay") LocalDateTime endOfDay
  );

  /**
   * Count total appointments for a customer
   * @param customerId Customer ID
   * @return Total count
   */
  Long countByCustomerId(Long customerId);

  /**
   * Count total appointments for a provider
   * @param providerId Provider ID
   * @return Total count
   */
  Long countByServiceProviderId(Long providerId);
}