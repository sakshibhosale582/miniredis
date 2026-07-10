package cache;

import java.io.Serializable;
import java.util.Objects;

public class CacheEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String value;
    private final long expirationTimeMillis;

    public CacheEntry(String value, long expirationTimeMillis) {
        this.value = Objects.requireNonNull(value, "value");
        this.expirationTimeMillis = expirationTimeMillis;
    }

    public static CacheEntry withoutExpiration(String value) {
        return new CacheEntry(value, -1L);
    }

    public static CacheEntry withTtl(String value, long ttlSeconds) {
        long expirationTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        return new CacheEntry(value, expirationTime);
    }

    public String getValue() {
        return value;
    }

    public long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    public boolean hasExpiration() {
        return expirationTimeMillis > 0;
    }

    public boolean isExpired() {
        return hasExpiration() && System.currentTimeMillis() > expirationTimeMillis;
    }

    public long remainingTtlSeconds() {
        if (!hasExpiration()) {
            return -1L;
        }
        long remaining = (expirationTimeMillis - System.currentTimeMillis()) / 1000L;
        return Math.max(remaining, 0L);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(value, expirationTimeMillis);
    }

    @Override
    public String toString() {
        if (hasExpiration()) {
            return value + " (TTL: " + remainingTtlSeconds() + "s remaining)";
        }
        return value;
    }
}
