package utils;

public final class AppConstants {

    public static final int MAX_CACHE_SIZE = 1000;
    public static final int MAX_HISTORY_SIZE = 20;
    public static final long CLEANUP_INTERVAL_SECONDS = 30;
    public static final String DEFAULT_PERSISTENCE_FILE = "mini-redis-cache.dat";

    private AppConstants() {
    }
}
