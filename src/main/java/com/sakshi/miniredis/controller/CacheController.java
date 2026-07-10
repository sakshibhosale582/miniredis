package com.sakshi.miniredis.controller;

import cache.CacheManager;
import com.sakshi.miniredis.dto.SetRequest;
import com.sakshi.miniredis.dto.StatsResponse;
import com.sakshi.miniredis.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sakshi.miniredis.dto.StatsResponse;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "API is running", "Mini Redis API is Running"));
    }

    @PostMapping("/set")
    public ResponseEntity<ApiResponse<Void>> set(@Valid @RequestBody SetRequest request) {

        cacheManager.set(request.getKey(), request.getValue());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Key stored successfully", null));
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<ApiResponse<String>> get(@PathVariable String key) {

        return cacheManager.get(key)
                .map(value -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Key retrieved successfully", value)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Key not found", null)));
    }

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

    @GetMapping("/exists/{key}")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable String key) {

        boolean exists = cacheManager.exists(key);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Exists check completed", exists));
    }

    @GetMapping("/keys")
    public ResponseEntity<ApiResponse<Object>> keys() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Keys fetched successfully", cacheManager.keys()));
    }

    @GetMapping("/stats")
public ResponseEntity<ApiResponse<StatsResponse>> stats() {

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    "Statistics fetched successfully",
                    cacheManager.stats()
            )
    );
}
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> history() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "History fetched successfully", cacheManager.history()));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> save() throws Exception {

        cacheManager.save();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache saved successfully", null));
    }

    @PostMapping("/load")
    public ResponseEntity<ApiResponse<Void>> load() {

        cacheManager.load();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache loaded successfully", null));
    }

    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clear() {

        cacheManager.clear();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cache cleared successfully", null));
    }
}