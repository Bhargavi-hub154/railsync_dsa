package railsync.service;

import railsync.algorithms.m1.KMP;
import railsync.algorithms.m2.SuffixArray;
import railsync.algorithms.m3.LevenshteinDistance;
import railsync.algorithms.m4.EdmondsKarp;
import railsync.algorithms.m5.VertexCoverApproximation;
import railsync.algorithms.m6.RandomizedQuickSort;
import railsync.data.RailwayDataLoader;
import railsync.model.Schedule;
import railsync.model.ServiceAlert;
import railsync.model.Station;
import railsync.model.Train;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * RAILSYNC CORE SERVICE LAYER
 * ============================================================================
 * Coordinates the loaded railway dataset with the six specialized DSA algorithm
 * modules to deliver cohesive operational features.
 * ============================================================================
 */
public class RailwayService {

    private final RailwayDataLoader.RailwayDataset dataset;
    private int[] cachedDocSuffixArray; // Lazily computed index for railway_documents.txt
    private String lowercaseDocument;

    public RailwayService(RailwayDataLoader.RailwayDataset dataset) {
        this.dataset = dataset;
    }

    public RailwayDataLoader.RailwayDataset getDataset() {
        return dataset;
    }

    // =========================================================================
    // FEATURE 1: VIEW RAILWAY DATA
    // =========================================================================

    public void displayTrains() {
        List<Train> trains = dataset.getTrains();
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-6s | %-24s | %-48s | %-5s | %-5s | %-9s | %-5s | %s\n",
                "ID", "TRAIN NAME", "ROUTE (SOURCE -> DESTINATION)", "DEP", "ARR", "DELAY", "PAX", "STATUS");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (Train t : trains) {
            String route = t.getSourceStation() + " -> " + t.getDestinationStation();
            if (route.length() > 48) route = route.substring(0, 45) + "...";
            System.out.printf("%-6d | %-24s | %-48s | %-5s | %-5s | %3d min   | %4d  | %s\n",
                    t.getTrainId(), t.getTrainName(), route,
                    t.getDepartureTime(), t.getArrivalTime(), t.getDelayMinutes(),
                    t.getPassengerCount(), t.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("Total Active Train Records: %d\n", trains.size());
    }

    public void displayStations() {
        List<Station> stations = dataset.getStations();
        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-32s | %-16s | %-9s | %s\n",
                "STN ID", "STATION NAME", "CITY", "PLATFORMS", "STATION TYPE");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Station s : stations) {
            System.out.printf("%-8s | %-32s | %-16s | %5d     | %s\n",
                    s.getStationId(), s.getStationName(), s.getCity(), s.getPlatformCount(), s.getStationType());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("Total Network Stations: %d\n", stations.size());
    }

    public void displaySchedules() {
        List<Schedule> schedules = dataset.getSchedules();
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-8s | %-10s | %-10s | %-10s | %-8s\n",
                "SCH ID", "TRAIN ID", "STATION ID", "ARR TIME", "DEP TIME", "PLATFORM");
        System.out.println("-------------------------------------------------------------------------------");
        for (Schedule s : schedules) {
            System.out.printf("%-8s | %-8d | %-10s | %-10s | %-10s | Platform %d\n",
                    s.getScheduleId(), s.getTrainId(), s.getStationId(),
                    s.getArrivalTime(), s.getDepartureTime(), s.getPlatformNumber());
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("Total Timetable Stop Records: %d\n", schedules.size());
    }

