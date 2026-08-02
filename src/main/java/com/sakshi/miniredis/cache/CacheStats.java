package com.sakshi.miniredis.cache;

import com.sakshi.miniredis.dto.StatsResponse;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class tracks important cache performance statistics
 * such as requests, hits, misses, and expired keys.
 */
public class CacheStats {

    // AtomicLong provides thread-safe counters for cache statistics.
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong expiredKeysRemoved = new AtomicLong();

    // Increases the total number of cache requests.
    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    // Increases the counter when requested data is found in cache.
    public void recordHit() {
        cacheHits.incrementAndGet();
    }

    // Increases the counter when requested data is not found.
    public void recordMiss() {
        cacheMisses.incrementAndGet();
    }

    // Adds the number of expired keys removed from the cache.
    public void recordExpiredKeysRemoved(int count) {
        if (count > 0) {
            expiredKeysRemoved.addAndGet(count);
        }
    }

    // Returns total cache requests.
    public long getTotalRequests() {
        return totalRequests.get();
    }

    // Returns total successful cache hits.
    public long getCacheHits() {
        return cacheHits.get();
    }

    // Returns total cache misses.
    public long getCacheMisses() {
        return cacheMisses.get();
    }

    // Returns total number of expired keys removed.
    public long getExpiredKeysRemoved() {
        return expiredKeysRemoved.get();
    }

    /*
     * Calculates cache hit ratio as a percentage.
     * Formula: (Cache Hits / Total Hits + Misses) * 100
     */
    public double getHitRatio() {

        long total = cacheHits.get() + cacheMisses.get();

        // Avoids division by zero when no requests have been made.
        if (total == 0) {
            return 0;
        }

        return (cacheHits.get() * 100.0) / total;
    }

    // Resets all cache statistics back to zero.
    public void reset() {

        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        expiredKeysRemoved.set(0);

    }

    /*
     * Converts internal cache statistics into StatsResponse
     * so they can be returned through the API.
     */
    public StatsResponse toResponse(int activeKeys) {

        return new StatsResponse(

                getTotalRequests(),
                getCacheHits(),
                getCacheMisses(),
                activeKeys,
                getExpiredKeysRemoved(),
                getHitRatio()

        );

    }

}