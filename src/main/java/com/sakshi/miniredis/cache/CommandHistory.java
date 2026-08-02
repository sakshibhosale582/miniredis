package com.sakshi.miniredis.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/*
 * Stores the history of recently executed cache commands.
 * It keeps only a limited number of commands based on maxSize.
 */
public class CommandHistory {

    // Maximum number of commands that can be stored in history.
    private final int maxSize;

    /*
     * Thread-safe deque used to store commands in execution order.
     * Useful when multiple requests access command history simultaneously.
     */
    private final ConcurrentLinkedDeque<String> history = new ConcurrentLinkedDeque<>();

    // Creates command history with a fixed maximum size.
    public CommandHistory(int maxSize) {

        // History size must always be greater than zero.
        if (maxSize <= 0) {
            throw new IllegalArgumentException("History size must be positive.");
        }

        this.maxSize = maxSize;
    }

    // Records a new command in the command history.
    public void record(String command) {

        // Ignores null or empty commands.
        if (command == null || command.isBlank()) {
            return;
        }

        // Adds the latest command at the end of history.
        history.addLast(command.trim());

        /*
         * If history exceeds maxSize, remove the oldest command
         * to maintain the fixed history limit.
         */
        while (history.size() > maxSize) {
            history.pollFirst();
        }
    }

    /*
     * Returns the command history as a read-only list
     * so external code cannot directly modify it.
     */
    public List<String> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    // Removes all stored commands from history.
    public void clear() {
        history.clear();
    }
}