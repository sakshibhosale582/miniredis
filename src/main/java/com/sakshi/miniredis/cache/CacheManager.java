package com.sakshi.miniredis.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sakshi.miniredis.config.CacheProperties;
import com.sakshi.miniredis.dto.StatsResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final LRUCache lruCache;
    private final CacheStats stats;
    private final CommandHistory commandHistory;
    private final PersistenceManager persistenceManager;

    @Autowired
    public CacheManager(CacheProperties properties) {
        this(properties.getMaxSize(), properties.getPersistenceFile(), properties.getHistorySize());
    }

    public CacheManager(int maxCacheSize, String persistenceFile, int historySize) {
        this.lruCache = new LRUCache(maxCacheSize);
        this.stats = new CacheStats();
        this.commandHistory = new CommandHistory(historySize);
        this.persistenceManager = new PersistenceManager(persistenceFile);
    }

    @PostConstruct
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
        requireNonBlank(key, "Key");
        requireNonBlank(value, "Value");

        CacheEntry entry = CacheEntry.withoutExpiration(value);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim());

        logger.info("Key '{}' stored successfully.", key.trim());

        return "OK";
    }

    public String set(String key, String value, long ttlSeconds) {
        requireNonBlank(key, "Key");
        requireNonBlank(value, "Value");

        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be a positive number of seconds.");
        }

        CacheEntry entry = CacheEntry.withTtl(value, ttlSeconds);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim() + " EX " + ttlSeconds);

        logger.info("Key '{}' stored with TTL {} seconds.", key.trim(), ttlSeconds);

        return "OK";
    }

    public Optional<String> get(String key) {
        requireNonBlank(key, "Key");
        stats.recordRequest();

        String normalizedKey = key.trim();
        Optional<CacheEntry> entryOptional = lruCache.get(normalizedKey);

        if (entryOptional.isEmpty()) {
            stats.recordMiss();
            commandHistory.record("GET " + normalizedKey);
            logger.debug("Key '{}' not found.", normalizedKey);
            return Optional.empty();
        }

        CacheEntry entry = entryOptional.get();
        if (entry.isExpired()) {
            lruCache.remove(normalizedKey);
            stats.recordExpiredKeysRemoved(1);
            stats.recordMiss();
            commandHistory.record("GET " + normalizedKey);
            logger.debug("Key '{}' expired and was removed.", normalizedKey);
            return Optional.empty();
        }

        stats.recordHit();
        commandHistory.record("GET " + normalizedKey);
        logger.debug("Key '{}' retrieved successfully.", normalizedKey);
        return Optional.of(entry.getValue());
    }

    public boolean delete(String key) {
        requireNonBlank(key, "Key");

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
        requireNonBlank(key, "Key");

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

    private void purgeExpiredEntries() {
        cleanupExpired();
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
}
