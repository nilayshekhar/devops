package com.appointment.exception;

import com.appointment.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers
 * Catches exceptions thrown by controllers and converts them to appropriate HTTP responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handle ResourceNotFoundException (404 Not Found)
   * Thrown when a requested resource doesn't exist in the database
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
    ResourceNotFoundException ex,
    WebRequest request) {

    log.error("Resource not found: {}", ex.getMessage());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(ex.getMessage());
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.NOT_FOUND.value());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * Handle AppointmentException (400 Bad Request)
   * Thrown when business logic rules are violated
   */
  @ExceptionHandler(AppointmentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Object>> handleAppointmentException(
    AppointmentException ex,
    WebRequest request) {

    log.error("Appointment exception: {}", ex.getMessage());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(ex.getMessage());
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle validation errors (400 Bad Request)
   * Thrown when @Valid annotation validation fails
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
    MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    log.error("Validation failed: {}", errors);
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("code", "VALIDATION_ERROR");
    errorDetails.put("errors", errors);
    ApiResponse<Map<String, String>> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage("Validation failed. Please check the provided data.");
    response.setErrors(errorDetails);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle type mismatch errors (400 Bad Request)
   * Thrown when path variable or request parameter has wrong type
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(
    MethodArgumentTypeMismatchException ex) {

    String error = String.format("Parameter '%s' should be of type %s",
      ex.getName(),
      ex.getRequiredType().getSimpleName());

    log.error("Type mismatch: {}", error);

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(error);
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle IllegalArgumentException (400 Bad Request)
   * Thrown when method receives invalid arguments
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
    IllegalArgumentException ex) {

    log.error("Illegal argument: {}", ex.getMessage());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(ex.getMessage());
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle IllegalStateException (409 Conflict)
   * Thrown when operation cannot be performed due to current state
   */
  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
    IllegalStateException ex) {

    log.error("Illegal state: {}", ex.getMessage());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(ex.getMessage());
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.CONFLICT.value());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  /**
   * Handle all other unhandled exceptions (500 Internal Server Error)
   * Catch-all for unexpected errors
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiResponse<Object>> handleGlobalException(
    Exception ex,
    WebRequest request) {

    log.error("Unexpected error occurred: ", ex);

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage("An unexpected error occurred. Please try again later or contact support.");
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    // In development, you might want to include the full error
    // Uncomment the line below for development only (remove in production)
    // response.setErrors(ex.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handle NullPointerException (500 Internal Server Error)
   * Should be rare if code is properly written
   */
  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiResponse<Object>> handleNullPointerException(
    NullPointerException ex) {

    log.error("Null pointer exception: ", ex);

    ApiResponse<Object> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage("A server error occurred. The development team has been notified.");
    response.setData(null);
    response.setTimestamp(LocalDateTime.now());
    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}