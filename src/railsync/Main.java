package railsync;

import railsync.algorithms.m2.SuffixArray;
import railsync.algorithms.m4.EdmondsKarp;
import railsync.benchmark.BenchmarkRunner;
import railsync.data.RailwayDataLoader;
import railsync.service.RailwayService;
import railsync.test.CorrectnessTests;

import java.io.IOException;
import java.util.Scanner;

/**
 * ============================================================================
 * RAILSYNC — INTELLIGENT RAILWAY OPERATIONS SYSTEM
 * ============================================================================
 * Entry point for the Core Java console application.
 *
 * Designed for Eclipse IDE: Right-click -> Run As -> Java Application.
 *
 * Implements 6 Core DSA Modules:
 * 1. String Matching           : Knuth-Morris-Pratt (KMP)
 * 2. Suffix Structures         : Suffix Array (Prefix-Doubling & Binary Search)
 * 3. Dynamic Programming       : Levenshtein Edit Distance (Fuzzy Station Match)
 * 4. Network Flow              : Edmonds-Karp Max Flow (BFS Augmenting Paths)
 * 5. NP-Hard Approximation     : Vertex Cover 2-Approximation (Conflict Graph)
 * 6. Randomized Algorithms     : Randomized QuickSort (Dynamic Train Sorting)
 * ============================================================================
 */
public class Main {

    private static RailwayService service;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        initializeData(null);

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = promptUser("Enter your choice: ");

            switch (choice.trim()) {
                case "1":
                    handleViewData();
                    break;
                case "2":
                    handleKmpSearch();
                    break;
                case "3":
                    handleSuffixArraySearch();
                    break;
                case "4":
                    handleEditDistance();
                    break;
                case "5":
                    handleNetworkFlow();
                    break;
                case "6":
                    handleConflictVertexCover();
                    break;
                case "7":
                    handleQuickSort();
                    break;
                case "8":
                    handleCorrectnessTests();
                    break;
                
                case "0":
                    System.out.println("\nThank you for using RailSync — Intelligent Railway Operations System.");
                    System.out.println("System session terminated successfully.");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid selection. Please enter a number between 0 and 8.");
            }