    public void displayAlerts() {
        List<ServiceAlert> alerts = dataset.getAlerts();
        System.out.println("\n----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-10s | %-8s | %-24s | %-18s | %s\n",
                "ALERT ID", "SEVERITY", "TRAIN", "STATION", "TYPE", "DESCRIPTION");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        for (ServiceAlert a : alerts) {
            System.out.printf("%-8s | %-10s | %-8d | %-24s | %-18s | %s\n",
                    a.getAlertId(), a.getSeverity(), a.getTrainId(),
                    a.getStationName(), a.getAlertType(), a.getDescription());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("Total Operational Alerts: %d\n", alerts.size());
    }

    // =========================================================================
    // FEATURE 2: SEARCH RAILWAY RECORDS USING KMP
    // =========================================================================

    public void searchRecordsWithKMP(String query) {
        if (query == null || query.trim().isEmpty()) {
            System.out.println("[!] Search query cannot be empty.");
            return;
        }
        String pattern = query.trim();
        System.out.println("\n=================================================================================");
        System.out.printf("  KMP SEARCH RESULTS FOR PATTERN: \"%s\" (Case-Insensitive)\n", pattern);
        System.out.println("=================================================================================");

        int totalMatches = 0;

        // 1. Search Trains
        List<Train> matchingTrains = new ArrayList<>();
        for (Train t : dataset.getTrains()) {
            String combined = t.getTrainName() + " " + t.getSourceStation() + " " +
                              t.getDestinationStation() + " " + t.getStatus() + " " + t.getTrainId();
            if (KMP.containsIgnoreCase(combined, pattern)) {
                matchingTrains.add(t);
            }
        }
        totalMatches += matchingTrains.size();

        System.out.printf("\n[1] Matching Train Records (%d found):\n", matchingTrains.size());
        if (matchingTrains.isEmpty()) {
            System.out.println("    (No trains matched the query)");
        } else {
            for (Train t : matchingTrains) {
                System.out.println("    - " + t);
            }
        }

        // 2. Search Stations
        List<Station> matchingStations = new ArrayList<>();
        for (Station s : dataset.getStations()) {
            String combined = s.getStationName() + " " + s.getCity() + " " + s.getStationType();
            if (KMP.containsIgnoreCase(combined, pattern)) {
                matchingStations.add(s);
            }
        }
        totalMatches += matchingStations.size();

        System.out.printf("\n[2] Matching Station Records (%d found):\n", matchingStations.size());
        if (matchingStations.isEmpty()) {
            System.out.println("    (No stations matched the query)");
        } else {
            for (Station s : matchingStations) {
                System.out.println("    - " + s);
            }
        }

        // 3. Search Alerts
        List<ServiceAlert> matchingAlerts = new ArrayList<>();
        for (ServiceAlert a : dataset.getAlerts()) {
            String combined = a.getStationName() + " " + a.getAlertType() + " " +
                              a.getDescription() + " " + a.getSeverity();
            if (KMP.containsIgnoreCase(combined, pattern)) {
                matchingAlerts.add(a);
            }
        }
        totalMatches += matchingAlerts.size();

        System.out.printf("\n[3] Matching Service Alerts (%d found):\n", matchingAlerts.size());
        if (matchingAlerts.isEmpty()) {
            System.out.println("    (No alerts matched the query)");
        } else {
            for (ServiceAlert a : matchingAlerts) {
                System.out.println("    - " + a);
            }
        }

        System.out.println("\n---------------------------------------------------------------------------------");
        System.out.printf("KMP Search Summary: Found %d total record matches across all files for \"%s\".\n",
                totalMatches, pattern);
        System.out.println("Algorithm: O(n + m) time using computed Longest Prefix Suffix (LPS) array.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    // =========================================================================
    // FEATURE 3: SEARCH RAILWAY DOCUMENTS USING SUFFIX ARRAY
    // =========================================================================

    public void searchRailwayDocumentsWithSuffixArray(String phrase) {
        if (phrase == null || phrase.trim().isEmpty()) {
            System.out.println("[!] Search phrase cannot be empty.");
            return;
        }
        String pattern = phrase.trim();
        String doc = dataset.getRailwayDocument();

        // Lazy initialization of Suffix Array index
        if (cachedDocSuffixArray == null) {
            System.out.println("[*] Building Suffix Array index on railway_documents.txt (Prefix-Doubling O(n log^2 n))...");
            lowercaseDocument = doc.toLowerCase();
            cachedDocSuffixArray = SuffixArray.buildSuffixArray(lowercaseDocument);
            System.out.printf("[*] Suffix Array successfully built for document of %,d characters.\n\n", doc.length());
        }

        long startSearch = System.nanoTime();
        List<Integer> matches = SuffixArray.searchIgnoreCase(lowercaseDocument, cachedDocSuffixArray, pattern);
        long elapsedSearch = System.nanoTime() - startSearch;

        System.out.println("=================================================================================");
        System.out.printf("  SUFFIX ARRAY DOCUMENT SEARCH RESULTS FOR: \"%s\"\n", pattern);
        System.out.println("=================================================================================");
        System.out.printf("Query Time: %,.3f ms (%,d ns) | Occurrences Found: %d\n\n",
                elapsedSearch / 1_000_000.0, elapsedSearch, matches.size());

        if (matches.isEmpty()) {
            System.out.println("No occurrences found in railway_documents.txt.");
            System.out.println("Try searching for terms such as: 'priority clearance', 'maintenance', 'fog', or 'platform'.");
            return;
        }

        int count = 0;
        for (int charIdx : matches) {
            count++;
            SuffixArray.MatchLocation loc = SuffixArray.computeLocation(doc, charIdx);

            // Extract context snippet: 40 chars before and 50 chars after
            int startSnippet = Math.max(0, charIdx - 35);
            int endSnippet = Math.min(doc.length(), charIdx + pattern.length() + 45);
            String snippet = doc.substring(startSnippet, endSnippet).replace("\n", " ");

            System.out.printf("Match #%d:\n", count);
            System.out.printf("  - Location Details : %s\n", loc);
            System.out.printf("  - Snippet Context  : \"...%s...\"\n\n", snippet);
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Distinction Note: Character offsets mark exact 0-indexed byte positions,");
        System.out.println("while Word numbers indicate natural spoken word order within document sections.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    // =========================================================================
    // FEATURE 4: FUZZY STATION NAME SUGGESTION (LEVENSHTEIN EDIT DISTANCE)
    // =========================================================================

    public void suggestStationNames(String inputQuery) {
        if (inputQuery == null || inputQuery.trim().isEmpty()) {
            System.out.println("[!] Query string cannot be empty.");
            return;
        }
        String query = inputQuery.trim();
        List<Station> stations = dataset.getStations();

        List<LevenshteinDistance.StationMatch> suggestions =
                LevenshteinDistance.findClosestStations(query, stations, 5);

        System.out.println("\n=================================================================================");
        System.out.printf("  LEVENSHTEIN EDIT DISTANCE: STATION SUGGESTIONS FOR \"%s\"\n", query);
        System.out.println("=================================================================================");

        if (suggestions.isEmpty()) {
            System.out.println("No suggestions could be generated.");
            return;
        }

        int minDistance = suggestions.get(0).getEditDistance();
        if (minDistance == 0) {
            System.out.println("[*] Exact match found in dataset!");
        } else {
            System.out.printf("[*] Input likely contains typos. Minimum edit distance found: %d\n", minDistance);
        }

        System.out.println("\nTop Closest Station Recommendations:");
        for (int i = 0; i < suggestions.size(); i++) {
            LevenshteinDistance.StationMatch match = suggestions.get(i);
            System.out.printf("  %d. %s\n", (i + 1), match);
        }

        // Show DP Table visualization for top match
        Station topStation = suggestions.get(0).getStation();
        System.out.println("\n--- Step-by-Step DP Matrix Visualization (Top Match) ---");
        System.out.println(LevenshteinDistance.formatDPTableVisualization(query.toLowerCase(), topStation.getCity().toLowerCase()));

        System.out.println("Algorithm: Dynamic Programming O(n * m) time and space.");
        System.out.println("Cost Model: Insertion = 1, Deletion = 1, Substitution = 1.");
    }

    // =========================================================================
    // FEATURE 5: RAILWAY NETWORK FLOW ANALYSIS (EDMONDS-KARP)
    // =========================================================================

    public void analyzeNetworkFlow(int sourceIdx, int sinkIdx) {
        EdmondsKarp.RailwayNetworkSample net = EdmondsKarp.getSampleRailwayNetwork();

        if (sourceIdx < 0 || sourceIdx >= net.nodeNames.length ||
            sinkIdx < 0 || sinkIdx >= net.nodeNames.length) {
            System.out.printf("[!] Invalid station selection. Indices must be between 0 and %d.\n",
                    net.nodeNames.length - 1);
            return;
        }

        if (sourceIdx == sinkIdx) {
            System.out.println("[!] Source and destination terminus cannot be the same junction.");
            return;
        }

        System.out.println("\n=================================================================================");
        System.out.println("      EDMONDS-KARP MAXIMUM FLOW ANALYSIS: CORRIDOR BOTTLENECK SIMULATION        ");
        System.out.println("=================================================================================");
        System.out.println("Disclaimer: Edge capacities denote hypothetical train throughput slots");
        System.out.println("            (trains per 6-hour dispatch block) based on physical track lines.\n");

        EdmondsKarp.FlowResult result = EdmondsKarp.computeMaxFlow(net.capacity, sourceIdx, sinkIdx, net.nodeNames);

        System.out.print(result.getSummary());

        System.out.println("\nCorridor Track Utilization Breakdown (Utilized Flow / Maximum Capacity):");
        System.out.println("-----------------------------------------------------------------------");
        for (int u = 0; u < net.nodeNames.length; u++) {
            for (int v = 0; v < net.nodeNames.length; v++) {
                if (net.capacity[u][v] > 0) {
                    System.out.printf("  %-28s -> %-28s : %2d / %2d slots\n",
                            net.nodeNames[u], net.nodeNames[v],
                            result.getFinalFlow() != null ? result.getFinalFlow()[u][v] : 0,
                            net.capacity[u][v]);
                }
            }
        }
        System.out.println("\nTime Complexity: O(V * E^2) using Breadth-First Search for shortest augmenting paths.");
    }

    // =========================================================================
    // FEATURE 6: CONFLICT RESOLUTION (VERTEX COVER 2-APPROXIMATION)
    // =========================================================================

    public void analyzeRailwayConflicts() {
        VertexCoverApproximation.ConflictGraph graph =
                VertexCoverApproximation.getSampleConflictGraph();

        VertexCoverApproximation.ApproximationResult result =
                VertexCoverApproximation.computeVertexCover(graph);

        System.out.println("\n--- Railway Conflict Analysis ---");
        System.out.println("Total conflicts: " + graph.edges.size());

        System.out.println("\nSelected Control Points:");
        for (int v : result.coverVertices) {
            System.out.println("- " + graph.vertexNames[v]);
        }

        if (result.isValidCover) {
            System.out.println("\nResult: All conflicts are covered.");
        } else {
            System.out.println("\nResult: Some conflicts remain uncovered.");
        }

        System.out.println("\nAlgorithm: Vertex Cover 2-Approximation");
        System.out.println("Selected points: " + result.coverVertices.size());
        System.out.println("Matching pairs: " + result.chosenMatchingEdges.size());
        System.out.println("Guarantee: Cover size is at most twice the optimal size.");
    }

    // =========================================================================
    // FEATURE 7: SORT TRAIN RECORDS (RANDOMIZED QUICKSORT)
    // =========================================================================

    public void sortTrainRecords(int criteriaChoice) {
        List<Train> trains = dataset.getTrains();
        if (trains.isEmpty()) {
            System.out.println("[!] No train records available to sort.");
            return;
        }

        Comparator<Train> comp;
        String criteriaLabel;

        switch (criteriaChoice) {
            case 1:
                comp = RandomizedQuickSort.BY_DELAY_DESC;
                criteriaLabel = "Delay Minutes (Highest Delay First)";
                break;
            case 2:
                comp = RandomizedQuickSort.BY_DELAY_ASC;
                criteriaLabel = "Delay Minutes (On-Time / Lowest Delay First)";
                break;
            case 3:
                comp = RandomizedQuickSort.BY_PASSENGERS_DESC;
                criteriaLabel = "Passenger Volume (Most Crowded First)";
                break;
            case 4:
                comp = RandomizedQuickSort.BY_TRAIN_ID_ASC;
                criteriaLabel = "Train Number / ID (Ascending Order)";
                break;
            default:
                System.out.println("[!] Invalid sort option.");
                return;
        }

        System.out.println("\n=================================================================================");
        System.out.printf("  RANDOMIZED QUICKSORT: SORTING TRAINS BY %s\n", criteriaLabel.toUpperCase());
        System.out.println("=================================================================================");

        long startTime = System.nanoTime();
        List<Train> sortedTrains = RandomizedQuickSort.sortedCopy(trains, comp);
        long elapsed = System.nanoTime() - startTime;

        System.out.println("\n--- Sorted Train Fleet Records ---");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-6s | %-24s | %-48s | %-5s | %-5s | %-9s | %-5s | %s\n",
                "ID", "TRAIN NAME", "ROUTE (SOURCE -> DESTINATION)", "DEP", "ARR", "DELAY", "PAX", "STATUS");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (Train t : sortedTrains) {
            String route = t.getSourceStation() + " -> " + t.getDestinationStation();
            if (route.length() > 48) route = route.substring(0, 45) + "...";
            System.out.printf("%-6d | %-24s | %-48s | %-5s | %-5s | %3d min   | %4d  | %s\n",
                    t.getTrainId(), t.getTrainName(), route,
                    t.getDepartureTime(), t.getArrivalTime(), t.getDelayMinutes(),
                    t.getPassengerCount(), t.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");

        System.out.printf("\nSorting Performance: %,d train records sorted in %,.3f ms (%,d ns)\n",
                sortedTrains.size(), elapsed / 1_000_000.0, elapsed);
        System.out.println("Theoretical Guarantee: Expected O(n log n) time due to uniform random pivot selection.");
        System.out.println("Random Pivot Role: Eliminates deterministic O(n^2) vulnerability on sorted inputs.");
    }
}
