package railsync.test;

import railsync.algorithms.m1.KMP;
import railsync.algorithms.m2.SuffixArray;
import railsync.algorithms.m3.LevenshteinDistance;
import railsync.algorithms.m4.EdmondsKarp;
import railsync.algorithms.m5.VertexCoverApproximation;
import railsync.algorithms.m6.RandomizedQuickSort;
import railsync.model.Train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * ============================================================================
 * RAILSYNC ALGORITHM CORRECTNESS TEST RUNNER
 * ============================================================================
 * Rigorously validates all 6 DSA algorithms against known mathematical
 * inputs, textbook edge-cases, and expected outcomes without JUnit dependencies.
 *
 * Each test prints [PASS] or [FAIL] with full diagnostic details.
 * ============================================================================
 */
public class CorrectnessTests {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        runAllTests();
    }

    public static boolean runAllTests() {
        testsPassed = 0;
        testsTotal = 0;

        System.out.println("================================================================");
        System.out.println("          RAILSYNC ALGORITHM CORRECTNESS TEST SUITE             ");
        System.out.println("================================================================\n");

        testModule1KMP();
        testModule2SuffixArray();
        testModule3LevenshteinDistance();
        testModule4EdmondsKarp();
        testModule5VertexCover();
        testModule6RandomizedQuickSort();

        System.out.println("================================================================");
        System.out.printf("TEST SUITE SUMMARY: %d / %d Tests Passed (%.1f%%)\n",
                testsPassed, testsTotal, (testsPassed * 100.0) / testsTotal);
        if (testsPassed == testsTotal) {
            System.out.println("STATUS: ALL ALGORITHM CORRECTNESS TESTS PASSED SUCCESSFULLY!");
        } else {
            System.err.println("STATUS: SOME TESTS FAILED! PLEASE REVIEW DIAGNOSTICS ABOVE.");
        }
        System.out.println("================================================================\n");

        return testsPassed == testsTotal;
    }

    private static void recordResult(String testName, boolean condition, String details) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.printf("  [PASS] %-45s | %s\n", testName, details);
        } else {
            System.err.printf("  [FAIL] %-45s | %s\n", testName, details);
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 1 TESTS: KMP
    // -------------------------------------------------------------------------
    private static void testModule1KMP() {
        System.out.println("--- [Module 1] Knuth-Morris-Pratt (KMP) Tests ---");

        // Test 1.1: LPS Array Computation for "AABAACAABAA"
        String pattern = "AABAACAABAA";
        int[] expectedLps = {0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 5};
        int[] actualLps = KMP.computeLPS(pattern);
        recordResult("1.1 LPS Array Calculation",
                Arrays.equals(expectedLps, actualLps),
                "Expected: " + Arrays.toString(expectedLps) + ", Got: " + Arrays.toString(actualLps));

        // Test 1.2: Multiple Pattern Occurrences
        String text = "RAIL_EXPRESS_RAIL_LOCAL_RAIL";
        List<Integer> expectedIndices = Arrays.asList(0, 13, 24);
        List<Integer> actualIndices = KMP.search(text, "RAIL");
        recordResult("1.2 Multiple Occurrences Match",
                expectedIndices.equals(actualIndices),
                "Positions: " + actualIndices);

        // Test 1.3: Non-existent Pattern Search
        List<Integer> missingIndices = KMP.search("Shatabdi Express", "Rajdhani");
        recordResult("1.3 Missing Pattern Handling",
                missingIndices.isEmpty(),
                "Found " + missingIndices.size() + " matches (Expected 0)");

        // Test 1.4: Case-Insensitive Normalization
        List<Integer> caseMatches = KMP.searchIgnoreCase("New Delhi Central Station", "delhi");
        recordResult("1.4 Case-Insensitive Search",
                caseMatches.size() == 1 && caseMatches.get(0) == 4,
                "Match at index: " + caseMatches);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MODULE 2 TESTS: SUFFIX ARRAY
    // -------------------------------------------------------------------------
    private static void testModule2SuffixArray() {
        System.out.println("--- [Module 2] Suffix Array (Prefix-Doubling) Tests ---");

        // Test 2.1: Classic "banana" Suffix Array Construction
        String banana = "banana";
        int[] expectedSA = {5, 3, 1, 0, 4, 2}; // "a", "ana", "anana", "banana", "na", "nana"
        int[] actualSA = SuffixArray.buildSuffixArray(banana);
        recordResult("2.1 Suffix Array for 'banana'",
                Arrays.equals(expectedSA, actualSA),
                "Expected: " + Arrays.toString(expectedSA) + ", Got: " + Arrays.toString(actualSA));

        // Test 2.2: Binary Search over SA for "an" -> positions [1, 3] in text
        List<Integer> matchesBanana = SuffixArray.search(banana, actualSA, "an");
        List<Integer> expectedBanana = Arrays.asList(1, 3);
        recordResult("2.2 Binary Search on SA ('an')",
                expectedBanana.equals(matchesBanana),
                "Found positions: " + matchesBanana);

        // Test 2.3: Search in Railway Sentence
        String doc = "scheduled track maintenance requires track inspection";
        int[] docSA = SuffixArray.buildSuffixArray(doc);
        List<Integer> trackMatches = SuffixArray.search(doc, docSA, "track");
        List<Integer> expectedTrack = Arrays.asList(10, 37);
        recordResult("2.3 Phrase Query in Railway Doc",
                expectedTrack.equals(trackMatches),
                "Found positions for 'track': " + trackMatches);

        // Test 2.4: Query for non-existent phrase
        List<Integer> missingSA = SuffixArray.search(doc, docSA, "aeroplane");
        recordResult("2.4 Non-existent Pattern Search",
                missingSA.isEmpty(),
                "Found matches: " + missingSA.size());
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MODULE 3 TESTS: LEVENSHTEIN DISTANCE
    // -------------------------------------------------------------------------
    private static void testModule3LevenshteinDistance() {
        System.out.println("--- [Module 3] Levenshtein Distance DP Tests ---");

        // Test 3.1: Classic "kitten" to "sitting" = 3
        int distKitten = LevenshteinDistance.computeDistance("kitten", "sitting");
        recordResult("3.1 Classic 'kitten' vs 'sitting'",
                distKitten == 3,
                "Expected: 3, Got: " + distKitten);

        // Test 3.2: Identical Strings
        int distSame = LevenshteinDistance.computeDistance("Howrah Junction", "Howrah Junction");
        recordResult("3.2 Identical String Distance",
                distSame == 0,
                "Expected: 0, Got: " + distSame);

        // Test 3.3: Case Insensitive Station Typo ("nuw delhi" vs "New Delhi")
        int distTypo = LevenshteinDistance.computeDistanceIgnoreCase("nuw delhi", "New Delhi");
        recordResult("3.3 Single Letter Typo Normalized",
                distTypo == 1,
                "Expected: 1, Got: " + distTypo);

        // Test 3.4: Empty String Edge Cases
        int distEmpty = LevenshteinDistance.computeDistance("", "Patna");
        recordResult("3.4 Empty String Boundary Case",
                distEmpty == 5,
                "Expected: 5, Got: " + distEmpty);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MODULE 4 TESTS: EDMONDS-KARP MAX FLOW
    // -------------------------------------------------------------------------
    private static void testModule4EdmondsKarp() {
        System.out.println("--- [Module 4] Edmonds-Karp Max Flow Tests ---");

        // Test 4.1: Known 4-node flow network
        // Source=0, Sink=3. Edges: (0->1: 10), (0->2: 10), (1->2: 2), (1->3: 10), (2->3: 10)
        // Expected max flow = 20
        int[][] cap4 = new int[4][4];
        cap4[0][1] = 10;
        cap4[0][2] = 10;
        cap4[1][2] = 2;
        cap4[1][3] = 10;
        cap4[2][3] = 10;

        EdmondsKarp.FlowResult res4 = EdmondsKarp.computeMaxFlow(cap4, 0, 3, null);
        recordResult("4.1 Textbook 4-Node Max Flow",
                res4.getMaxFlow() == 20,
                "Expected Max Flow: 20, Got: " + res4.getMaxFlow());

        // Test 4.2: Linear Bottleneck Corridor (0 -> 1 -> 2 -> 3 with capacities 50, 8, 30)
        int[][] capLinear = new int[4][4];
        capLinear[0][1] = 50;
        capLinear[1][2] = 8;  // Bottleneck
        capLinear[2][3] = 30;

        EdmondsKarp.FlowResult resLinear = EdmondsKarp.computeMaxFlow(capLinear, 0, 3, null);
        recordResult("4.2 Bottleneck Flow Verification",
                resLinear.getMaxFlow() == 8,
                "Expected Max Flow: 8, Got: " + resLinear.getMaxFlow());

        // Test 4.3: Sample Railway Network Flow (DEL=0 to HWH=6)
        EdmondsKarp.RailwayNetworkSample railwayNet = EdmondsKarp.getSampleRailwayNetwork();
        EdmondsKarp.FlowResult resRail = EdmondsKarp.computeMaxFlow(
                railwayNet.capacity, 0, 6, railwayNet.nodeNames);
        // Validating that flow is positive and augmenting paths found
        recordResult("4.3 Rail Network Flow Simulation",
                resRail.getMaxFlow() > 0 && !resRail.getPathSteps().isEmpty(),
                "Computed Max Flow DEL->HWH: " + resRail.getMaxFlow() + " trains/slot across " +
                        resRail.getPathSteps().size() + " augmenting paths");
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MODULE 5 TESTS: VERTEX COVER 2-APPROXIMATION
    // -------------------------------------------------------------------------
    private static void testModule5VertexCover() {
        System.out.println("--- [Module 5] Vertex Cover 2-Approximation Tests ---");

        // Test 5.1: Triangle Graph K3 (vertices 0, 1, 2; edges (0,1), (1,2), (2,0))
        // Optimal vertex cover has size 2. 2-approximation picks 1 edge -> 2 endpoints.
        List<VertexCoverApproximation.Edge> triEdges = Arrays.asList(
                new VertexCoverApproximation.Edge(0, 1, "Edge 0-1"),
                new VertexCoverApproximation.Edge(1, 2, "Edge 1-2"),
                new VertexCoverApproximation.Edge(2, 0, "Edge 2-0")
        );
        VertexCoverApproximation.ConflictGraph triGraph =
                new VertexCoverApproximation.ConflictGraph(3, new String[]{"A", "B", "C"}, triEdges);
        VertexCoverApproximation.ApproximationResult triRes =
                VertexCoverApproximation.computeVertexCover(triGraph);

        recordResult("5.1 Triangle Graph (K3) Coverage",
                triRes.isValidCover && triRes.coverVertices.size() <= 2 * 2,
                "Cover: " + triRes.coverVertices + ", Valid: " + triRes.isValidCover);

        // Test 5.2: Railway Predefined Interlocking Conflict Graph
        VertexCoverApproximation.ConflictGraph railGraph = VertexCoverApproximation.getSampleConflictGraph();
        VertexCoverApproximation.ApproximationResult railRes =
                VertexCoverApproximation.computeVertexCover(railGraph);

        recordResult("5.2 Railway Interlocking Cover Validity",
                railRes.isValidCover,
                "Every conflict edge covered: " + railRes.isValidCover + " (|C|=" + railRes.coverVertices.size() + ")");

        // Test 5.3: Disconnected Star Graph
        List<VertexCoverApproximation.Edge> starEdges = Arrays.asList(
                new VertexCoverApproximation.Edge(0, 1, "Star 1"),
                new VertexCoverApproximation.Edge(0, 2, "Star 2"),
                new VertexCoverApproximation.Edge(0, 3, "Star 3")
        );
        VertexCoverApproximation.ConflictGraph starGraph =
                new VertexCoverApproximation.ConflictGraph(4, new String[]{"Center", "L1", "L2", "L3"}, starEdges);
        VertexCoverApproximation.ApproximationResult starRes =
                VertexCoverApproximation.computeVertexCover(starGraph);

        recordResult("5.3 Star Graph Complete Edge Coverage",
                starRes.isValidCover,
                "Cover: " + starRes.coverVertices + ", Valid: " + starRes.isValidCover);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MODULE 6 TESTS: RANDOMIZED QUICKSORT
    // -------------------------------------------------------------------------
    private static void testModule6RandomizedQuickSort() {
        System.out.println("--- [Module 6] Randomized QuickSort Tests ---");

        // Test 6.1: General Unsorted Array
        int[] arr1 = {45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 45, 8};
        RandomizedQuickSort.sort(arr1);
        recordResult("6.1 Standard Unsorted Array",
                isSorted(arr1),
                "Sorted Array: " + Arrays.toString(arr1));

        // Test 6.2: Empty & Single-Element Array
        int[] emptyArr = {};
        RandomizedQuickSort.sort(emptyArr);
        int[] singleArr = {99};
        RandomizedQuickSort.sort(singleArr);
        recordResult("6.2 Empty and Single-Element Arrays",
                emptyArr.length == 0 && singleArr[0] == 99,
                "Handled without exceptions.");

        // Test 6.3: Array with Identical Duplicate Elements
        int[] dupArr = {7, 7, 7, 7, 7, 7, 7};
        RandomizedQuickSort.sort(dupArr);
        recordResult("6.3 All-Duplicates Array",
                isSorted(dupArr),
                "Sorted Duplicates: " + Arrays.toString(dupArr));

        // Test 6.4: Reverse-Sorted Array (Classic QuickSort worst-case test)
        int[] revArr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        RandomizedQuickSort.sort(revArr);
        recordResult("6.4 Reverse-Sorted Array Resilience",
                isSorted(revArr),
                "Sorted: " + Arrays.toString(revArr));

        // Test 6.5: Sorting Railway Train Model Objects by Delay
        List<Train> testTrains = new ArrayList<>();
        testTrains.add(new Train(101, "Train A", "S1", "S2", "08:00", "12:00", 45, 800, "DELAYED"));
        testTrains.add(new Train(102, "Train B", "S1", "S2", "09:00", "13:00", 0, 1200, "ON_TIME"));
        testTrains.add(new Train(103, "Train C", "S1", "S2", "10:00", "14:00", 120, 950, "DELAYED"));
        testTrains.add(new Train(104, "Train D", "S1", "S2", "11:00", "15:00", 15, 600, "DELAYED"));

        RandomizedQuickSort.sort(testTrains, RandomizedQuickSort.BY_DELAY_DESC);
        boolean trainsSortedDesc = testTrains.get(0).getDelayMinutes() == 120 &&
                                   testTrains.get(1).getDelayMinutes() == 45 &&
                                   testTrains.get(2).getDelayMinutes() == 15 &&
                                   testTrains.get(3).getDelayMinutes() == 0;
        recordResult("6.5 Railway Train Sorting by Delay (Desc)",
                trainsSortedDesc,
                "Delays: " + testTrains.get(0).getDelayMinutes() + ", " +
                        testTrains.get(1).getDelayMinutes() + ", " +
                        testTrains.get(2).getDelayMinutes() + ", " +
                        testTrains.get(3).getDelayMinutes());
        System.out.println();
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
