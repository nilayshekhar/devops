package com.appointment;

import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.UserRequest;
import com.appointment.model.Appointment;
import com.appointment.model.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BusinessFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private User customer;
    private User provider;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();
        customer = new User();
        customer.setName("Jane Customer");
        customer.setEmail("jane@example.com");
        customer.setPassword("password");
        customer.setRole(User.Role.CUSTOMER);
        customer.setActive(true);
        customer = userRepository.save(customer);

        provider = new User();
        provider.setName("Dr. Provider");
        provider.setEmail("provider@example.com");
        provider.setPassword("password");
        provider.setRole(User.Role.SERVICE_PROVIDER);
        provider.setActive(true);
        provider = userRepository.save(provider);
    }

    @Test
    void testFullAppointmentFlow() {
        // Create appointment
        AppointmentRequest request = new AppointmentRequest();
        request.setCustomerId(customer.getId());
        request.setServiceProviderId(provider.getId());
        request.setServiceType(Appointment.ServiceType.DOCTOR);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(2));

        ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/v1/appointments", request, String.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertTrue(postResponse.getBody().contains("Appointment created successfully"));

        // Get appointments by customer
        ResponseEntity<String> getCustomerResponse = restTemplate.getForEntity("/api/v1/appointments/customer/" + customer.getId(), String.class);
        assertEquals(HttpStatus.OK, getCustomerResponse.getStatusCode());
        assertTrue(getCustomerResponse.getBody().contains("Jane Customer"));

        // Get appointments by provider
        ResponseEntity<String> getProviderResponse = restTemplate.getForEntity("/api/v1/appointments/provider/" + provider.getId(), String.class);
        assertEquals(HttpStatus.OK, getProviderResponse.getStatusCode());
        assertTrue(getProviderResponse.getBody().contains("Dr. Provider"));
    }
}
