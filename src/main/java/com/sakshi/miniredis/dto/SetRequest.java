package com.sakshi.miniredis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/*
 * DTO (Data Transfer Object) used to receive
 * key, value, and optional TTL from the client.
 */
public class SetRequest {

    // Cache key cannot be blank and maximum length is 50 characters.
    @NotBlank(message = "Key cannot be empty")
    @Size(max = 50, message = "Key cannot be longer than 50 characters")
    private String key;

    // Cache value cannot be blank and maximum length is 500 characters.
    @NotBlank(message = "Value cannot be empty")
    @Size(max = 500, message = "Value cannot be longer than 500 characters")
    private String value;

    /*
     * Optional TTL (Time To Live) in seconds.
     * If provided, it must be a positive number.
     */
    @Positive(message = "TTL must be a positive number of seconds")
    private Long ttlSeconds;

    // Default constructor required for Spring to create the object.
    public SetRequest() {
    }

    // Returns the cache key.
    public String getKey() {
        return key;
    }

    // Sets the cache key.
    public void setKey(String key) {
        this.key = key;
    }

    // Returns the cache value.
    public String getValue() {
        return value;
    }

    // Sets the cache value.
    public void setValue(String value) {
        this.value = value;
    }

    // Returns the TTL value in seconds.
    public Long getTtlSeconds() {
        return ttlSeconds;
    }

    // Sets the TTL value.
    public void setTtlSeconds(Long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}