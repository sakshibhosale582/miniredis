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

/*
 * Main service class responsible for managing all cache operations.
 * It connects LRU cache, statistics, command history, and persistence.
 */
@Service
public class CacheManager {

    // Logger is used to record important cache activities and errors.
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final LRUCache lruCache;
    private final CacheStats stats;
    private final CommandHistory commandHistory;
    private final PersistenceManager persistenceManager;

    /*
     * Spring injects cache configuration such as maximum cache size,
     * persistence file location, and command history size.
     */
    @Autowired
    public CacheManager(CacheProperties properties) {
        this(properties.getMaxSize(), properties.getPersistenceFile(), properties.getHistorySize());
    }

    // Initializes all main components required by the cache manager.
    public CacheManager(int maxCacheSize, String persistenceFile, int historySize) {
        this.lruCache = new LRUCache(maxCacheSize);
        this.stats = new CacheStats();
        this.commandHistory = new CommandHistory(historySize);
        this.persistenceManager = new PersistenceManager(persistenceFile);
    }

    /*
     * Runs automatically after Spring creates the service.
     * Restores previously saved cache data from persistent storage.
     */
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

    // Stores a key-value pair in cache without an expiration time.
    public String set(String key, String value) {
        requireNonBlank(key, "Key");
        requireNonBlank(value, "Value");

        CacheEntry entry = CacheEntry.withoutExpiration(value);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim());

        logger.info("Key '{}' stored successfully.", key.trim());

        return "OK";
    }

    // Stores a key-value pair with a TTL (Time To Live) in seconds.
    public String set(String key, String value, long ttlSeconds) {
        requireNonBlank(key, "Key");
        requireNonBlank(value, "Value");

        // TTL must always be greater than zero.
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be a positive number of seconds.");
        }

        CacheEntry entry = CacheEntry.withTtl(value, ttlSeconds);
        lruCache.put(key.trim(), entry);
        commandHistory.record("SET " + key.trim() + " EX " + ttlSeconds);

        logger.info("Key '{}' stored with TTL {} seconds.", key.trim(), ttlSeconds);

        return "OK";
    }

    /*
     * Retrieves a value from the cache.
     * Also tracks cache hits, misses, and removes expired entries.
     */
    public Optional<String> get(String key) {
        requireNonBlank(key, "Key");

        // Counts every GET request for cache statistics.
        stats.recordRequest();

        String normalizedKey = key.trim();
        Optional<CacheEntry> entryOptional = lruCache.get(normalizedKey);

        // Cache miss occurs when the requested key does not exist.
        if (entryOptional.isEmpty()) {
            stats.recordMiss();
            commandHistory.record("GET " + normalizedKey);
            logger.debug("Key '{}' not found.", normalizedKey);
            return Optional.empty();
        }

        CacheEntry entry = entryOptional.get();

        // Expired entries are automatically removed and counted as a miss.
        if (entry.isExpired()) {
            lruCache.remove(normalizedKey);
            stats.recordExpiredKeysRemoved(1);
            stats.recordMiss();
            commandHistory.record("GET " + normalizedKey);
            logger.debug("Key '{}' expired and was removed.", normalizedKey);
            return Optional.empty();
        }

        // Records a cache hit when valid data is successfully found.
        stats.recordHit();
        commandHistory.record("GET " + normalizedKey);
        logger.debug("Key '{}' retrieved successfully.", normalizedKey);
        return Optional.of(entry.getValue());
    }

    // Deletes a key from the cache and returns whether deletion succeeded.
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

    /*
     * Checks whether a valid key exists.
     * If the key has expired, it is removed automatically.
     */
    public boolean exists(String key) {
        requireNonBlank(key, "Key");

        String normalizedKey = key.trim();
        Optional<CacheEntry> entryOptional = lruCache.get(normalizedKey);

        if (entryOptional.isEmpty()) {
            logger.warn("Exists check failed. Key '{}' not found.", normalizedKey);
            return false;
        }

        CacheEntry entry = entryOptional.get();

        // Remove the entry if its TTL has expired.
        if (entry.isExpired()) {
            lruCache.remove(normalizedKey);
            stats.recordExpiredKeysRemoved(1);

            logger.warn("Key '{}' expired during exists check.", normalizedKey);

            return false;
        }

        logger.info("Exists check passed for key '{}'.", normalizedKey);

        return true;
    }

    // Returns all active keys after removing expired entries.
    public List<String> keys() {
        purgeExpiredEntries();

        logger.info("Retrieved all cache keys.");

        return lruCache.keys();
    }

    // Removes every key-value pair from the cache.
    public void clear() {
        lruCache.clear();
        commandHistory.record("CLEAR");

        logger.info("Cache cleared successfully.");
    }

    // Returns the number of active entries currently stored.
    public int size() {
        purgeExpiredEntries();

        logger.info("Cache size requested.");

        return lruCache.size();
    }

    // Returns cache statistics such as hits, misses, and current size.
    public StatsResponse stats() {
        purgeExpiredEntries();
        return stats.toResponse(lruCache.size());
    }

    // Saves a snapshot of the current cache to persistent storage.
    public void save() throws IOException {
        persistenceManager.save(lruCache.snapshot());
        commandHistory.record("SAVE");

        logger.info("Cache saved successfully.");
    }

    // Loads previously saved cache data from persistent storage.
    public void load() {
        Map<String, CacheEntry> restored = persistenceManager.load();
        lruCache.replaceAll(restored);
        commandHistory.record("LOAD");

        logger.info("Cache loaded successfully. {} entries restored.", restored.size());
    }

    // Returns the recently executed cache commands.
    public List<String> history() {
        logger.info("Command history requested.");
        return commandHistory.getHistory();
    }

    // Finds and removes all expired cache entries.
    public int cleanupExpired() {
        int removed = lruCache.removeExpiredEntries();

        // Updates statistics with the number of expired keys removed.
        stats.recordExpiredKeysRemoved(removed);

        if (removed > 0) {
            logger.info("Removed {} expired cache entries.", removed);
        }

        return removed;
    }

    // Helper method used before operations that require only active entries.
    private void purgeExpiredEntries() {
        cleanupExpired();
    }

    // Validates that keys and values are not null, empty, or only spaces.
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
}