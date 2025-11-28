package com.appointment;

import com.appointment.controller.AppointmentController;
import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
import com.appointment.model.Appointment;
import com.appointment.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AppointmentController
 */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {
	@Test
	void testUpdateAppointment_Valid() throws Exception {
		when(appointmentService.updateAppointment(eq(1L), any(AppointmentRequest.class))).thenReturn(appointmentResponse);
		mockMvc.perform(put("/api/v1/appointments/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(appointmentRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	void testUpdateAppointment_InvalidId() throws Exception {
		when(appointmentService.updateAppointment(eq(99L), any(AppointmentRequest.class))).thenThrow(new com.appointment.exception.ResourceNotFoundException("Appointment not found"));
		mockMvc.perform(put("/api/v1/appointments/99")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(appointmentRequest)))
			.andExpect(status().isNotFound());
	}

	@Test
	void testDeleteAppointment_Valid() throws Exception {
		mockMvc.perform(delete("/api/v1/appointments/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	void testDeleteAppointment_InvalidId() throws Exception {
		doThrow(new com.appointment.exception.ResourceNotFoundException("Appointment not found")).when(appointmentService).deleteAppointment(99L);
		mockMvc.perform(delete("/api/v1/appointments/99"))
			.andExpect(status().isNotFound());
	}

	@Test
	void testCreateAppointment_InvalidProvider() throws Exception {
		when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenThrow(new com.appointment.exception.AppointmentException("Selected user is not a service provider"));
		mockMvc.perform(post("/api/v1/appointments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(appointmentRequest)))
			.andExpect(status().isBadRequest());
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AppointmentService appointmentService;

	private AppointmentResponse appointmentResponse;
	private AppointmentRequest appointmentRequest;

	@BeforeEach
	void setUp() {
		appointmentResponse = new AppointmentResponse();
		appointmentResponse.setId(1L);
		appointmentResponse.setCustomerId(1L);
		appointmentResponse.setCustomerName("John Doe");
		appointmentResponse.setServiceProviderId(2L);
		appointmentResponse.setServiceProviderName("Dr. Smith");
		appointmentResponse.setServiceType(Appointment.ServiceType.DOCTOR);
		appointmentResponse.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
		appointmentResponse.setStatus(Appointment.Status.PENDING);

		appointmentRequest = new AppointmentRequest();
		appointmentRequest.setCustomerId(1L);
		appointmentRequest.setServiceProviderId(2L);
		appointmentRequest.setServiceType(Appointment.ServiceType.DOCTOR);
		appointmentRequest.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
	}

	@Test
	void testGetAllAppointments() throws Exception {
		List<AppointmentResponse> appointments = Arrays.asList(appointmentResponse);
		when(appointmentService.getAllAppointments()).thenReturn(appointments);

		mockMvc.perform(get("/api/v1/appointments"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data[0].customerName").value("John Doe"));
	}

	@Test
	void testGetAppointmentById() throws Exception {
		when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentResponse);

		mockMvc.perform(get("/api/v1/appointments/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.customerName").value("John Doe"));
	}

	@Test
	void testCreateAppointment() throws Exception {
		when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenReturn(appointmentResponse);

			mockMvc.perform(post("/api/v1/appointments")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(appointmentRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.customerName").value("John Doe"));
	}

}
