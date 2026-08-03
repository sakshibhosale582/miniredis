package com.sakshi.miniredis.controller;

import java.io.IOException;
import java.util.List;

import com.sakshi.miniredis.cache.CacheManager;
import com.sakshi.miniredis.dto.SetRequest;
import com.sakshi.miniredis.dto.StatsResponse;
import com.sakshi.miniredis.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * REST Controller that exposes cache APIs.
 * It receives HTTP requests from clients and delegates
 * the business logic to CacheManager.
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    // Service responsible for all cache operations.
    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Simple endpoint to verify that the API is running.
    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "API is running", "Mini Redis API is Running"));
    }

    /*
     * Stores a key-value pair in the cache.
     * If TTL is provided, the entry is stored with expiration time.
     */
    @PostMapping("/set")
    public ResponseEntity<ApiResponse<Void>> set(@Valid @RequestBody SetRequest request) {

        // Store with TTL if provided.
        if (request.getTtlSeconds() != null) {
            cacheManager.set(request.getKey(), request.getValue(), request.getTtlSeconds());
        } else {

            // Store without expiration.
            cacheManager.set(request.getKey(), request.getValue());
        }

        // Returns HTTP 201 (Created) after successful insertion.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Key stored successfully", null));
    }

    // Retrieves the value associated with the given key.
    @GetMapping("/get/{key}")
    public ResponseEntity<ApiResponse<String>> get(@PathVariable String key) {

        return cacheManager.get(key)

                // Return value if key exists.
                .map(value -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Key retrieved successfully", value)))

                // Return 404 if key is not found.
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Key not found", null)));
    }

    // Deletes the specified key from the cache.
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String key) {

        boolean deleted = cacheManager.delete(key);

        if (deleted) {
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Key deleted successfully", null));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Key not found", null));
    }

    // Checks whether a key exists in the cache.
    @GetMapping("/exists/{key}")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable String key) {

        boolean exists = cacheManager.exists(key);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Exists check completed", exists));
    }

    // Returns all active cache keys.
    @GetMapping("/keys")
    public ResponseEntity<ApiResponse<List<String>>> keys() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Keys fetched successfully", cacheManager.keys()));
    }

    // Returns cache statistics such as hits, misses, and active keys.
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StatsResponse>> stats() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Statistics fetched successfully",
                        cacheManager.stats()));
    }

    // Returns recently executed cache commands.
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<String>>> history() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "History fetched successfully", cacheManager.history()));
    }

    // Saves the current cache to persistent storage.
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> save() throws IOException {

        cacheManager.save();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache saved successfully", null));
    }

    // Loads previously saved cache data.
    @PostMapping("/load")
    public ResponseEntity<ApiResponse<Void>> load() {

        cacheManager.load();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache loaded successfully", null));
    }

    // Removes all cache entries.
    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clear() {

        cacheManager.clear();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache cleared successfully", null));
    }

    // Health check endpoint used to verify API availability.
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(new ApiResponse<>(true, "API is healthy", "UP"));
    }
}