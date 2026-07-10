package cache;

import com.sakshi.miniredis.dto.StatsResponse;
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

        long total = cacheHits.get() + cacheMisses.get();

        if (total == 0) {
            return 0;
        }

        return (cacheHits.get() * 100.0) / total;
    }

    public void reset() {

        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        expiredKeysRemoved.set(0);

    }

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