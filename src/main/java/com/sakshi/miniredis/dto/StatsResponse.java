package com.sakshi.miniredis.dto;

public class StatsResponse {

    private long totalRequests;
    private long cacheHits;
    private long cacheMisses;
    private long activeKeys;
    private long expiredKeysRemoved;
    private double hitRatio;

    public StatsResponse(long totalRequests,
                         long cacheHits,
                         long cacheMisses,
                         long activeKeys,
                         long expiredKeysRemoved,
                         double hitRatio) {

        this.totalRequests = totalRequests;
        this.cacheHits = cacheHits;
        this.cacheMisses = cacheMisses;
        this.activeKeys = activeKeys;
        this.expiredKeysRemoved = expiredKeysRemoved;
        this.hitRatio = hitRatio;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getCacheHits() {
        return cacheHits;
    }

    public long getCacheMisses() {
        return cacheMisses;
    }

    public long getActiveKeys() {
        return activeKeys;
    }

    public long getExpiredKeysRemoved() {
        return expiredKeysRemoved;
    }

    public double getHitRatio() {
        return hitRatio;
    }
}