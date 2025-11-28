package com.appointment.controller;

import com.appointment.dto.ApiResponse;
import com.appointment.dto.UserRequest;
import com.appointment.dto.UserResponse;
import com.appointment.model.User;
import com.appointment.service.UserService;
import com.appointment.service.AppointmentCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final com.appointment.repository.UserRepository userRepository;
    private final UserService userService;
    private final AppointmentCleanupService appointmentCleanupService;

    /**
     * Login endpoint: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserRequest request) {
        // Only validate email and password for login
        String email = request.getEmail();
        String password = request.getPassword();
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Email and password are required", null));
        }
        com.appointment.model.User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Invalid email or password", null));
        }
        // Cleanup expired unconfirmed appointments on login
        int removed = appointmentCleanupService.removeExpiredUnconfirmedAppointments();
        if (removed > 0) {
            // Optionally log or handle removed count
        }
        UserResponse response = userService.getUserByEmail(user.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }
}
