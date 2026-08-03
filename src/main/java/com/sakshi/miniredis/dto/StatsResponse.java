package com.sakshi.miniredis.dto;

/*
 * DTO used to send cache statistics
 * from the backend to the client.
 */
public class StatsResponse {

    // Total number of cache requests received.
    private long totalRequests;

    // Number of successful cache hits.
    private long cacheHits;

    // Number of cache misses.
    private long cacheMisses;

    // Number of currently active cache keys.
    private long activeKeys;

    // Total expired keys removed from the cache.
    private long expiredKeysRemoved;

    // Cache hit ratio in percentage.
    private double hitRatio;

    // Creates a statistics response with all cache metrics.
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

    // Returns total cache requests.
    public long getTotalRequests() {
        return totalRequests;
    }

    // Returns total cache hits.
    public long getCacheHits() {
        return cacheHits;
    }

    // Returns total cache misses.
    public long getCacheMisses() {
        return cacheMisses;
    }

    // Returns number of active keys.
    public long getActiveKeys() {
        return activeKeys;
    }

    // Returns number of expired keys removed.
    public long getExpiredKeysRemoved() {
        return expiredKeysRemoved;
    }

    // Returns cache hit ratio.
    public double getHitRatio() {
        return hitRatio;
    }

    /*
     * Returns a formatted string representation
     * of all cache statistics.
     */
    @Override
    public String toString() {
        return String.format(
                "Total Requests: %d%n" +
                "Cache Hits: %d%n" +
                "Cache Misses: %d%n" +
                "Active Keys: %d%n" +
                "Expired Keys Removed: %d%n" +
                "Hit Ratio: %.0f%%",
                totalRequests,
                cacheHits,
                cacheMisses,
                activeKeys,
                expiredKeysRemoved,
                hitRatio
        );
    }
}