package railsync.benchmark;

import railsync.algorithms.m1.KMP;
import railsync.algorithms.m6.RandomizedQuickSort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * ============================================================================
 * RAILSYNC PERFORMANCE BENCHMARK RUNNER
 * ============================================================================
 * Evaluates empirical execution times using System.nanoTime():
 * 1. Knuth-Morris-Pratt (KMP) vs Naive Substring Search:
 *    - Validates match equivalence.
 *    - Compares execution across pathological/repetitive and natural text.
 * 2. Randomized QuickSort vs Java Standard Library (Arrays.sort):
 *    - Validates sort equivalence.
 *    - Evaluates random, already-sorted, and reverse-sorted distributions.
 *
 * Saves a formatted report to 'results/benchmark_report.txt'.
 * ============================================================================
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        runBenchmarksAndSaveReport();
    }

    /**
     * Executes benchmarks, prints live results to console, and writes results/benchmark_report.txt.
     */
    public static String runBenchmarksAndSaveReport() {
        StringBuilder report = new StringBuilder();
        report.append("================================================================================\n");
        report.append("                   RAILSYNC ALGORITHM PERFORMANCE BENCHMARK REPORT             \n");
        report.append("================================================================================\n");
        report.append("Generated At: ").append(new java.util.Date()).append("\n");
        report.append("Java Runtime: ").append(System.getProperty("java.version"))
              .append(" (").append(System.getProperty("java.vendor")).append(")\n");
        report.append("Operating System: ").append(System.getProperty("os.name"))
              .append(" (").append(System.getProperty("os.arch")).append(")\n");
        report.append("================================================================================\n\n");

        System.out.println("Starting RailSync Performance Benchmarks (warming up JVM)...");

        // ---------------------------------------------------------------------
        // BENCHMARK 1: KMP VS NAIVE SUBSTRING SEARCH
        // ---------------------------------------------------------------------
        report.append("--------------------------------------------------------------------------------\n");
        report.append("EXPERIMENT 1: STRING SEARCHING — KMP VS NAIVE BACKTRACKING SEARCH\n");
        report.append("--------------------------------------------------------------------------------\n");

        // Test 1A: Repetitive/Pathological Text (Classic Worst-Case for Naive Search)
        int repLength = 60000;
        StringBuilder sbRep = new StringBuilder(repLength);
        for (int i = 0; i < repLength; i++) {
            sbRep.append('A');
        }
        // Place rare matches
        sbRep.setCharAt(20000, 'B');
        sbRep.setCharAt(45000, 'B');
        String textPathological = sbRep.toString();
        String patternPathological = "AAAAAB"; // High prefix overlap

        // Warmup
        for (int i = 0; i < 5; i++) {
            KMP.search(textPathological.substring(0, 1000), patternPathological);
            naiveSearch(textPathological.substring(0, 1000), patternPathological);
        }

        // Measure KMP
        long startKMP1 = System.nanoTime();
        List<Integer> kmpMatches1 = KMP.search(textPathological, patternPathological);
        long endKMP1 = System.nanoTime();
        long durKMP1 = endKMP1 - startKMP1;

        // Measure Naive
        long startNaive1 = System.nanoTime();
        List<Integer> naiveMatches1 = naiveSearch(textPathological, patternPathological);
        long endNaive1 = System.nanoTime();
        long durNaive1 = endNaive1 - startNaive1;

        boolean matchesEqual1 = kmpMatches1.equals(naiveMatches1);

        report.append(String.format("Dataset 1A: Repetitive Text (Length: %,d chars, Pattern: '%s')\n",
                textPathological.length(), patternPathological));
        report.append(String.format("  - KMP Matches Found    : %d (Time: %,.3f ms | %,d ns)\n",
                kmpMatches1.size(), durKMP1 / 1_000_000.0, durKMP1));
        report.append(String.format("  - Naive Matches Found  : %d (Time: %,.3f ms | %,d ns)\n",
                naiveMatches1.size(), durNaive1 / 1_000_000.0, durNaive1));
        report.append(String.format("  - Match Verification   : %s (Both algorithms returned identical indices)\n",
                matchesEqual1 ? "VERIFIED MATCH" : "DISCREPANCY DETECTED"));
        report.append(String.format("  - Observed Speedup     : %,.2fx faster with KMP on repetitive text\n\n",
                (double) durNaive1 / Math.max(1, durKMP1)));

        // Test 1B: Natural Railway Document Simulation
        int railLogLength = 80000;
        String sampleChunk = "DISPATCH: Train 12001 departed platform 1 on schedule. Track block authorized. ";
        StringBuilder sbRail = new StringBuilder(railLogLength);
        while (sbRail.length() < railLogLength) {
            sbRail.append(sampleChunk);
        }
        sbRail.append("CRITICAL_SIGNAL_OVERRIDE_AT_JUNCTION");
        String textNatural = sbRail.toString();
        String patternNatural = "Track block authorized.";

        long startKMP2 = System.nanoTime();
        List<Integer> kmpMatches2 = KMP.search(textNatural, patternNatural);
        long durKMP2 = System.nanoTime() - startKMP2;

        long startNaive2 = System.nanoTime();
        List<Integer> naiveMatches2 = naiveSearch(textNatural, patternNatural);
        long durNaive2 = System.nanoTime() - startNaive2;

        report.append(String.format("Dataset 1B: Natural Railway Text (Length: %,d chars, Pattern: '%s')\n",
                textNatural.length(), patternNatural));
        report.append(String.format("  - KMP Execution Time   : %,.3f ms (%,d ns)\n",
                durKMP2 / 1_000_000.0, durKMP2));
        report.append(String.format("  - Naive Execution Time : %,.3f ms (%,d ns)\n",
                durNaive2 / 1_000_000.0, durNaive2));
        report.append(String.format("  - Verification         : %s (Matches: %d)\n\n",
                kmpMatches2.equals(naiveMatches2) ? "VERIFIED MATCH" : "FAILED", kmpMatches2.size()));

        // ---------------------------------------------------------------------
        // BENCHMARK 2: RANDOMIZED QUICKSORT VS JAVA STANDARD LIBRARY
        // ---------------------------------------------------------------------
        report.append("--------------------------------------------------------------------------------\n");
        report.append("EXPERIMENT 2: SORTING — RANDOMIZED QUICKSORT VS DUAL-PIVOT ARRAYS.SORT\n");
        report.append("--------------------------------------------------------------------------------\n");

        int[] testSizes = {10000, 50000};
        Random rng = new Random(42); // Deterministic seed for reproducibility

        for (int size : testSizes) {
            report.append(String.format(">> Input Size N = %,d elements:\n", size));

            // Distribution 1: Random Integers
            int[] originalRandom = new int[size];
            for (int i = 0; i < size; i++) originalRandom[i] = rng.nextInt(1_000_000);

            benchmarkSortComparison(originalRandom, "Uniform Random Integers", report);

            // Distribution 2: Already Sorted Integers
            int[] originalSorted = originalRandom.clone();
            Arrays.sort(originalSorted);
            benchmarkSortComparison(originalSorted, "Already Sorted Integers (Ascending)", report);

            // Distribution 3: Reverse Sorted Integers (Adversarial test)
            int[] originalReverse = new int[size];
            for (int i = 0; i < size; i++) {
                originalReverse[i] = originalSorted[size - 1 - i];
            }
            benchmarkSortComparison(originalReverse, "Reverse Sorted Integers (Descending)", report);
            report.append("\n");
        }

        // ---------------------------------------------------------------------
        // ACADEMIC OBSERVATIONS & METHODOLOGY NOTE
        // ---------------------------------------------------------------------
        report.append("--------------------------------------------------------------------------------\n");
        report.append("ACADEMIC ANALYSIS & VIVA NOTES\n");
        report.append("--------------------------------------------------------------------------------\n");
        report.append("1. String Search Insights:\n");
        report.append("   - Naive search requires backtracking: when a character mismatch occurs after\n");
        report.append("     several matches, the text pointer rewinds, causing O(n * m) worst-case time.\n");
        report.append("   - KMP utilizes the Longest Prefix Suffix (LPS) array to advance without any\n");
        report.append("     backtracking in the text pointer, strictly achieving O(n + m) time complexity.\n");
        report.append("   - In pathological cases with heavy prefix repetition, KMP provides orders of\n");
        report.append("     magnitude faster execution compared to Naive search.\n\n");
        report.append("2. Sorting Insights:\n");
        report.append("   - Standard deterministic QuickSort (pivot at first/last index) degrades to O(n^2)\n");
        report.append("     on already-sorted or reverse-sorted datasets, causing call-stack overflow.\n");
        report.append("   - Randomized QuickSort selects a pivot uniformly at random, ensuring balanced\n");
        report.append("     partitions and preserving expected O(n log n) performance across all inputs.\n");
        report.append("   - Java's built-in Arrays.sort uses Dual-Pivot QuickSort with JVM assembly-level\n");
        report.append("     intrinsics and JIT optimizations, serving as an optimal industry baseline.\n\n");
        report.append("3. Benchmarking Considerations:\n");
        report.append("   - Measurements use System.nanoTime() for high-resolution elapsed duration.\n");
        report.append("   - Exact timings vary depending on CPU frequency scaling, JVM JIT warmup,\n");
        report.append("     operating system scheduler interrupts, and garbage collection passes.\n");
        report.append("================================================================================\n");

        String reportText = report.toString();
        System.out.println(reportText);

        // Save report to results/benchmark_report.txt
        saveReportToFile(reportText);

        return reportText;
    }

    private static void benchmarkSortComparison(int[] original, String distributionLabel, StringBuilder report) {
        int[] arrQS = original.clone();
        int[] arrStd = original.clone();

        long startQS = System.nanoTime();
        RandomizedQuickSort.sort(arrQS);
        long durQS = System.nanoTime() - startQS;

        long startStd = System.nanoTime();
        Arrays.sort(arrStd);
        long durStd = System.nanoTime() - startStd;

        boolean equal = Arrays.equals(arrQS, arrStd);

        report.append(String.format("  * %-36s | Randomized QS: %,7.3f ms | Arrays.sort: %,7.3f ms | Match: %s\n",
                distributionLabel, durQS / 1_000_000.0, durStd / 1_000_000.0,
                equal ? "VERIFIED" : "FAIL"));
    }

    private static void saveReportToFile(String content) {
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }
        File reportFile = new File(resultsDir, "benchmark_report.txt");
        try (FileWriter fw = new FileWriter(reportFile, StandardCharsets.UTF_8)) {
            fw.write(content);
            System.out.println("Benchmark report successfully written to: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Could not write benchmark report file: " + e.getMessage());
        }
    }

    /**
     * Naive Substring Search implementation for baseline academic comparison.
     */
    public static List<Integer> naiveSearch(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new ArrayList<>();
        }
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == m) {
                matches.add(i);
            }
        }
        return matches;
    }
}
