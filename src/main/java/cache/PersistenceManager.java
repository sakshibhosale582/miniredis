package cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceManager {

    private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());

    private final Path persistencePath;

    public PersistenceManager(String fileName) {
        this.persistencePath = Path.of(fileName);
    }

    public void save(Map<String, CacheEntry> entries) throws IOException {
        Map<String, CacheEntry> activeEntries = filterActiveEntries(entries);
        Path parent = persistencePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tempFile = persistencePath.resolveSibling(persistencePath.getFileName() + ".tmp");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
            outputStream.writeObject(activeEntries);
            outputStream.flush();
        }
        Files.move(tempFile, persistencePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @SuppressWarnings("unchecked")
    public Map<String, CacheEntry> load() {
        if (!Files.exists(persistencePath)) {
            LOGGER.info("Persistence file not found. Starting with an empty cache.");
            return new HashMap<>();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(persistencePath))) {
            Object data = inputStream.readObject();
            if (!(data instanceof Map<?, ?> rawMap)) {
                LOGGER.warning("Persistence file format is invalid. Starting with an empty cache.");
                return new HashMap<>();
            }
            Map<String, CacheEntry> loaded = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                if (entry.getKey() instanceof String key && entry.getValue() instanceof CacheEntry cacheEntry) {
                    if (!cacheEntry.isExpired()) {
                        loaded.put(key, cacheEntry);
                    }
                }
            }
            return loaded;
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.WARNING, "Failed to load persistence file. Starting with an empty cache.", ex);
            return new HashMap<>();
        }
    }

    public Path getPersistencePath() {
        return persistencePath;
    }

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
