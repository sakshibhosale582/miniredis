package utils;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    public static long parseTtl(String ttlValue) {
        requireNonEmpty(ttlValue, "TTL");
        try {
            long ttl = Long.parseLong(ttlValue.trim());
            if (ttl <= 0) {
                throw new IllegalArgumentException("TTL must be a positive number of seconds.");
            }
            return ttl;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid TTL value: " + ttlValue);
        }
    }

    public static int parseMenuChoice(String input, int min, int max) {
        requireNonEmpty(input, "Menu choice");
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice < min || choice > max) {
                throw new IllegalArgumentException(
                        "Invalid choice. Please enter a number between " + min + " and " + max + ".");
            }
            return choice;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid menu choice: " + input);
        }
    }
}
