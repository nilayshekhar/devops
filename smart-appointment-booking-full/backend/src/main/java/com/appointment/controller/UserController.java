package com.appointment.controller;

import com.appointment.dto.ApiResponse;
import com.appointment.dto.UserRequest;
import com.appointment.dto.UserResponse;
import com.appointment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User Management
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

  private final UserService userService;

  /**
   * Get all users
   * GET /api/v1/users
   * Actors: ADMIN
   */
  @GetMapping
  @Operation(summary = "Get all users", description = "Retrieve list of all users")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
  }

  /**
   * Get user by ID
   * GET /api/v1/users/{id}
   * Actors: ADMIN, USER
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
    UserResponse user = userService.getUserById(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "User found", user));
  }

  /**
   * Create new user
   * POST /api/v1/users
   * Actors: PUBLIC
   */
  @PostMapping
  @Operation(summary = "Create user", description = "Register a new user")
  public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
    UserResponse user = userService.createUser(request);
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(new ApiResponse<>(true, "User created successfully", user));
  }

  /**
   * Update user
   * PUT /api/users/{id}
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update user", description = "Update user details")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
    @PathVariable Long id,
    @Valid @RequestBody UserRequest request) {
    UserResponse user = userService.updateUser(id, request);
    return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", user));
  }

  /**
   * Delete user
   * DELETE /api/users/{id}
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user", description = "Delete a user account")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
  }

  /**
   * Get all service providers
   * GET /api/users/providers
   */
  @GetMapping("/providers")
  @Operation(summary = "Get service providers", description = "Get all active service providers")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllServiceProviders() {
    List<UserResponse> providers = userService.getAllServiceProviders();
    return ResponseEntity.ok(new ApiResponse<>(true, "Service providers retrieved", providers));
  }

  /**
   * Get user by email
   * GET /api/users/email/{email}
   */
  @GetMapping("/email/{email}")
  @Operation(summary = "Get user by email", description = "Find user by email address")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
    UserResponse user = userService.getUserByEmail(email);
    return ResponseEntity.ok(new ApiResponse<>(true, "User found", user));
  }

  /**
   * Search users by name
   * GET /api/users/search?name=value
   */
  @GetMapping("/search")
  @Operation(summary = "Search users", description = "Search users by name")
  public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String name) {
    List<UserResponse> users = userService.searchUsersByName(name);
    return ResponseEntity.ok(new ApiResponse<>(true, "Search results", users));
  }
}