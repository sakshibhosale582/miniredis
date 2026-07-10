package cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.AppConstants;

public class CacheCleaner implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(CacheCleaner.class.getName());

    private final ScheduledExecutorService scheduler;
    private final CacheManager cacheManager;
    private final long intervalSeconds;

    public CacheCleaner(CacheManager cacheManager) {
        this(cacheManager, AppConstants.CLEANUP_INTERVAL_SECONDS);
    }

    public CacheCleaner(CacheManager cacheManager, long intervalSeconds) {
        this.cacheManager = cacheManager;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "cache-cleaner");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::runCleanup, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        LOGGER.info("Cache cleaner started. Running every " + intervalSeconds + " seconds.");
    }

    private void runCleanup() {
        try {
            int removed = cacheManager.cleanupExpired();
            if (removed > 0) {
                LOGGER.info("Background cleanup removed " + removed + " expired key(s).");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Background cleanup failed.", ex);
        }
    }

    @Override
    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
