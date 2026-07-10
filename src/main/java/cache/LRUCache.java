package cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache {

    private final int maxSize;
    private final LinkedHashMap<String, CacheEntry> store;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max cache size must be positive.");
        }
        this.maxSize = maxSize;
        this.store = new LinkedHashMap<>(maxSize, 0.75f, true);
    }

    public void put(String key, CacheEntry entry) {
        lock.writeLock().lock();
        try {
            store.put(key, entry);
            evictIfNecessary();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<CacheEntry> get(String key) {
        lock.writeLock().lock();
        try {
            CacheEntry entry = store.get(key);
            return Optional.ofNullable(entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean remove(String key) {
        lock.writeLock().lock();
        try {
            return store.remove(key) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean containsKey(String key) {
        lock.readLock().lock();
        try {
            return store.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return store.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            store.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> keys() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(store.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, CacheEntry> snapshot() {
        lock.readLock().lock();
        try {
            return Map.copyOf(store);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void replaceAll(Map<String, CacheEntry> entries) {
        lock.writeLock().lock();
        try {
            store.clear();
            store.putAll(entries);
            evictIfNecessary();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int removeExpiredEntries() {
        lock.writeLock().lock();
        try {
            int removed = 0;
            var iterator = store.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().isExpired()) {
                    iterator.remove();
                    removed++;
                }
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    private void evictIfNecessary() {
        while (store.size() > maxSize) {
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
