package cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CommandHistory {

    private final int maxSize;
    private final ConcurrentLinkedDeque<String> history = new ConcurrentLinkedDeque<>();

    public CommandHistory(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("History size must be positive.");
        }
        this.maxSize = maxSize;
    }

    public void record(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        history.addLast(command.trim());
        while (history.size() > maxSize) {
            history.pollFirst();
        }
    }

    public List<String> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public void clear() {
        history.clear();
    }
}
