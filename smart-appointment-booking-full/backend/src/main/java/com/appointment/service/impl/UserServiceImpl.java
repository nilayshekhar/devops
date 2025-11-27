package com.appointment.service.impl;

import com.appointment.dto.UserRequest;
import com.appointment.dto.UserResponse;
import com.appointment.exception.AppointmentException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.model.User;
import com.appointment.repository.UserRepository;
import com.appointment.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    log.info("Fetching all users");
    // Default sorting: most recently created users first
    return userRepository.findAll().stream()
      .sorted(Comparator.comparing(User::getCreatedAt).reversed())
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    log.info("Fetching user with id: {}", id);
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    return convertToResponse(user);
  }

  @Override
  public UserResponse createUser(UserRequest request) {
    log.info("Creating new user with email: {}", request.getEmail());

    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AppointmentException("Email already registered: " + request.getEmail());
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword()); // In production, hash the password
    user.setPhone(request.getPhone());
    user.setRole(request.getRole() != null ? request.getRole() : User.Role.CUSTOMER);
    user.setActive(true);

    User savedUser = userRepository.save(user);
    log.info("User created successfully with id: {}", savedUser.getId());

    return convertToResponse(savedUser);
  }

  @Override
  public UserResponse updateUser(Long id, UserRequest request) {
    log.info("Updating user with id: {}", id);

    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Update fields
    if (request.getName() != null) {
      user.setName(request.getName());
    }

    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new AppointmentException("Email already in use: " + request.getEmail());
      }
      user.setEmail(request.getEmail());
    }

    if (request.getPassword() != null) {
      user.setPassword(request.getPassword()); // Hash in production
    }

    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }

    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }

    User updatedUser = userRepository.save(user);
    log.info("User updated successfully");

    return convertToResponse(updatedUser);
  }

  @Override
  public void deleteUser(Long id) {
    log.info("Deleting user with id: {}", id);

    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found with id: " + id);
    }

    userRepository.deleteById(id);
    log.info("User deleted successfully");
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> getAllServiceProviders() {
    log.info("Fetching all service providers");
    return userRepository.findAllActiveServiceProviders().stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserByEmail(String email) {
    log.info("Fetching user with email: {}", email);
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return convertToResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> searchUsersByName(String name) {
    log.info("Searching users with name: {}", name);
    return userRepository.findByNameContainingIgnoreCase(name).stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  // Helper method to convert Entity to Response DTO
  private UserResponse convertToResponse(User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setName(user.getName());
    response.setEmail(user.getEmail());
    response.setPhone(user.getPhone());
    response.setRole(user.getRole());
    response.setActive(user.getActive());
    response.setCreatedAt(user.getCreatedAt());
    return response;
  }
}