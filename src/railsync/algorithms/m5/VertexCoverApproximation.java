package railsync.algorithms.m5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ============================================================================
 * MODULE 5: NP-COMPLETENESS & APPROXIMATION — VERTEX COVER 2-APPROXIMATION
 * ============================================================================
 * Problem Solved:
 * In a busy railway junction, simultaneous train movements through shared
 * physical interlocking switches, scissors crossings, and turnouts cause
 * route conflicts. A "Vertex Cover" on the conflict graph identifies a
 * subset of critical track switches/interlocking nodes to monitor or lock
 * such that EVERY potential collision conflict edge touches at least one
 * monitored node.
 *
 * NP-Completeness & Why We Use an Approximation:
 * - Finding the absolute minimum Vertex Cover is a classic NP-Hard problem.
 * - Exact algorithms run in exponential time O(2^V) or O(1.27^V), which becomes
 *   computationally infeasible for large interlocking networks.
 * - The polynomial-time 2-Approximation algorithm runs in O(V + E) time and
 *   guarantees that the resulting cover size is at most 2 * OPT (at most twice
 *   the size of the minimum possible vertex cover).
 *
 * How the 2-Approximation Works:
 * 1. Initialize Cover C = empty set.
 * 2. While there exists an edge e = (u, v) that is not yet covered:
 *    - Add BOTH endpoints u and v to C.
 *    - Remove (or mark as covered) all edges incident to either u or v.
 * 3. Return C.
 *
 * Proof of 2-Approximation Guarantee:
 * - Let M be the set of edges selected by the algorithm.
 * - Because we eliminate all incident edges whenever we pick an edge, no two
 *   edges in M share any vertex. Hence, M forms a MATCHING.
 * - Any valid vertex cover (including the optimal cover C*) MUST pick at least
 *   one vertex from each independent edge in M. Therefore: |C*| >= |M|.
 * - Our algorithm adds both endpoints of each edge in M, so |C| = 2 * |M|.
 * - Combining these gives: |C| = 2 * |M| <= 2 * |C*|.
 * - Conclusion: |C| <= 2 * OPT.
 * ============================================================================
 */
public class VertexCoverApproximation {

    public static class Edge {
        public final int u;
        public final int v;
        public final String conflictDescription;

        public Edge(int u, int v, String conflictDescription) {
            this.u = u;
            this.v = v;
            this.conflictDescription = conflictDescription;
        }

        @Override
        public String toString() {
            return String.format("(%d <-> %d) [%s]", u, v, conflictDescription);
        }
    }

    public static class ConflictGraph {
        public final int numVertices;
        public final String[] vertexNames;
        public final List<Edge> edges;

        public ConflictGraph(int numVertices, String[] vertexNames, List<Edge> edges) {
            this.numVertices = numVertices;
            this.vertexNames = vertexNames;
            this.edges = edges;
        }
    }

    public static class ApproximationResult {
        public final Set<Integer> coverVertices;
        public final List<Edge> chosenMatchingEdges;
        public final boolean isValidCover;
        public final String explanation;

        public ApproximationResult(Set<Integer> coverVertices, List<Edge> chosenMatchingEdges,
                                   boolean isValidCover, String explanation) {
            this.coverVertices = coverVertices;
            this.chosenMatchingEdges = chosenMatchingEdges;
            this.isValidCover = isValidCover;
            this.explanation = explanation;
        }
    }

    /**
     * Executes the 2-approximation vertex cover algorithm on the provided conflict graph.
     *
     * @param graph conflict graph of railway interlocking elements
     * @return ApproximationResult containing cover, matching edges, and verification
     */
    public static ApproximationResult computeVertexCover(ConflictGraph graph) {
        if (graph == null || graph.edges == null || graph.edges.isEmpty()) {
            return new ApproximationResult(new HashSet<>(), new ArrayList<>(), true, "Empty graph.");
        }

        int m = graph.edges.size();
        boolean[] edgeCovered = new boolean[m];
        Set<Integer> cover = new HashSet<>();
        List<Edge> matchingEdges = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            if (!edgeCovered[i]) {
                Edge e = graph.edges.get(i);
                // Add both endpoints of the uncovered edge to cover
                cover.add(e.u);
                cover.add(e.v);
                matchingEdges.add(e);

                // Mark all edges incident to either u or v as covered
                for (int j = 0; j < m; j++) {
                    Edge other = graph.edges.get(j);
                    if (other.u == e.u || other.v == e.u || other.u == e.v || other.v == e.v) {
                        edgeCovered[j] = true;
                    }
                }
            }
        }

        // Verify that every single edge in the original graph has at least one endpoint in the cover
        boolean isValid = verifyCover(graph.edges, cover);

        StringBuilder sb = new StringBuilder();
        sb.append("Approximation Theoretical Guarantee:\n");
        sb.append(String.format("  - Selected Maximal Matching Edges (|M|): %d\n", matchingEdges.size()));
        sb.append(String.format("  - Resulting Approximate Cover Size (|C| = 2*|M|): %d vertices\n", cover.size()));
        sb.append("  - Theoretical Lower Bound for OPT: |C*| >= |M| = ").append(matchingEdges.size()).append("\n");
        sb.append("  - Guarantee: |C| <= 2 * |C*| (At most twice the optimal size).\n");
        sb.append("  - Note: This is a fast polynomial approximation, not guaranteed to be the strictly minimal cover.");

        return new ApproximationResult(cover, matchingEdges, isValid, sb.toString());
    }

    /**
     * Verifies whether the computed vertex set covers every edge in the graph.
     */
    public static boolean verifyCover(List<Edge> edges, Set<Integer> cover) {
        if (edges == null) return true;
        for (Edge e : edges) {
            if (!cover.contains(e.u) && !cover.contains(e.v)) {
                return false; // Found an uncovered edge!
            }
        }
        return true;
    }

    /**
     * Creates a realistic 8-node railway track interlocking conflict graph.
     */
    public static ConflictGraph getSampleConflictGraph() {
        String[] nodes = {
            "S1: Main Platform 1 Throat Switch",       // 0
            "INT2: Central Diamond Crossover",         // 1
            "SW3: Coaching Yard Shunting Siding",      // 2
            "P4: Freight Loop Bypass Crossover",       // 3
            "INT5: Outer Yard Junction Interlock",     // 4
            "S6: Platform 2-3 Scissors Crossover",     // 5
            "T7: Locomotive Shed Turnout Siding",      // 6
            "P8: High-Speed Through Chord Signal"      // 7
        };

        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, "Platform 1 dispatch conflicts with diamond crossover transit"));
        edges.add(new Edge(1, 2, "Diamond crossover overlaps coaching yard shunting route"));
        edges.add(new Edge(1, 5, "Diamond crossover intersects platform 2-3 scissors route"));
        edges.add(new Edge(2, 3, "Coaching yard shunting fouls freight loop clearance line"));
        edges.add(new Edge(3, 4, "Freight bypass locks outer yard junction point"));
        edges.add(new Edge(4, 5, "Outer yard junction overlaps platform scissors route"));
        edges.add(new Edge(5, 6, "Scissors route blocks locomotive shed ingress turnout"));
        edges.add(new Edge(6, 7, "Loco turnout requires flank protection from high-speed chord"));

        return new ConflictGraph(nodes.length, nodes, edges);
    }
}
