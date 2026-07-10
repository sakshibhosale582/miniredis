package com.sakshi.miniredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "com.sakshi.miniredis",
        "cache"
    }
)
public class MiniRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniRedisApplication.class, args);
    }
}