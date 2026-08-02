package com.sakshi.miniredis.cache;

import java.io.Serializable;
import java.util.Objects;

/*
 * Represents a single entry stored inside the cache.
 * Each entry contains a value and an optional expiration time (TTL).
 *
 * Serializable allows CacheEntry objects to be saved to disk
 * and restored later by the persistence system.
 */
public class CacheEntry implements Serializable {

    // Used during serialization to verify class compatibility.
    private static final long serialVersionUID = 1L;

    // Actual value stored in the cache.
    private final String value;

    /*
     * Absolute expiration time in milliseconds.
     * A value of -1 means that the entry does not expire.
     */
    private final long expirationTimeMillis;

    // Creates a cache entry with the given value and expiration time.
    public CacheEntry(String value, long expirationTimeMillis) {

        // Prevents null values from being stored in the cache.
        this.value = Objects.requireNonNull(value, "value");
        this.expirationTimeMillis = expirationTimeMillis;
    }

    /*
     * Factory method for creating an entry without expiration.
     * -1L is used to represent "no expiration".
     */
    public static CacheEntry withoutExpiration(String value) {
        return new CacheEntry(value, -1L);
    }

    /*
     * Creates a cache entry with a TTL (Time To Live).
     * TTL is provided in seconds and converted into milliseconds.
     */
    public static CacheEntry withTtl(String value, long ttlSeconds) {
        long expirationTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        return new CacheEntry(value, expirationTime);
    }

    // Returns the value stored in this cache entry.
    public String getValue() {
        return value;
    }

    // Returns the exact expiration timestamp in milliseconds.
    public long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    // Checks whether this cache entry has an expiration time.
    public boolean hasExpiration() {
        return expirationTimeMillis > 0;
    }

    /*
     * Checks whether the entry has already expired
     * by comparing the current time with its expiration time.
     */
    public boolean isExpired() {
        return hasExpiration() && System.currentTimeMillis() > expirationTimeMillis;
    }

    // Calculates how many seconds of TTL are remaining.
    public long remainingTtlSeconds() {

        // -1 indicates that the entry has no expiration.
        if (!hasExpiration()) {
            return -1L;
        }

        long remaining = (expirationTimeMillis - System.currentTimeMillis()) / 1000L;

        // Prevents returning a negative TTL for an expired entry.
        return Math.max(remaining, 0L);
    }

    /*
     * Two CacheEntry objects are considered equal when both
     * their value and expiration time are the same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CacheEntry that)) {
            return false;
        }
        return expirationTimeMillis == that.expirationTimeMillis
                && value.equals(that.value);
    }

    // Generates a hash code using value and expiration time.
    @Override
    public int hashCode() {
        return Objects.hash(value, expirationTimeMillis);
    }

    /*
     * Provides a readable representation of the cache entry.
     * If TTL exists, the remaining TTL is also displayed.
     */
    @Override
    public String toString() {
        if (hasExpiration()) {
            return value + " (TTL: " + remainingTtlSeconds() + "s remaining)";
        }
        return value;
    }
}