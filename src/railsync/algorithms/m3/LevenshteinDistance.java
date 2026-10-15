package railsync.algorithms.m3;

import railsync.model.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * MODULE 3: DYNAMIC PROGRAMMING — LEVENSHTEIN EDIT DISTANCE
 * ============================================================================
 * Problem Solved:
 * Passenger queries and station operator inputs frequently contain typos,
 * phonetic misspellings, or phonetic variations (e.g., "Mumba", "Nuw Delhi",
 * "Hovrah"). Levenshtein Edit Distance quantifies spelling similarity to
 * automatically suggest the closest valid station names.
 *
 * How it Works:
 * 1. Let DP[i][j] denote the minimum edit distance between prefix s1[0..i-1]
 *    and s2[0..j-1].
 * 2. Three atomic operations are permitted (each of unit cost 1):
 *    - Insertion: DP[i][j-1] + 1
 *    - Deletion:  DP[i-1][j] + 1
 *    - Substitution: DP[i-1][j-1] + (s1[i-1] == s2[j-1] ? 0 : 1)
 * 3. Base cases:
 *    - DP[i][0] = i (deleting i characters to match empty string)
 *    - DP[0][j] = j (inserting j characters from empty string)
 *
 * Time Complexity:
 * - O(n * m) time where n = |s1| and m = |s2|.
 *
 * Space Complexity:
 * - O(n * m) auxiliary space for the DP table (enables inspecting operations).
 * ============================================================================
 */
public class LevenshteinDistance {

    /**
     * Represents a station suggestion scored by edit distance.
     */
    public static class StationMatch implements Comparable<StationMatch> {
        private final Station station;
        private final int editDistance;

        public StationMatch(Station station, int editDistance) {
            this.station = station;
            this.editDistance = editDistance;
        }

        public Station getStation() {
            return station;
        }

        public int getEditDistance() {
            return editDistance;
        }

        @Override
        public int compareTo(StationMatch o) {
            if (this.editDistance != o.editDistance) {
                return Integer.compare(this.editDistance, o.editDistance);
            }
            return this.station.getStationName().compareToIgnoreCase(o.station.getStationName());
        }

        @Override
        public String toString() {
            return String.format("Distance: %2d | %-28s (ID: %s, City: %s)",
                    editDistance, station.getStationName(), station.getStationId(), station.getCity());
        }
    }

    /**
     * Computes the Levenshtein edit distance table and final distance between two strings.
     *
     * @param s1 source string
     * @param s2 target string
     * @return 2D integer array containing the full DP table
     */
    public static int[][] computeDPTable(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        // Base cases: transforming between empty prefix and target prefix
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        // Fill dynamic programming table
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No edit operation required
                } else {
                    int deleteCost = dp[i - 1][j] + 1;
                    int insertCost = dp[i][j - 1] + 1;
                    int substituteCost = dp[i - 1][j - 1] + 1;

                    dp[i][j] = Math.min(deleteCost, Math.min(insertCost, substituteCost));
                }
            }
        }
        return dp;
    }

    /**
     * Computes the minimal edit distance between s1 and s2 (case-sensitive).
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return s2.length();
        if (s2 == null) return s1.length();

        int[][] dp = computeDPTable(s1, s2);
        return dp[s1.length()][s2.length()];
    }

    /**
     * Computes the minimal edit distance normalized to lowercase and trimmed.
     */
    public static int computeDistanceIgnoreCase(String s1, String s2) {
        String n1 = (s1 == null) ? "" : s1.trim().toLowerCase();
        String n2 = (s2 == null) ? "" : s2.trim().toLowerCase();
        return computeDistance(n1, n2);
    }

    /**
     * Evaluates a user query against all stations in the dataset, returning the top closest matches.
     *
     * @param query          misspelled or partial station name
     * @param stations       list of master station records
     * @param maxSuggestions maximum number of suggestions to return (typically 5)
     * @return sorted list of closest StationMatch items
     */
    public static List<StationMatch> findClosestStations(String query, List<Station> stations, int maxSuggestions) {
        if (query == null || stations == null || stations.isEmpty()) {
            return Collections.emptyList();
        }

        List<StationMatch> allMatches = new ArrayList<>();
        for (Station stn : stations) {
            // Compare normalized station name
            int distName = computeDistanceIgnoreCase(query, stn.getStationName());
            // Also consider city name distance as a secondary match indicator
            int distCity = computeDistanceIgnoreCase(query, stn.getCity());
            int bestDist = Math.min(distName, distCity);

            allMatches.add(new StationMatch(stn, bestDist));
        }

        Collections.sort(allMatches);

        int limit = Math.min(Math.max(1, maxSuggestions), allMatches.size());
        return new ArrayList<>(allMatches.subList(0, limit));
    }

    /**
     * Visualizes the DP grid for two short strings for educational/viva demonstrations.
     */
    public static String formatDPTableVisualization(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        int[][] dp = computeDPTable(s1, s2);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("DP Table for s1=\"%s\" vs s2=\"%s\":\n\n", s1, s2));
        sb.append("       ε ");
        for (int j = 0; j < s2.length(); j++) {
            sb.append(String.format("  %c ", s2.charAt(j)));
        }
        sb.append("\n");

        for (int i = 0; i <= s1.length(); i++) {
            char label = (i == 0) ? 'ε' : s1.charAt(i - 1);
            sb.append(String.format(" %c |", label));
            for (int j = 0; j <= s2.length(); j++) {
                sb.append(String.format(" %2d ", dp[i][j]));
            }
            sb.append("\n");
        }
        sb.append(String.format("\nFinal Levenshtein Distance: %d\n", dp[s1.length()][s2.length()]));
        return sb.toString();
    }
}