            if (running) {
                pause();
            }
        }
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("                                  RAILSYNC                                      ");
        System.out.println("                     Intelligent Railway Operations System                      ");
        System.out.println("                  Data Structures & Algorithms Academic Suite                   ");
        System.out.println("================================================================================");
    }

    private static void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("               MAIN MENU                ");
        System.out.println("========================================");
        System.out.println("1. View Railway Data");
        System.out.println("2. Search Railway Records (KMP)");
        System.out.println("3. Search Railway Documents (Suffix Array)");
        System.out.println("4. Find Similar Station Names (Edit Distance)");
        System.out.println("5. Analyze Railway Network Flow (Edmonds-Karp)");
        System.out.println("6. Analyze Railway Conflicts (Vertex Cover Approximation)");
        System.out.println("7. Sort Train Records (Randomized QuickSort)");
        System.out.println("8. Run Algorithm Correctness Tests");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private static void initializeData(String initialCustomPath) {
        String pathCandidate = initialCustomPath;
        while (service == null) {
            try {
                RailwayDataLoader.RailwayDataset dataset = RailwayDataLoader.loadAll(pathCandidate);
                service = new RailwayService(dataset);
                System.out.printf("[✓] Railway dataset loaded successfully from: %s\n",
                        dataset.getDataDirectory().getAbsolutePath());
                System.out.printf("    Loaded: %d Trains | %d Stations | %d Schedules | %d Alerts\n",
                        dataset.getTrains().size(), dataset.getStations().size(),
                        dataset.getSchedules().size(), dataset.getAlerts().size());
            } catch (IOException e) {
                System.err.println("\n[!] Error loading dataset: " + e.getMessage());
                System.out.println("Please specify the path to the 'data' directory (or press Enter to retry default):");
                System.out.print("Custom data directory path: ");
                String input = scanner.nextLine().trim();
                pathCandidate = input.isEmpty() ? null : input;
            }
        }
    }

    // -------------------------------------------------------------------------
    // MENU HANDLERS
    // -------------------------------------------------------------------------

    private static void handleViewData() {
        System.out.println("\n--- View Railway Data ---");
        System.out.println("1. View Train Fleet Records");
        System.out.println("2. View Railway Stations Master");
        System.out.println("3. View Timetable Schedules");
        System.out.println("4. View Active Service Alerts");
        System.out.println("0. Return to Main Menu");

        String subChoice = promptUser("Select dataset category: ");
        switch (subChoice.trim()) {
            case "1":
                service.displayTrains();
                break;
            case "2":
                service.displayStations();
                break;
            case "3":
                service.displaySchedules();
                break;
            case "4":
                service.displayAlerts();
                break;
            case "0":
                return;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private static void handleKmpSearch() {
        System.out.println("\n--- Module 1: Search Railway Records (Knuth-Morris-Pratt) ---");
        System.out.println("Searches across train names, stations, cities, and alert descriptions.");
        System.out.println("Examples: 'Rajdhani', 'Howrah', 'fog', 'congestion', 'delayed', 'express'");

        String pattern = promptUser("Enter search keyword or phrase: ");
        service.searchRecordsWithKMP(pattern);
    }

    private static void handleSuffixArraySearch() {
        System.out.println("\n--- Module 2: Search Railway Documents (Suffix Array Index) ---");
        System.out.println("Indexes operations manual and dispatch logs using Prefix-Doubling O(n log^2 n).");
        System.out.println("Examples: 'priority clearance', 'fog protocol', 'track maintenance', 'congestion'");
        System.out.println("Tip: Type 'demo' to see the classic 'banana' prefix-doubling walkthrough.");

        String query = promptUser("Enter search phrase (or 'demo'): ");
        if ("demo".equalsIgnoreCase(query.trim())) {
            System.out.println("\n" + SuffixArray.getEducationalExample());
        } else {
            service.searchRailwayDocumentsWithSuffixArray(query);
        }
    }

    private static void handleEditDistance() {
        System.out.println("\n--- Module 3: Find Similar Station Names (Levenshtein Distance) ---");
        System.out.println("Uses Dynamic Programming to suggest correct stations for misspelled queries.");
        System.out.println("Examples of typos to test: 'mumba', 'nuw delhi', 'hovrah', 'chenai', 'jaypur', 'punee'");

        String query = promptUser("Enter station name (with possible typo): ");
        service.suggestStationNames(query);
    }

    private static void handleNetworkFlow() {
        System.out.println("\n--- Module 4: Analyze Railway Network Flow (Edmonds-Karp) ---");
        System.out.println("Evaluates maximum train throughput bottleneck across northern corridor junctions.");
        System.out.println("\nAvailable Network Junctions:");

        EdmondsKarp.RailwayNetworkSample net = EdmondsKarp.getSampleRailwayNetwork();
        for (int i = 0; i < net.nodeNames.length; i++) {
            System.out.printf("  [%d] %s\n", i, net.nodeNames[i]);
        }

        System.out.println("\nPress Enter to use default Origin (0: New Delhi) and Terminus (6: Howrah),");
        System.out.println("or enter custom junction indices.");

        int src = 0;
        int sink = 6;

        String srcInput = promptUser("Enter Source Junction Index [Default 0]: ");
        if (!srcInput.trim().isEmpty()) {
            try {
                src = Integer.parseInt(srcInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number. Falling back to default (0).");
            }
        }

        String sinkInput = promptUser("Enter Destination Junction Index [Default 6]: ");
        if (!sinkInput.trim().isEmpty()) {
            try {
                sink = Integer.parseInt(sinkInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number. Falling back to default (6).");
            }
        }

        service.analyzeNetworkFlow(src, sink);
    }

    private static void handleConflictVertexCover() {
        System.out.println("\n--- Module 5: Analyze Railway Conflicts (Vertex Cover 2-Approximation) ---");
        System.out.println("Solves track interlocking collision conflicts using polynomial-time 2-approximation.");
        service.analyzeRailwayConflicts();
    }

    private static void handleQuickSort() {
        System.out.println("\n--- Module 6: Sort Train Records (Randomized QuickSort) ---");
        System.out.println("Select Sorting Criteria:");
        System.out.println("1. Delay Minutes (Highest Delay First — Urgent Dispatch)");
        System.out.println("2. Delay Minutes (On-Time / Lowest Delay First)");
        System.out.println("3. Passenger Volume (Most Crowded Services First)");
        System.out.println("4. Train ID / Number (Ascending Timetable Order)");
        System.out.println("0. Return to Main Menu");

        String choice = promptUser("Enter sort option (1-4): ");
        if ("0".equals(choice.trim())) return;

        try {
            int option = Integer.parseInt(choice.trim());
            service.sortTrainRecords(option);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid selection. Please enter a valid number.");
        }
    }

    private static void handleCorrectnessTests() {
        System.out.println("\nLaunching Algorithm Correctness Test Suite...");
        CorrectnessTests.runAllTests();
    }

   

    private static String promptUser(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            return "0"; // Gracefully return exit command if input stream ends
        }
        return scanner.nextLine();
    }

    private static void pause() {
        System.out.print("\nPress [Enter] to return to the menu...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
