package com.sakshi.miniredis;

import com.sakshi.miniredis.cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit tests for CacheManager.
 * Verifies that all major cache operations work correctly.
 */
public class CacheManagerTest {

    private CacheManager cache;

    /*
     * Runs before every test case.
     * Creates a fresh CacheManager instance so tests remain independent.
     */
    @BeforeEach
    void setup() {
        cache = new CacheManager(1000, "target/test-cache.dat", 20);
    }

    // Tests storing and retrieving a key-value pair.
    @Test
    void testSetAndGet() {
        cache.set("name", "Rohit");
        assertEquals("Rohit", cache.get("name").orElse(null));
    }

    // Tests deleting a key from the cache.
    @Test
    void testDelete() {
        cache.set("city", "Nashik");
        assertTrue(cache.delete("city"));
        assertFalse(cache.exists("city"));
    }

    // Tests whether an existing key is detected correctly.
    @Test
    void testExists() {
        cache.set("country", "India");
        assertTrue(cache.exists("country"));
    }

    // Tests clearing all cache entries.
    @Test
    void testClear() {
        cache.set("a", "1");
        cache.set("b", "2");
        cache.clear();
        assertEquals(0, cache.size());
    }

    // Tests retrieval of all stored keys.
    @Test
    void testKeys() {
        cache.set("one", "1");
        cache.set("two", "2");

        assertEquals(2, cache.keys().size());
        assertTrue(cache.keys().contains("one"));
        assertTrue(cache.keys().contains("two"));
    }

    // Tests behavior when requesting a non-existing key.
    @Test
    void testKeyNotFound() {
        assertTrue(cache.get("unknown").isEmpty());
    }

    // Tests storing a key with TTL before it expires.
    @Test
    void testSetWithTtlIsRetrievableBeforeExpiry() {
        cache.set("session", "abc123", 60);
        assertEquals("abc123", cache.get("session").orElse(null));
        assertTrue(cache.exists("session"));
    }

    // Tests automatic expiration of a key after TTL.
    @Test
    void testSetWithTtlExpiresAfterDuration() throws InterruptedException {
        cache.set("otp", "9821", 1);
        assertTrue(cache.exists("otp"));

        // Wait for the TTL to expire.
        Thread.sleep(1100);

        assertTrue(cache.get("otp").isEmpty());
        assertFalse(cache.exists("otp"));
    }

    // Tests that an invalid TTL throws an exception.
    @Test
    void testSetWithNonPositiveTtlThrows() {
        assertThrows(IllegalArgumentException.class, () -> cache.set("bad", "value", 0));
    }
}