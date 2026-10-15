package railsync.algorithms.m4;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * ============================================================================
 * MODULE 4: NETWORK FLOW — EDMONDS-KARP ALGORITHM
 * ============================================================================
 * Problem Solved:
 * Determines the maximum throughput capacity of trains that can transit through
 * a bottlenecked railway junction network from a designated origin corridor
 * to a destination terminus without exceeding corridor track capacity limits.
 *
 * How it Works:
 * 1. Edmonds-Karp is a specific implementation of the Ford-Fulkerson method
 *    that uses Breadth-First Search (BFS) to find the shortest augmenting
 *    path (in terms of number of edges) in the residual network.
 * 2. In each iteration:
 *    - BFS explores the residual graph from source (s) to sink (t).
 *    - If an augmenting path is found, the bottleneck capacity (minimum
 *      residual capacity along the path) is identified.
 *    - The bottleneck flow is added to the total flow.
 *    - Forward edge residual capacities are decreased, and reverse edge
 *      residual capacities are increased by the bottleneck amount.
 * 3. The process repeats until no augmenting path exists from s to t in
 *    the residual network (Max-Flow Min-Cut Theorem).
 *
 * Time Complexity:
 * - O(V * E^2) where V is the number of stations/junctions and E is track edges.
 *   (Each edge can become a bottleneck at most O(V) times, and BFS takes O(E)).
 *
 * Space Complexity:
 * - O(V^2) or O(V + E) for residual capacity representation and parent pointers.
 *
 * Note on Railway Context:
 * - Edge capacities represent hypothetical train throughput slots (trains per
 *   operating window), reflecting physical track, signaling, and loop capacities.
 * ============================================================================
 */
public class EdmondsKarp {

    public static class FlowResult {
        private final int source;
        private final int sink;
        private final int maxFlow;
        private final List<AugmentingPathStep> pathSteps;
        private final int[][] finalFlow;
        private final String[] nodeNames;

        public FlowResult(int source, int sink, int maxFlow, List<AugmentingPathStep> pathSteps,
                          int[][] finalFlow, String[] nodeNames) {
            this.source = source;
            this.sink = sink;
            this.maxFlow = maxFlow;
            this.pathSteps = pathSteps;
            this.finalFlow = finalFlow;
            this.nodeNames = nodeNames;
        }

        public int getMaxFlow() {
            return maxFlow;
        }

        public List<AugmentingPathStep> getPathSteps() {
            return pathSteps;
        }

