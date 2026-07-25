package com.sakshi.miniredis;

import com.sakshi.miniredis.cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {

    private CacheManager cache;

    @BeforeEach
    void setup() {
        cache = new CacheManager(1000, "target/test-cache.dat", 20);
    }

    @Test
    void testSetAndGet() {
        cache.set("name", "Rohit");
        assertEquals("Rohit", cache.get("name").orElse(null));
    }

    @Test
    void testDelete() {
        cache.set("city", "Nashik");
        assertTrue(cache.delete("city"));
        assertFalse(cache.exists("city"));
    }

    @Test
    void testExists() {
        cache.set("country", "India");
        assertTrue(cache.exists("country"));
    }

    @Test
    void testClear() {
        cache.set("a", "1");
        cache.set("b", "2");
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testKeys() {
        cache.set("one", "1");
        cache.set("two", "2");

        assertEquals(2, cache.keys().size());
        assertTrue(cache.keys().contains("one"));
        assertTrue(cache.keys().contains("two"));
    }

    @Test
    void testKeyNotFound() {
        assertTrue(cache.get("unknown").isEmpty());
    }

    @Test
    void testSetWithTtlIsRetrievableBeforeExpiry() {
        cache.set("session", "abc123", 60);
        assertEquals("abc123", cache.get("session").orElse(null));
        assertTrue(cache.exists("session"));
    }

    @Test
    void testSetWithTtlExpiresAfterDuration() throws InterruptedException {
        cache.set("otp", "9821", 1);
        assertTrue(cache.exists("otp"));

        Thread.sleep(1100);

        assertTrue(cache.get("otp").isEmpty());
        assertFalse(cache.exists("otp"));
    }

    @Test
    void testSetWithNonPositiveTtlThrows() {
        assertThrows(IllegalArgumentException.class, () -> cache.set("bad", "value", 0));
    }
}
