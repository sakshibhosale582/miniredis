package cache;

import java.util.concurrent.atomic.AtomicLong;

public class CacheStats {

    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong expiredKeysRemoved = new AtomicLong();

    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public void recordHit() {
        cacheHits.incrementAndGet();
    }

    public void recordMiss() {
        cacheMisses.incrementAndGet();
    }

    public void recordExpiredKeysRemoved(int count) {
        if (count > 0) {
            expiredKeysRemoved.addAndGet(count);
        }
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getCacheHits() {
        return cacheHits.get();
    }

    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public long getExpiredKeysRemoved() {
        return expiredKeysRemoved.get();
    }

    public double getHitRatio() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        if (total == 0) {
            return 0.0;
        }
        return (hits * 100.0) / total;
    }

    public void reset() {
        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        expiredKeysRemoved.set(0);
    }

    @Override
    public String toString() {
        return String.format(
                "Total Requests: %d%nHits: %d%nMisses: %d%nActive Keys: %d%nExpired Keys Removed: %d%nHit Ratio: %.0f%%",
                getTotalRequests(),
                getCacheHits(),
                getCacheMisses(),
                0,
                getExpiredKeysRemoved(),
                getHitRatio());
    }

    public String formatWithActiveKeys(int activeKeys) {
        return String.format(
                "Total Requests: %d%nHits: %d%nMisses: %d%nActive Keys: %d%nExpired Keys Removed: %d%nHit Ratio: %.0f%%",
                getTotalRequests(),
                getCacheHits(),
                getCacheMisses(),
                activeKeys,
                getExpiredKeysRemoved(),
                getHitRatio());
    }
}