        public int[][] getFinalFlow() {
            return finalFlow;
        }

        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Maximum Railway Flow from [%s] to [%s]: %d capacity units (trains/slot)\n\n",
                    nodeNames[source], nodeNames[sink], maxFlow));
            sb.append("Augmenting Paths Found by BFS:\n");
            if (pathSteps.isEmpty()) {
                sb.append("  (No path exists between the selected source and sink)\n");
            } else {
                for (int i = 0; i < pathSteps.size(); i++) {
                    AugmentingPathStep step = pathSteps.get(i);
                    sb.append(String.format("  Path %d: %s | Pushed Flow: +%d\n",
                            i + 1, step.formatPath(nodeNames), step.bottleneckFlow));
                }
            }
            return sb.toString();
        }
    }

    public static class AugmentingPathStep {
        public final List<Integer> nodes;
        public final int bottleneckFlow;

        public AugmentingPathStep(List<Integer> nodes, int bottleneckFlow) {
            this.nodes = nodes;
            this.bottleneckFlow = bottleneckFlow;
        }

        public String formatPath(String[] names) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nodes.size(); i++) {
                int node = nodes.get(i);
                sb.append(names != null && node < names.length ? names[node] : "Node " + node);
                if (i < nodes.size() - 1) {
                    sb.append(" -> ");
                }
            }
            return sb.toString();
        }
    }

    /**
     * Executes the Edmonds-Karp algorithm to find the maximum flow.
     *
     * @param capacity  capacity matrix where capacity[u][v] is edge capacity
     * @param source    source node index
     * @param sink      sink node index
     * @param nodeNames optional array of readable station labels
     * @return FlowResult containing max flow and sequence of augmenting paths
     */
    public static FlowResult computeMaxFlow(int[][] capacity, int source, int sink, String[] nodeNames) {
        int n = capacity.length;
        if (source < 0 || source >= n || sink < 0 || sink >= n) {
            throw new IllegalArgumentException("Invalid source (" + source + ") or sink (" + sink + ")");
        }
        if (source == sink) {
            throw new IllegalArgumentException("Source and sink cannot be identical.");
        }

        // Initialize residual capacity matrix as a copy of initial capacity
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, n);
        }

        int maxFlow = 0;
        int[] parent = new int[n];
        List<AugmentingPathStep> pathSteps = new ArrayList<>();

        // Augment flow as long as there is a path from source to sink in residual network
        while (bfs(residual, source, sink, parent)) {
            // Find bottleneck capacity along the augmenting path found by BFS
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            // Reconstruct path nodes for tracing
            List<Integer> pathNodes = new ArrayList<>();
            for (int v = sink; v != source; v = parent[v]) {
                pathNodes.add(0, v);
            }
            pathNodes.add(0, source);
            pathSteps.add(new AugmentingPathStep(pathNodes, pathFlow));

            // Update residual capacities of the edges and reverse edges
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        // Calculate final flow on each edge: capacity - residual
        int[][] finalFlow = new int[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (capacity[u][v] > 0) {
                    finalFlow[u][v] = capacity[u][v] - residual[u][v];
                }
            }
        }

        if (nodeNames == null || nodeNames.length != n) {
            nodeNames = new String[n];
            for (int i = 0; i < n; i++) nodeNames[i] = "Node " + i;
        }

        return new FlowResult(source, sink, maxFlow, pathSteps, finalFlow, nodeNames);
    }

    /**
     * Standard BFS to locate the shortest augmenting path (in number of edges).
     */
    private static boolean bfs(int[][] residual, int source, int sink, int[] parent) {
        int n = residual.length;
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[n];

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < n; v++) {
                // If not visited and positive residual capacity available
                if (!visited[v] && residual[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) {
                        return true; // Reached sink!
                    }
                    queue.offer(v);
                }
            }
        }
        return false;
    }

    /**
     * Builds a predefined 7-station northern trunk railway network for simulation.
     */
    public static RailwayNetworkSample getSampleRailwayNetwork() {
        String[] nodes = {
            "New Delhi Origin (DEL)",       // Node 0
            "Moradabad Junction (MB)",      // Node 1
            "Kanpur Central (CNB)",         // Node 2
            "Lucknow Charbagh (LKO)",       // Node 3
            "Prayagraj Junction (PRYJ)",    // Node 4
            "Pt. DDU Junction (DDU)",       // Node 5
            "Howrah Terminus (HWH)"         // Node 6
        };

        int n = nodes.length;
        int[][] cap = new int[n][n];

        // Hypothetical route capacities (trains/time slot)
        cap[0][1] = 14; // DEL -> MB
        cap[0][2] = 18; // DEL -> CNB
        cap[1][3] = 12; // MB -> LKO
        cap[2][3] = 8;  // CNB -> LKO
        cap[2][4] = 16; // CNB -> PRYJ
        cap[3][5] = 12; // LKO -> DDU
        cap[4][5] = 15; // PRYJ -> DDU
        cap[4][6] = 7;  // PRYJ -> HWH (Direct chord)
        cap[5][6] = 22; // DDU -> HWH (Main trunk corridor)

        return new RailwayNetworkSample(nodes, cap);
    }

    public static class RailwayNetworkSample {
        public final String[] nodeNames;
        public final int[][] capacity;

        public RailwayNetworkSample(String[] nodeNames, int[][] capacity) {
            this.nodeNames = nodeNames;
            this.capacity = capacity;
        }
    }
}
