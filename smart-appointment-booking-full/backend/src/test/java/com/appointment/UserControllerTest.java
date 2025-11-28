package com.appointment;

import com.appointment.controller.UserController;
import com.appointment.dto.UserRequest;
import com.appointment.dto.UserResponse;
import com.appointment.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 */
@WebMvcTest(UserController.class)
class UserControllerTest {
  @Test
  void testUpdateUser_Valid() throws Exception {
    when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(userResponse);
    mockMvc.perform(put("/api/v1/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void testUpdateUser_InvalidId() throws Exception {
    when(userService.updateUser(eq(99L), any(UserRequest.class))).thenThrow(new com.appointment.exception.ResourceNotFoundException("User not found"));
    mockMvc.perform(put("/api/v1/users/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteUser_Valid() throws Exception {
    mockMvc.perform(delete("/api/v1/users/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void testDeleteUser_InvalidId() throws Exception {
    doThrow(new com.appointment.exception.ResourceNotFoundException("User not found")).when(userService).deleteUser(99L);
    mockMvc.perform(delete("/api/v1/users/99"))
      .andExpect(status().isNotFound());
  }

  @Test
  void testCreateUser_EmailExists() throws Exception {
    when(userService.createUser(any(UserRequest.class))).thenThrow(new com.appointment.exception.AppointmentException("Email already registered"));
    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isBadRequest());
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  private UserResponse userResponse;
  private UserRequest userRequest;

  @BeforeEach
  void setUp() {
    userResponse = new UserResponse();
    userResponse.setId(1L);
    userResponse.setName("Alice");
    userResponse.setEmail("alice@example.com");
    userResponse.setPhone("1234567890");

    userRequest = new UserRequest();
    userRequest.setName("Alice");
    userRequest.setEmail("alice@example.com");
    userRequest.setPassword("password");
    userRequest.setPhone("1234567890");
  }

  @Test
  void testGetAllUsers() throws Exception {
    List<UserResponse> users = Arrays.asList(userResponse);
    when(userService.getAllUsers()).thenReturn(users);

    mockMvc.perform(get("/api/v1/users"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.data[0].name").value("Alice"));
  }

  @Test
  void testGetUserById() throws Exception {
    when(userService.getUserById(1L)).thenReturn(userResponse);

    mockMvc.perform(get("/api/v1/users/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.data.name").value("Alice"));
  }

  @Test
  void testCreateUser() throws Exception {
    when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.data.name").value("Alice"));
  }
}
