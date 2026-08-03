package com.sakshi.miniredis.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * Stores CORS configuration for the application.
 * It defines which frontend origins are allowed to access the backend APIs.
 */
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /*
     * List of frontend URLs allowed to communicate with the backend.
     * localhost:5173 is commonly used by the Vite/React development server.
     */
    private List<String> allowedOrigins = List.of("http://localhost:5173");

    // Returns the list of allowed frontend origins.
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    // Updates the list of allowed frontend origins.
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}