package com.sakshi.miniredis.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    @Min(1)
    private int maxSize = 1000;

    @Min(1)
    private int historySize = 20;

    @NotBlank
    private String persistenceFile = "./data/mini-redis-cache.dat";

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public String getPersistenceFile() {
        return persistenceFile;
    }

    public void setPersistenceFile(String persistenceFile) {
        this.persistenceFile = persistenceFile;
    }
}
