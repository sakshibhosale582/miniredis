package com.sakshi.miniredis;

import cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {

    private CacheManager cache;

    @BeforeEach
    void setup() {
        cache = new CacheManager();
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
}