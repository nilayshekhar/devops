package com.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "http://18.61.253.86:3000",
                "http://18.61.253.86:8080",
                "http://18.60.44.0:3000",
                "http://ec2-18-60-44-0.ap-south-2.compute.amazonaws.com:3000",
                "http://18.60.44.0:8080",
                "http://ec2-18-60-44-0.ap-south-2.compute.amazonaws.com:8080",
                "http://localhost:8080",
                "http://localhost:30080"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
            }
        };
    }
}
