package com.sakshi.miniredis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Configures CORS (Cross-Origin Resource Sharing) rules
 * for the Spring Boot application.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Loads allowed origins from CorsProperties configuration.
    private final CorsProperties corsProperties;

    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    /*
     * Configures which frontend applications are allowed
     * to access the backend REST APIs.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")   // Apply CORS only to API endpoints.

                // Allow requests from configured frontend origins.
                .allowedOriginPatterns(corsProperties.getAllowedOrigins().toArray(String[]::new))

                // Allow only these HTTP methods.
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS")

                // Allow these request headers.
                .allowedHeaders("Content-Type", "Accept")

                // Browser can cache the CORS response for 1 hour (3600 seconds).
                .maxAge(3600);
    }
}