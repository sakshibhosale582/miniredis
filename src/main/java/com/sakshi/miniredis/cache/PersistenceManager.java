package com.sakshi.miniredis.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Handles persistence of cache data.
 * It saves cache entries to a file and restores them when required.
 */
public class PersistenceManager {

    // Logger records persistence-related information and errors.
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    // Stores the path of the file where cache data will be saved.
    private final Path persistencePath;

    // Creates PersistenceManager using the configured persistence file.
    public PersistenceManager(String fileName) {
        this.persistencePath = Path.of(fileName);
    }

    /*
     * Saves active cache entries to disk using Java serialization.
     * Expired entries are removed before saving.
     */
    public void save(Map<String, CacheEntry> entries) throws IOException {

        // Keep only non-expired entries.
        Map<String, CacheEntry> activeEntries = filterActiveEntries(entries);

        Path parent = persistencePath.getParent();

        // Creates parent directories if they do not already exist.
        if (parent != null) {
            Files.createDirectories(parent);
        }

        /*
         * Data is first written to a temporary file.
         * This helps avoid damaging the main persistence file during saving.
         */
        Path tempFile = persistencePath.resolveSibling(persistencePath.getFileName() + ".tmp");

        // ObjectOutputStream serializes and writes cache entries to the file.
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
            outputStream.writeObject(activeEntries);
            outputStream.flush();
        }

        // Replaces the old persistence file with the newly saved file.
        Files.move(tempFile, persistencePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /*
     * Suppresses the unchecked cast warning because loaded serialized
     * data is validated manually before being used.
     */
    @SuppressWarnings("unchecked")

    /*
     * Loads previously saved cache data from disk.
     * Invalid or expired entries are not restored.
     */
    public Map<String, CacheEntry> load() {

        // If persistence file does not exist, start with an empty cache.
        if (!Files.exists(persistencePath)) {
            logger.info("Persistence file not found. Starting with an empty cache.");
            return new HashMap<>();
        }

        // ObjectInputStream reads and deserializes objects from the file.
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(persistencePath))) {

            Object data = inputStream.readObject();

            // Ensures that the loaded object is actually a Map.
            if (!(data instanceof Map<?, ?> rawMap)) {
                logger.warn("Persistence file format is invalid. Starting with an empty cache.");
                return new HashMap<>();
            }

            Map<String, CacheEntry> loaded = new HashMap<>();

            // Validate every key and CacheEntry before restoring it.
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {

                if (entry.getKey() instanceof String key && entry.getValue() instanceof CacheEntry cacheEntry) {

                    // Expired entries are ignored during restoration.
                    if (!cacheEntry.isExpired()) {
                        loaded.put(key, cacheEntry);
                    }
                }
            }

            return loaded;

        } catch (IOException | ClassNotFoundException ex) {

            // If loading fails, application continues with an empty cache.
            logger.warn("Failed to load persistence file. Starting with an empty cache.", ex);
            return new HashMap<>();
        }
    }

    // Returns the path where cache data is stored.
    public Path getPersistencePath() {
        return persistencePath;
    }

    /*
     * Filters the cache and returns only active (non-expired) entries.
     * This prevents expired data from being saved to disk.
     */
    private Map<String, CacheEntry> filterActiveEntries(Map<String, CacheEntry> entries) {

        Map<String, CacheEntry> activeEntries = new HashMap<>();

        for (Map.Entry<String, CacheEntry> entry : entries.entrySet()) {

            if (!entry.getValue().isExpired()) {
                activeEntries.put(entry.getKey(), entry.getValue());
            }
        }

        return activeEntries;
    }
}