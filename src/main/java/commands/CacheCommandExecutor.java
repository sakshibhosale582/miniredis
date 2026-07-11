package commands;

import cache.CacheManager;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import utils.ValidationUtils;

public class CacheCommandExecutor {

    private final CacheManager cacheManager;

    public CacheCommandExecutor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CommandResult executeSet(String key, String value) {
        try {
            return CommandResult.success(cacheManager.set(key, value));
        } catch (IllegalArgumentException ex) {
            return CommandResult.failure(ex.getMessage());
        }
    }

    public CommandResult executeSet(String key, String value, String ttlValue) {
        try {
            long ttl = ValidationUtils.parseTtl(ttlValue);
            return CommandResult.success(cacheManager.set(key, value, ttl));
        } catch (IllegalArgumentException ex) {
            return CommandResult.failure(ex.getMessage());
        }
    }

    public CommandResult executeGet(String key) {
        try {
            Optional<String> value = cacheManager.get(key);
            if (value.isPresent()) {
                return CommandResult.success(value.get());
            }
            return CommandResult.success("(nil)");
        } catch (IllegalArgumentException ex) {
            return CommandResult.failure(ex.getMessage());
        }
    }

    public CommandResult executeDelete(String key) {
        try {
            boolean removed = cacheManager.delete(key);
            return CommandResult.success(removed ? "1" : "0");
        } catch (IllegalArgumentException ex) {
            return CommandResult.failure(ex.getMessage());
        }
    }

    public CommandResult executeExists(String key) {
        try {
            boolean exists = cacheManager.exists(key);
            return CommandResult.success(exists ? "1" : "0");
        } catch (IllegalArgumentException ex) {
            return CommandResult.failure(ex.getMessage());
        }
    }

    public CommandResult executeKeys() {
        List<String> keys = cacheManager.keys();
        if (keys.isEmpty()) {
            return CommandResult.success("(empty)");
        }
        return CommandResult.success(String.join(System.lineSeparator(), keys));
    }

    public CommandResult executeDisplay() {
        Map<String, String> entries = cacheManager.displayAll();
        if (entries.isEmpty()) {
            return CommandResult.success("(empty)");
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            builder.append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }
        return CommandResult.success(builder.toString().trim());
    }

    public CommandResult executeClear() {
        cacheManager.clear();
        return CommandResult.success("Cache cleared.");
    }

    public CommandResult executeSize() {
        return CommandResult.success(String.valueOf(cacheManager.size()));
    }

    public CommandResult executeStats() {
    return CommandResult.success(cacheManager.stats().toString());

    }

    public CommandResult executeSave() {
        try {
            cacheManager.save();
            return CommandResult.success("Cache saved to " + cacheManager.getPersistenceManager().getPersistencePath());
        } catch (IOException ex) {
            return CommandResult.failure("Failed to save cache: " + ex.getMessage());
        }
    }

    public CommandResult executeLoad() {
        cacheManager.load();
        return CommandResult.success("Cache loaded from " + cacheManager.getPersistenceManager().getPersistencePath());
    }

    public CommandResult executeHistory() {
        List<String> history = cacheManager.history();
        if (history.isEmpty()) {
            return CommandResult.success("(empty)");
        }
        return CommandResult.success(String.join(System.lineSeparator(), history));
    }
}
