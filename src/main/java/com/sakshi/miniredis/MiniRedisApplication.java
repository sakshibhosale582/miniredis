package com.sakshi.miniredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * Main entry point of the Mini Redis Spring Boot application.
 */
@SpringBootApplication

/*
 * Automatically scans and registers all classes
 * annotated with @ConfigurationProperties.
 */
@ConfigurationPropertiesScan
public class MiniRedisApplication {

    /*
     * Application execution starts from the main() method.
     * Spring Boot initializes the application context,
     * creates all required beans, and starts the embedded server.
     */
    public static void main(String[] args) {
        SpringApplication.run(MiniRedisApplication.class, args);
    }
}