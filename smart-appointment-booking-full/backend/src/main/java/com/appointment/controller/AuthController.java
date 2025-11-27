package com.appointment.controller;

import com.appointment.dto.ApiResponse;
import com.appointment.dto.UserRequest;
import com.appointment.dto.UserResponse;
import com.appointment.model.User;
import com.appointment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final com.appointment.repository.UserRepository userRepository;
    private final UserService userService;

    /**
     * Login endpoint: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserRequest request) {
        com.appointment.model.User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Invalid email or password", null));
        }
        UserResponse response = userService.getUserByEmail(user.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }
}
