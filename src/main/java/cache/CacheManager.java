package cache;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sakshi.miniredis.dto.StatsResponse;

import utils.AppConstants;
import utils.ValidationUtils;

@Service
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final LRUCache lruCache;
    private final CacheStats stats;
    private final CommandHistory commandHistory;
    private final PersistenceManager persistenceManager;
    private final ConcurrentHashMap<String, Object> keyLocks = new ConcurrentHashMap<>();

    public CacheManager() {
        this(AppConstants.MAX_CACHE_SIZE, AppConstants.DEFAULT_PERSISTENCE_FILE);
    }

    public CacheManager(int maxCacheSize, String persistenceFile) {
        this.lruCache = new LRUCache(maxCacheSize);
        this.stats = new CacheStats();
        this.commandHistory = new CommandHistory(AppConstants.MAX_HISTORY_SIZE);
        this.persistenceManager = new PersistenceManager(persistenceFile);
    }

    public void initialize() {
        Map<String, CacheEntry> restored = persistenceManager.load();
        if (!restored.isEmpty()) {
            lruCache.replaceAll(restored);
            logger.info("Cache initialized with {} restored entries.", restored.size());
        } else {
            logger.info("No cache data found during initialization.");
        }
    }

    public String set(String key, String value) {
        ValidationUtils.requireNonEmpty(key, "Key");
        ValidationUtils.requireNonEmpty(value, "Value");

        CacheEntry entry = CacheEntry.withoutExpiration(value);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim() + " " + value);

        logger.info("Key '{}' stored successfully.", key.trim());

        return "OK";
    }

    public String set(String key, String value, long ttlSeconds) {
        ValidationUtils.requireNonEmpty(key, "Key");
        ValidationUtils.requireNonEmpty(value, "Value");

        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be a positive number of seconds.");
        }

        CacheEntry entry = CacheEntry.withTtl(value, ttlSeconds);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim() + " " + value + " " + ttlSeconds);

        logger.info("Key '{}' stored with TTL {} seconds.", key.trim(), ttlSeconds);

        return "OK";
    }

    public Optional<String> get(String key) {
        ValidationUtils.requireNonEmpty(key, "Key");
        stats.recordRequest();

        String normalizedKey = key.trim();
        Object lock = keyLocks.computeIfAbsent(normalizedKey, ignored -> new Object());

        synchronized (lock) {
            Optional<CacheEntry> entryOptional = lruCache.get(normalizedKey);

            if (entryOptional.isEmpty()) {
                stats.recordMiss();
                commandHistory.record("GET " + normalizedKey);

                logger.warn("Key '{}' not found.", normalizedKey);

                return Optional.empty();
            }

            CacheEntry entry = entryOptional.get();

            if (entry.isExpired()) {
                lruCache.remove(normalizedKey);
                stats.recordExpiredKeysRemoved(1);
                stats.recordMiss();
                commandHistory.record("GET " + normalizedKey);

                logger.warn("Key '{}' expired and removed.", normalizedKey);

                return Optional.empty();
            }

            stats.recordHit();
            commandHistory.record("GET " + normalizedKey);

            logger.info("Key '{}' retrieved successfully.", normalizedKey);

            return Optional.of(entry.getValue());
        }
    }

    public boolean delete(String key) {
        ValidationUtils.requireNonEmpty(key, "Key");

        String normalizedKey = key.trim();
        boolean removed = lruCache.remove(normalizedKey);

        commandHistory.record("DELETE " + normalizedKey);

        if (removed) {
            logger.info("Key '{}' deleted successfully.", normalizedKey);
        } else {
            logger.warn("Delete failed. Key '{}' not found.", normalizedKey);
        }

        return removed;
    }

    public boolean exists(String key) {
        ValidationUtils.requireNonEmpty(key, "Key");

        String normalizedKey = key.trim();
        Optional<CacheEntry> entryOptional = lruCache.get(normalizedKey);

        if (entryOptional.isEmpty()) {
            logger.warn("Exists check failed. Key '{}' not found.", normalizedKey);
            return false;
        }

        CacheEntry entry = entryOptional.get();

        if (entry.isExpired()) {
            lruCache.remove(normalizedKey);
            stats.recordExpiredKeysRemoved(1);

            logger.warn("Key '{}' expired during exists check.", normalizedKey);

            return false;
        }

        logger.info("Exists check passed for key '{}'.", normalizedKey);

        return true;
    }

    public List<String> keys() {
        purgeExpiredEntries();

        logger.info("Retrieved all cache keys.");

        return lruCache.keys();
    }

    public Map<String, String> displayAll() {
        purgeExpiredEntries();

        Map<String, CacheEntry> snapshot = lruCache.snapshot();
        Map<String, String> result = new LinkedHashMap<>();

        for (Map.Entry<String, CacheEntry> entry : snapshot.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }

        logger.info("Displaying all cache entries.");

        return result;
    }

    public void clear() {
        lruCache.clear();
        commandHistory.record("CLEAR");

        logger.info("Cache cleared successfully.");
    }

    public int size() {
        purgeExpiredEntries();

        logger.info("Cache size requested.");

        return lruCache.size();
    }

    public StatsResponse stats() {

    purgeExpiredEntries();

    return stats.toResponse(lruCache.size());

}
    public void save() throws IOException {
        persistenceManager.save(lruCache.snapshot());
        commandHistory.record("SAVE");

        logger.info("Cache saved successfully.");
    }

    public void load() {
        Map<String, CacheEntry> restored = persistenceManager.load();
        lruCache.replaceAll(restored);
        commandHistory.record("LOAD");

        logger.info("Cache loaded successfully. {} entries restored.", restored.size());
    }

    public List<String> history() {
        logger.info("Command history requested.");
        return commandHistory.getHistory();
    }

    public int cleanupExpired() {
        int removed = lruCache.removeExpiredEntries();
        stats.recordExpiredKeysRemoved(removed);

        if (removed > 0) {
            logger.info("Removed {} expired cache entries.", removed);
        }

        return removed;
    }

    public CacheStats getStats() {
        return stats;
    }

    public LRUCache getLruCache() {
        return lruCache;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    private void purgeExpiredEntries() {
        cleanupExpired();
    }
}