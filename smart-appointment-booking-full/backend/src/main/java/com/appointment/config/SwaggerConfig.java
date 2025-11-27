package com.appointment.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Smart Appointment Booking System API")
        .version("1.0.0")
        .description("REST API for managing appointments with service providers")
        .contact(new Contact()
          .name("Your Name")
          .email("nilayshekhar16@gmail.cn")
          .url("https://github.com/nilayshekhar"))
        .license(new License()
          .name("MIT License")
          .url("https://opensource.org/licenses/MIT")))
      .servers(List.of(
        new Server().url("http://localhost:8080").description("Local Development Server"),
        new Server().url("https://production-url.com").description("Production Server")
      ));
  }
}