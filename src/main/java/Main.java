import cache.CacheCleaner;
import cache.CacheManager;
import commands.CacheCommandExecutor;
import commands.CommandResult;
import java.util.Scanner;
import utils.AppConstants;
import utils.ValidationUtils;

public class Main {

    private static final String MENU_SEPARATOR = "==========================";

    private final CacheManager cacheManager;
    private final CacheCommandExecutor commandExecutor;
    private final CacheCleaner cacheCleaner;
    private final Scanner scanner;

    public Main() {
        this.cacheManager = new CacheManager();
        this.commandExecutor = new CacheCommandExecutor(cacheManager);
        this.cacheCleaner = new CacheCleaner(cacheManager);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main application = new Main();
        application.run();
    }

    public void run() {
        cacheManager.initialize();
        cacheCleaner.start();
        printWelcome();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();

            try {
                int choice = ValidationUtils.parseMenuChoice(input, 1, 13);
                running = handleMenuChoice(choice);
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            System.out.println();
        }

        shutdown();
    }

    private boolean handleMenuChoice(int choice) {
        return switch (choice) {
            case 1 -> {
                handleSet();
                yield true;
            }
            case 2 -> {
                printResult(commandExecutor.executeGet(readLine("Enter key: ")));
                yield true;
            }
            case 3 -> {
                printResult(commandExecutor.executeDelete(readLine("Enter key: ")));
                yield true;
            }
            case 4 -> {
                printResult(commandExecutor.executeExists(readLine("Enter key: ")));
                yield true;
            }
            case 5 -> {
                printResult(commandExecutor.executeKeys());
                yield true;
            }
            case 6 -> {
                printResult(commandExecutor.executeDisplay());
                yield true;
            }
            case 7 -> {
                printResult(commandExecutor.executeClear());
                yield true;
            }
            case 8 -> {
                printResult(commandExecutor.executeSize());
                yield true;
            }
            case 9 -> {
                printResult(commandExecutor.executeStats());
                yield true;
            }
            case 10 -> {
                printResult(commandExecutor.executeSave());
                yield true;
            }
            case 11 -> {
                printResult(commandExecutor.executeLoad());
                yield true;
            }
            case 12 -> {
                printResult(commandExecutor.executeHistory());
                yield true;
            }
            case 13 -> {
                System.out.println("Exiting Mini Redis. Goodbye!");
                yield false;
            }
            default -> true;
        };
    }

    private void handleSet() {
        String key = readLine("Enter key: ");
        String value = readLine("Enter value: ");
        String ttlInput = readLine("Enter TTL in seconds (leave blank for no expiration): ");

        CommandResult result;
        if (ttlInput == null || ttlInput.trim().isEmpty()) {
            result = commandExecutor.executeSet(key, value);
        } else {
            result = commandExecutor.executeSet(key, value, ttlInput);
        }
        printResult(result);
    }

    private void printWelcome() {
        System.out.println(MENU_SEPARATOR);
        System.out.println("MINI REDIS CACHE SYSTEM");
        System.out.println(MENU_SEPARATOR);
        System.out.println("Max cache size: " + AppConstants.MAX_CACHE_SIZE);
        System.out.println("Persistence file: " + AppConstants.DEFAULT_PERSISTENCE_FILE);
        System.out.println("Background cleanup interval: " + AppConstants.CLEANUP_INTERVAL_SECONDS + "s");
        System.out.println();
    }

    private void printMenu() {
        System.out.println(MENU_SEPARATOR);
        System.out.println("MINI REDIS CACHE SYSTEM");
        System.out.println("=======================");
        System.out.println();
        System.out.println("1. SET");
        System.out.println("2. GET");
        System.out.println("3. DELETE");
        System.out.println("4. EXISTS");
        System.out.println("5. KEYS");
        System.out.println("6. DISPLAY");
        System.out.println("7. CLEAR");
        System.out.println("8. SIZE");
        System.out.println("9. STATS");
        System.out.println("10. SAVE");
        System.out.println("11. LOAD");
        System.out.println("12. HISTORY");
        System.out.println("13. EXIT");
        System.out.println();
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private void printResult(CommandResult result) {
        if (result.isSuccess()) {
            System.out.println(result.getMessage());
        } else {
            System.out.println("Error: " + result.getMessage());
        }
    }

    private void shutdown() {
        try {
            cacheManager.save();
            System.out.println("Cache saved before exit.");
        } catch (Exception ex) {
            System.out.println("Warning: Could not save cache on exit: " + ex.getMessage());
        }
        cacheCleaner.close();
        scanner.close();
    }
}
