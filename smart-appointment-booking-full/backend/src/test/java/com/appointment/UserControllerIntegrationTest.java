package com.appointment;

import com.appointment.dto.UserRequest;
import com.appointment.model.User;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.appointment.repository.AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateAndGetUser() {
        UserRequest request = new UserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("password");
        request.setPhone("1234567890");
        request.setRole(User.Role.CUSTOMER);

        ResponseEntity<String> postResponse = restTemplate.postForEntity("/api/v1/users", request, String.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertTrue(postResponse.getBody().contains("User created successfully"));

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/v1/users", String.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertTrue(getResponse.getBody().contains("Alice"));
    }
}
