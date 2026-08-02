package com.sakshi.miniredis.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Implements an LRU (Least Recently Used) cache.
 * When the cache becomes full, the least recently used entry is removed.
 */
public class LRUCache {

    // Maximum number of entries allowed in the cache.
    private final int maxSize;

    /*
     * LinkedHashMap stores cache entries.
     * accessOrder=true maintains entries based on their access order,
     * which helps implement LRU behavior.
     */
    private final LinkedHashMap<String, CacheEntry> store;

    // Provides thread-safe read and write operations on the cache.
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // Creates an LRU cache with the specified maximum capacity.
    public LRUCache(int maxSize) {

        // Cache size must always be greater than zero.
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max cache size must be positive.");
        }

        this.maxSize = maxSize;

        /*
         * true enables access-order mode.
         * Recently accessed entries move toward the end of the map.
         */
        this.store = new LinkedHashMap<>(maxSize, 0.75f, true);
    }

    // Adds or updates a key-value entry in the cache.
    public void put(String key, CacheEntry entry) {

        // Write lock prevents conflicting modifications by multiple threads.
        lock.writeLock().lock();

        try {
            store.put(key, entry);

            // Removes least recently used entries if cache exceeds maxSize.
            evictIfNecessary();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Retrieves a cache entry for the given key.
    public Optional<CacheEntry> get(String key) {

        /*
         * Write lock is used because LinkedHashMap is in access-order mode.
         * Calling get() changes the internal access order.
         */
        lock.writeLock().lock();

        try {
            CacheEntry entry = store.get(key);
            return Optional.ofNullable(entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Removes a specific key from the cache.
    public boolean remove(String key) {
        lock.writeLock().lock();
        try {
            return store.remove(key) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Checks whether a key exists in the cache.
    public boolean containsKey(String key) {
        lock.readLock().lock();
        try {
            return store.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    // Returns the current number of entries in the cache.
    public int size() {
        lock.readLock().lock();
        try {
            return store.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    // Removes all entries from the cache.
    public void clear() {
        lock.writeLock().lock();
        try {
            store.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Returns a list containing all cache keys.
    public List<String> keys() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(store.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * Creates a copy of the current cache.
     * Useful for persistence without exposing the original map.
     */
    public Map<String, CacheEntry> snapshot() {
        lock.readLock().lock();
        try {
            return new LinkedHashMap<>(store);
        } finally {
            lock.readLock().unlock();
        }
    }

    // Replaces existing cache data with the provided entries.
    public void replaceAll(Map<String, CacheEntry> entries) {
        lock.writeLock().lock();
        try {
            store.clear();
            store.putAll(entries);

            // Ensures restored data does not exceed maximum cache size.
            evictIfNecessary();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Searches for expired cache entries and removes them.
    public int removeExpiredEntries() {
        lock.writeLock().lock();

        try {
            int removed = 0;

            var iterator = store.entrySet().iterator();

            while (iterator.hasNext()) {
                var entry = iterator.next();

                // Remove entry if its TTL has expired.
                if (entry.getValue().isExpired()) {
                    iterator.remove();
                    removed++;
                }
            }

            // Returns total number of expired entries removed.
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Returns the maximum capacity of the cache.
    public int getMaxSize() {
        return maxSize;
    }

    /*
     * Implements LRU eviction.
     * Removes the least recently used entry when cache exceeds maxSize.
     */
    private void evictIfNecessary() {

        while (store.size() > maxSize) {

            // First entry represents the least recently used item.
            var iterator = store.entrySet().iterator();

            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            } else {
                break;
            }
        }
    }
}