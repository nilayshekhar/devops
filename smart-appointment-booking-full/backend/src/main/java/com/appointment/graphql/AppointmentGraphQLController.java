package com.appointment.graphql;

import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.UserResponse;
import com.appointment.service.AppointmentService;
import com.appointment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    @QueryMapping
    public List<AppointmentResponse> appointments() {
        return appointmentService.getAllAppointments();
    }

    @QueryMapping
    public AppointmentResponse appointment(@Argument Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @QueryMapping
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public UserResponse user(@Argument Long id) {
        return userService.getUserById(id);
    }

    @MutationMapping
    public AppointmentResponse createAppointment(@Argument Long customerId, @Argument Long providerId, @Argument String serviceType, @Argument String appointmentDateTime, @Argument String notes) {
        // Convert serviceType string to enum
        com.appointment.model.Appointment.ServiceType type = com.appointment.model.Appointment.ServiceType.valueOf(serviceType);
        // Convert appointmentDateTime string to LocalDateTime
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(appointmentDateTime);
        com.appointment.dto.AppointmentRequest request = new com.appointment.dto.AppointmentRequest(customerId, providerId, type, dateTime, notes);
        return appointmentService.createAppointment(request);
    }

    @MutationMapping
    public UserResponse createUser(@Argument String name, @Argument String email, @Argument String phone, @Argument String password) {
        com.appointment.dto.UserRequest request = new com.appointment.dto.UserRequest(name, email, password, phone, com.appointment.model.User.Role.CUSTOMER);
        return userService.createUser(request);
    }
}
