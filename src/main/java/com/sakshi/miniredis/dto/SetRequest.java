package com.sakshi.miniredis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SetRequest {

    @NotBlank(message = "Key cannot be empty")
    @Size(max = 50, message = "Key cannot be longer than 50 characters")
    private String key;

    @NotBlank(message = "Value cannot be empty")
    @Size(max = 500, message = "Value cannot be longer than 500 characters")
    private String value;

    public SetRequest() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}