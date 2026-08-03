package com.sakshi.miniredis.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/*
 * Stores configuration settings for the Mini Redis cache.
 * Values can be loaded from application.properties using "app.cache" prefix.
 */
@Validated
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    // Maximum number of entries allowed in cache. Minimum value must be 1.
    @Min(1)
    private int maxSize = 1000;

    // Maximum number of commands stored in command history.
    @Min(1)
    private int historySize = 20;

    // File location used by PersistenceManager to save cache data.
    @NotBlank
    private String persistenceFile = "./data/mini-redis-cache.dat";

    // Returns configured maximum cache size.
    public int getMaxSize() {
        return maxSize;
    }

    // Updates maximum cache size.
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    // Returns configured command history size.
    public int getHistorySize() {
        return historySize;
    }

    // Updates command history size.
    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    // Returns the configured persistence file location.
    public String getPersistenceFile() {
        return persistenceFile;
    }

    // Updates the persistence file location.
    public void setPersistenceFile(String persistenceFile) {
        this.persistenceFile = persistenceFile;
    }
}