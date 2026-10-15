# RailSync — Intelligent Railway Operations System
### A Data Structures and Algorithms (DSA) Core Java Academic Suite

---

## 1. Project Overview

**RailSync** is a terminal-based, Core Java academic application designed to demonstrate how fundamental Data Structures and Algorithms solve critical, real-world problems in railway operations management. 

Developed strictly in **Java standard library (Java 17+)** with zero external dependencies, RailSync provides an interactive console application that integrates realistic railway datasets with six core algorithmic paradigms: string matching, suffix indexing, dynamic programming, network flow, NP-hard approximation, and randomized algorithms.

> **Academic Context**: RailSync is designed for Computer Science & Engineering (B.Tech / MCA) coursework, lab practicals, and viva examinations. It operates purely in the terminal and runs seamlessly in **Eclipse IDE** as a standard Java Project.

---

## 2. Problem Statement & Objectives

Modern railway operations face complex computational challenges:
1. **Record Retrieval**: Millions of passengers and operators search for trains, stations, and alert advisories simultaneously.
2. **Document Indexing**: Operational rulebooks, safety circulars, and dispatch directives require rapid sub-millisecond phrase search without repeatedly scanning large documents.
3. **Misspelled Queries**: Passengers and booking clerks often enter misspelled or phonetically inaccurate station names (e.g., "mumba", "nuw delhi", "hovrah").
4. **Network Throughput**: Determining how many trains can travel through multi-junction corridors without causing bottleneck jams requires maximum-flow optimization.
5. **Conflict Resolution**: High-density junctions have intersecting diamond crossings and scissor switches that cannot be occupied simultaneously; selecting a minimal set of control nodes to cover all conflicts is NP-hard.
6. **Fleet Prioritization**: Sorting trains by delay, passenger volume, or timetable order needs high performance while avoiding deterministic worst-case stalls on partially sorted lists.

**RailSync Objectives:**
- Implement six core algorithms completely from scratch using standard Java.
- Ground each algorithm in a practical railway operational domain with realistic local datasets.
- Provide comprehensive correctness tests verifying mathematical properties.
- Deliver empirical benchmarking against standard baselines with realistic observations.

---

## 3. The Six Implemented DSA Modules

| Module | Category | Algorithm | Class | Railway Application |
|---|---|---|---|---|
| **Module 1** | String Algorithms | **Knuth–Morris–Pratt (KMP)** | `KMP.java` | Fast keyword search in trains, stations, and alerts using LPS array |
| **Module 2** | Suffix Structures | **Suffix Array (Prefix-Doubling)** | `SuffixArray.java` | Full-text indexed phrase lookup in operations manuals and logs |
| **Module 3** | Dynamic Programming | **Levenshtein Edit Distance** | `LevenshteinDistance.java` | Fuzzy station name suggestions for misspelled passenger input |
| **Module 4** | Network Flow | **Edmonds–Karp (BFS Flow)** | `EdmondsKarp.java` | Bottleneck throughput analysis on multi-junction railway corridors |
| **Module 5** | NP-Approximation | **Vertex Cover 2-Approximation** | `VertexCoverApproximation.java` | Junction collision conflict resolution across track interlockings |
| **Module 6** | Randomized Algorithms | **Randomized QuickSort** | `RandomizedQuickSort.java` | In-place fleet sorting by delay, passenger count, or train ID |

---

## 4. In-Depth Algorithm Explanations & Complexities

### Module 1: Knuth–Morris–Pratt (KMP)
- **Problem Solved**: Searches for keywords (e.g., train names, station names, delay reasons) in railway records.
- **Mechanism**: Precomputes a Longest Prefix Suffix (`LPS`) array in $O(m)$ time. When a character mismatch occurs after $j$ matched characters, the pattern shifts to `LPS[j - 1]` without ever rewinding the text pointer $i$.
- **Time Complexity**: $O(n + m)$ where $n$ is text length and $m$ is pattern length. (Compared to Naive search worst-case $O(n \cdot m)$).
- **Space Complexity**: $O(m)$ auxiliary space for the LPS integer array.

### Module 2: Suffix Array (Prefix-Doubling & Binary Search)
- **Problem Solved**: Enables sub-millisecond query indexing across `railway_documents.txt` (circulars, dispatch rules, safety guidelines).
- **Mechanism**:
  1. Constructs the Suffix Array using the Prefix-Doubling method. Ranks suffixes in doubling phases ($2^0, 2^1, 2^2, \dots$) using rank pairs $(rank_1, rank_2)$.
  2. Queries are answered via two binary searches on the lexicographically sorted suffix array to locate the contiguous range $[L, R]$ of suffixes starting with the pattern prefix.
  3. Returns occurrences sorted in ascending document order and calculates line and approximate word offsets.
- **Time Complexity**: Construction: $O(n \log^2 n)$; Query search: $O(m \log n)$.
- **Space Complexity**: $O(n)$ space for suffix indices and rank tables.

### Module 3: Levenshtein Edit Distance (Dynamic Programming)
- **Problem Solved**: Suggests the top 5 closest valid station names when users enter misspelled station names.
- **Mechanism**: Fills a $(n+1) \times (m+1)$ DP table where $DP[i][j]$ is the minimum number of insertions, deletions, or substitutions required to transform prefix $s_1[0..i-1]$ into $s_2[0..j-1]$.
- **Time Complexity**: $O(n \cdot m)$ time.
- **Space Complexity**: $O(n \cdot m)$ space (enabling educational DP grid rendering).

### Module 4: Edmonds–Karp (Network Flow)
- **Problem Solved**: Determines maximum train throughput slots (trains per dispatch window) that can pass from an origin station to a destination terminus through intermediate junction bottlenecks.
- **Mechanism**: Ford-Fulkerson method implemented using Breadth-First Search (BFS) to find the shortest augmenting paths (by number of edges) in the residual graph. Residual capacities are updated along each augmenting path until no $s \to t$ path exists.
- **Time Complexity**: $O(V \cdot E^2)$ where $V$ is junction count and $E$ is corridor track segments.
- **Space Complexity**: $O(V^2)$ for residual matrix and BFS parent pointers.

### Module 5: Vertex Cover 2-Approximation (NP-Hard Conflict Resolution)
- **Problem Solved**: High-speed turnouts, scissors crossovers, and diamond interlockings cannot be occupied by two train routes simultaneously. We need a covering set of control points that neutralizes all collision conflicts.
- **Why Approximation?**: Minimum Vertex Cover is NP-Hard. Finding the exact minimal cover requires $O(2^V)$ exponential time.
- **Mechanism**: The standard unweighted 2-approximation repeatedly selects an uncovered edge $(u, v)$, adds both endpoints $u$ and $v$ to the cover, and marks all incident edges as covered.
- **Theoretical Guarantee**: Because the selected edges form a matching $M$, any optimal cover $C^*$ must pick at least one vertex per matching edge ($|C^*| \ge |M|$). Our algorithm picks $2|M|$ vertices, guaranteeing $|C| \le 2 \cdot OPT$.
- **Time Complexity**: $O(V + E)$ polynomial time.
- **Space Complexity**: $O(V + E)$ edge list and coverage tracking.

### Module 6: Randomized QuickSort
- **Problem Solved**: Sorts the train fleet by delay minutes (urgency), passenger count (crowd management), or train number.
- **Why Random Pivot?**: Railway timetables and records are frequently partially or reverse sorted. Deterministic QuickSort (picking first/last element) suffers catastrophic $O(n^2)$ worst-case time on sorted data. Selecting a pivot uniformly at random guarantees balanced partitions in expectation.
- **Time Complexity**: Expected: $O(n \log n)$; Worst-Case: $O(n^2)$ (astronomically low probability); Best: $O(n \log n)$.
- **Space Complexity**: $O(\log n)$ auxiliary stack space.

---

## 5. Project Directory Structure

```
RailSync/
├── .classpath                          # Eclipse IDE classpath configuration
├── .project                            # Eclipse IDE project definition
├── README.md                           # Comprehensive documentation and manual
├── data/                               # Local CSV & TXT operational dataset
│   ├── alerts.csv                      # 12 Service alerts across 4 severity tiers
│   ├── railway_documents.txt           # Operations manual, dispatch rules & safety logs
│   ├── schedules.csv                   # 24 Stop-level timetable schedules
│   ├── stations.csv                    # 14 Railway junction and terminal stations
│   └── trains.csv                      # 18 Express & passenger trains with delay metrics
├── results/
│   └── benchmark_report.txt            # Empirical benchmark output generated by the app
└── src/
    └── railsync/
        ├── Main.java                   # Terminal menu entry point
        ├── algorithms/
        │   ├── m1/
        │   │   └── KMP.java            # Knuth-Morris-Pratt string searching & LPS array
        │   ├── m2/
        │   │   └── SuffixArray.java    # Prefix-Doubling Suffix Array & Binary Search
        │   ├── m3/
        │   │   └── LevenshteinDistance.java # DP Edit Distance & Station Fuzzy Search
        │   ├── m4/
        │   │   └── EdmondsKarp.java    # Network Max Flow with BFS augmenting paths
        │   ├── m5/
        │   │   └── VertexCoverApproximation.java # 2-Approximation for interlocking conflicts
        │   └── m6/
        │       └── RandomizedQuickSort.java # In-place QuickSort with random pivot
        ├── benchmark/
        │   └── BenchmarkRunner.java    # Nanosecond benchmark harness & report generator
        ├── data/
        │   └── RailwayDataLoader.java  # Robust CSV/TXT parser with auto path resolution
        ├── model/
        │   ├── Schedule.java           # Timetable stop entity
        │   ├── ServiceAlert.java       # Operational disruption alert entity
        │   ├── Station.java            # Railway station node entity
        │   └── Train.java              # Train fleet entity
        ├── service/
        │   └── RailwayService.java     # Business logic linking dataset with algorithms
        └── test/
            └── CorrectnessTests.java   # Independent unit test suite (23 test cases)
```

---

## 6. Railway Dataset Specifications

The project includes realistic fictional datasets in `data/`:

1. **`stations.csv` (14 Stations)**:
   - Contains major hubs: New Delhi Central, Mumbai CSMT, Howrah Jn, Chennai Central, Bengaluru City Jn, Ahmedabad Jn, Jaipur Jn, Lucknow Charbagh, Hyderabad Deccan, Patna Jn, Pune Jn, Chandigarh Jn, Bhopal Jn, Varanasi Cantt.
   - Fields: `station_id,station_name,city,platform_count,station_type`

2. **`trains.csv` (18 Trains)**:
   - Includes Rajdhani, Shatabdi, Vande Bharat, Gitanjali Express, Coromandel Express, Duronto, etc.
   - Fields: `train_id,train_name,source_station,destination_station,departure_time,arrival_time,delay_minutes,passenger_count,status`
   - Features varied delays (0 to 180 minutes), passenger loads (620 to 1,420), and operational statuses.

3. **`schedules.csv` (24 Timetable Stops)**:
   - Links train trips with specific station platforms, arrival times, and departure times.
   - Fields: `schedule_id,train_id,station_id,arrival_time,departure_time,platform_number`

4. **`alerts.csv` (12 Service Alerts)**:
   - Disruption alerts with severities: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
   - Alert types: `DELAY`, `TRACK_MAINTENANCE`, `WEATHER`, `SIGNAL_FAILURE`, `OVERCROWDING`, `TECHNICAL`, `SECURITY`, `POWER_OUTAGE`.

5. **`railway_documents.txt`**:
   - Comprehensive operations manual with 5 distinct operational sections: Priority Clearance, Platform Congestion, Emergency Track Maintenance, Signal Failure Contingency & Fog Protocol, and Crew Turnaround Instructions.

---

## 7. Setup & Execution in Eclipse IDE

### Step 1: Open Eclipse IDE
Launch your Eclipse IDE for Java Developers (Eclipse 2021 or later).

### Step 2: Import the Project
1. Go to **File → Import...**
2. In the Import wizard, select **General → Existing Projects into Workspace**, then click **Next**.
3. Under **Select root directory**, click **Browse...** and select the folder:
   ```
   d:\eclipse-java-2025-09-R-win32-x86_64\eclipse\Project_Dsa\RailSync
   ```
4. Verify that **RailSync** appears checked in the Projects list.
5. Click **Finish**.

### Step 3: Configure JDK / JRE
1. Right-click the **RailSync** project in Package Explorer → **Properties**.
2. Select **Java Build Path** on the left, then open the **Libraries** tab.
3. Verify that **JRE System Library** is set to **JavaSE-17** or higher (Java 17, 21, or 25).
4. If marked unbound, click **JRE System Library** → **Edit...** → select **Workspace default JRE** (or an installed JDK 17+) → **Finish**.
5. Check the **Source** tab to ensure `RailSync/src` is marked as a source folder, with default output folder `RailSync/bin`. Click **Apply and Close**.

### Step 4: Run the Application
1. In Package Explorer, expand `src` → `railsync`.
2. Right-click **`Main.java`**.
3. Select **Run As → Java Application**.
4. The interactive terminal menu will appear in the Eclipse **Console** view at the bottom!

---

## 8. Command-Line Compilation & Execution

If compiling directly from a terminal or PowerShell:

```powershell
# Navigate to project directory
cd d:\eclipse-java-2025-09-R-win32-x86_64\eclipse\Project_Dsa\RailSync

# Compile all source files into bin/
javac -d bin -cp bin src/railsync/model/*.java src/railsync/data/*.java src/railsync/algorithms/m1/*.java src/railsync/algorithms/m2/*.java src/railsync/algorithms/m3/*.java src/railsync/algorithms/m4/*.java src/railsync/algorithms/m5/*.java src/railsync/algorithms/m6/*.java src/railsync/service/*.java src/railsync/test/*.java src/railsync/benchmark/*.java src/railsync/Main.java

# Run Main Interactive Application
java -cp bin railsync.Main

# Run Correctness Tests independently
java -cp bin railsync.test.CorrectnessTests

# Run Performance Benchmarks independently
java -cp bin railsync.benchmark.BenchmarkRunner
```

---

## 9. Terminal Feature Walkthrough & Sample Outputs

### Main Menu
```
========================================
             RAILSYNC
 Intelligent Railway Operations System
========================================

1. View Railway Data
2. Search Railway Records (KMP)
3. Search Railway Documents (Suffix Array)
4. Find Similar Station Names (Edit Distance)
5. Analyze Railway Network Flow (Edmonds-Karp)
6. Analyze Railway Conflicts (Vertex Cover Approximation)
7. Sort Train Records (Randomized QuickSort)
8. Run Algorithm Correctness Tests
9. Run Performance Benchmarks
0. Exit

Enter your choice: 
```

### Feature 2 Sample: KMP Record Search (`fog`)
```
=================================================================================
  KMP SEARCH RESULTS FOR PATTERN: "fog" (Case-Insensitive)
=================================================================================

[1] Matching Train Records (0 found):
    (No trains matched the query)

[2] Matching Station Records (0 found):
    (No stations matched the query)

[3] Matching Service Alerts (1 found):
    - [ALT203] Severity: CRITICAL | Train: 12012  | Station: Patna Junction         | Type: WEATHER           
       Details: Dense morning fog causing cautionary speed restriction of 30 kmph
---------------------------------------------------------------------------------
KMP Search Summary: Found 1 total record matches across all files for "fog".
Algorithm: O(n + m) time using computed Longest Prefix Suffix (LPS) array.
---------------------------------------------------------------------------------
```

### Feature 3 Sample: Suffix Array Document Search (`priority clearance`)
```
=================================================================================
  SUFFIX ARRAY DOCUMENT SEARCH RESULTS FOR: "priority clearance"
=================================================================================
Query Time: 0.124 ms (124,300 ns) | Occurrences Found: 5

Match #1:
  - Location Details : Character Offset: 139 | Word #: ~20 | Line #: 5
  - Snippet Context  : "...1 All express corridor movements must receive priority clearance over freight services during peak commute..."

Match #2:
  - Location Details : Character Offset: 279 | Word #: ~40 | Line #: 6
  - Snippet Context  : "...2 When an express train approaches a junction, priority clearance must be logged by the section controller..."
...
```

### Feature 4 Sample: Levenshtein Station Name Match (`nuw delhi`)
```
=================================================================================
  LEVENSHTEIN EDIT DISTANCE: STATION SUGGESTIONS FOR "nuw delhi"
=================================================================================
[*] Input likely contains typos. Minimum edit distance found: 1

Top Closest Station Recommendations:
  1. Distance:  1 | New Delhi Central            (ID: STN101, City: New Delhi)
  2. Distance:  7 | Mumbai Chhatrapati Shivaji   (ID: STN102, City: Mumbai)
  3. Distance:  7 | Pune Junction                (ID: STN111, City: Pune)

--- Step-by-Step DP Matrix Visualization (Top Match) ---
DP Table for s1="nuw delhi" vs s2="new delhi":

       ε   n   e   w       d   e   l   h   i 
 ε |  0   1   2   3   4   5   6   7   8   9 
 n |  1   0   1   2   3   4   5   6   7   8 
 u |  2   1   1   2   3   4   5   6   7   8 
 w |  3   2   2   1   2   3   4   5   6   7 
   |  4   3   3   2   1   2   3   4   5   6 
 d |  5   4   4   3   2   1   2   3   4   5 
 e |  6   5   4   4   3   2   1   2   3   4 
 l |  7   6   5   5   4   3   2   1   2   3 
 h |  8   7   6   6   5   4   3   2   1   2 
 i |  9   8   7   7   6   5   4   3   2   1 

Final Levenshtein Distance: 1
```

### Feature 5 Sample: Edmonds–Karp Network Flow Analysis
```
=================================================================================
      EDMONDS-KARP MAXIMUM FLOW ANALYSIS: CORRIDOR BOTTLENECK SIMULATION        
=================================================================================
Maximum Railway Flow from [New Delhi Origin (DEL)] to [Howrah Terminus (HWH)]: 28 capacity units (trains/slot)

Augmenting Paths Found by BFS:
  Path 1: New Delhi Origin (DEL) -> Kanpur Central (CNB) -> Prayagraj Junction (PRYJ) -> Howrah Terminus (HWH) | Pushed Flow: +7
  Path 2: New Delhi Origin (DEL) -> Moradabad Junction (MB) -> Lucknow Charbagh (LKO) -> Pt. DDU Junction (DDU) -> Howrah Terminus (HWH) | Pushed Flow: +12
  Path 3: New Delhi Origin (DEL) -> Kanpur Central (CNB) -> Prayagraj Junction (PRYJ) -> Pt. DDU Junction (DDU) -> Howrah Terminus (HWH) | Pushed Flow: +9

Corridor Track Utilization Breakdown (Utilized Flow / Maximum Capacity):
  New Delhi Origin (DEL)       -> Moradabad Junction (MB)      : 12 / 14 slots
  New Delhi Origin (DEL)       -> Kanpur Central (CNB)         : 16 / 18 slots
  Moradabad Junction (MB)      -> Lucknow Charbagh (LKO)       : 12 / 12 slots
  Kanpur Central (CNB)         -> Prayagraj Junction (PRYJ)    : 16 / 16 slots
  Pt. DDU Junction (DDU)       -> Howrah Terminus (HWH)        : 21 / 22 slots
```

### Feature 6 Sample: Vertex Cover 2-Approximation Conflict Resolution
```
=================================================================================
  RAILWAY CONFLICT RESOLUTION: VERTEX COVER 2-APPROXIMATION ANALYSIS             
=================================================================================
Computed Approximate Vertex Cover Set (Monitored Control Nodes):
  [V] S1: Main Platform 1 Throat Switch (Index 0)
  [V] INT2: Central Diamond Crossover (Index 1)
  [V] SW3: Coaching Yard Shunting Siding (Index 2)
  [V] P4: Freight Loop Bypass Crossover (Index 3)
  [V] INT5: Outer Yard Junction Interlock (Index 4)
  [V] S6: Platform 2-3 Scissors Crossover (Index 5)
  [V] T7: Locomotive Shed Turnout Siding (Index 6)
  [V] P8: High-Speed Through Chord Signal (Index 7)

Integrity Verification Check:
  [PASS] Every conflict edge has AT LEAST ONE endpoint in the computed cover.

Approximation Theoretical Guarantee:
  - Selected Maximal Matching Edges (|M|): 4
  - Resulting Approximate Cover Size (|C| = 2*|M|): 8 vertices
  - Theoretical Lower Bound for OPT: |C*| >= |M| = 4
  - Guarantee: |C| <= 2 * |C*| (At most twice the optimal size).
```

### Feature 7 Sample: Train Fleet Sorting (Highest Delay First)
```
---------------------------------------------------------------------------------------------------------------------------
ID     | TRAIN NAME               | ROUTE (SOURCE -> DESTINATION)                    | DEP   | ARR   | DELAY     | PAX   | STATUS
---------------------------------------------------------------------------------------------------------------------------
12012  | Ganga Kaveri Express     | Patna Junction -> Chennai Central                | 18:10 | 06:50 | 180 min   | 1140  | DELAYED
12005  | Coromandel Express       | Howrah Junction -> Chennai Central               | 15:20 | 17:00 | 120 min   | 1420  | DELAYED
12010  | Kalka Mail               | Howrah Junction -> Chandigarh Junction           | 19:40 | 04:30 |  90 min   | 1310  | DELAYED
12018  | Sampark Kranti Express   | Howrah Junction -> New Delhi Central             | 13:10 | 10:15 |  60 min   | 1190  | DELAYED
12004  | Gitanjali Express        | Mumbai Chhatrapati Shivaji -> Howrah Junction    | 06:00 | 13:30 |  45 min   | 1350  | DELAYED
...
Sorting Performance: 18 train records sorted in 0.185 ms (185,200 ns)
```

---

## 10. Correctness Testing Suite

The project includes `CorrectnessTests.java` with **23 rigorous mathematical test cases**:

```
================================================================
          RAILSYNC ALGORITHM CORRECTNESS TEST SUITE             
================================================================

--- [Module 1] Knuth-Morris-Pratt (KMP) Tests ---
  [PASS] 1.1 LPS Array Calculation                     | Expected: [0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 5]
  [PASS] 1.2 Multiple Occurrences Match                | Positions: [0, 13, 24]
  [PASS] 1.3 Missing Pattern Handling                  | Found 0 matches (Expected 0)
  [PASS] 1.4 Case-Insensitive Search                   | Match at index: [4]

--- [Module 2] Suffix Array (Prefix-Doubling) Tests ---
  [PASS] 2.1 Suffix Array for 'banana'                 | Expected: [5, 3, 1, 0, 4, 2]
  [PASS] 2.2 Binary Search on SA ('an')                | Found positions: [1, 3]
  [PASS] 2.3 Phrase Query in Railway Doc               | Found positions for 'track': [10, 37]
  [PASS] 2.4 Non-existent Pattern Search               | Found matches: 0

--- [Module 3] Levenshtein Distance DP Tests ---
  [PASS] 3.1 Classic 'kitten' vs 'sitting'             | Expected: 3, Got: 3
  [PASS] 3.2 Identical String Distance                 | Expected: 0, Got: 0
  [PASS] 3.3 Single Letter Typo Normalized             | Expected: 1, Got: 1
  [PASS] 3.4 Empty String Boundary Case                | Expected: 5, Got: 5

--- [Module 4] Edmonds-Karp Max Flow Tests ---
  [PASS] 4.1 Textbook 4-Node Max Flow                  | Expected Max Flow: 20, Got: 20
  [PASS] 4.2 Bottleneck Flow Verification              | Expected Max Flow: 8, Got: 8
  [PASS] 4.3 Rail Network Flow Simulation              | Computed Max Flow DEL->HWH: 28 trains/slot

--- [Module 5] Vertex Cover 2-Approximation Tests ---
  [PASS] 5.1 Triangle Graph (K3) Coverage              | Cover: [0, 1], Valid: true
  [PASS] 5.2 Railway Interlocking Cover Validity       | Every conflict edge covered: true (|C|=8)
  [PASS] 5.3 Star Graph Complete Edge Coverage         | Cover: [0, 1], Valid: true

--- [Module 6] Randomized QuickSort Tests ---
  [PASS] 6.1 Standard Unsorted Array                   | Sorted Array verified
  [PASS] 6.2 Empty and Single-Element Arrays           | Handled without exceptions
  [PASS] 6.3 All-Duplicates Array                      | Sorted Duplicates verified
  [PASS] 6.4 Reverse-Sorted Array Resilience           | Sorted verified
  [PASS] 6.5 Railway Train Sorting by Delay (Desc)     | Delays: 120, 45, 15, 0

================================================================
TEST SUITE SUMMARY: 23 / 23 Tests Passed (100.0%)
STATUS: ALL ALGORITHM CORRECTNESS TESTS PASSED SUCCESSFULLY!
================================================================
```

---

## 11. Performance Benchmarking & Viva Discussion

Running Menu Option 9 executes `BenchmarkRunner.java`, generating a detailed report saved to `results/benchmark_report.txt`:

1. **KMP vs. Naive Substring Search**:
   - On pathological/repetitive datasets ($A^k B$), naive search backtracks repeatedly causing $O(n \cdot m)$ degradation. KMP advances without text pointer rewinds using the LPS table, executing up to $2.3\times$ faster.
2. **Randomized QuickSort vs. `Arrays.sort`**:
   - Randomized QuickSort achieves consistent $O(n \log n)$ performance across uniform random, already-sorted, and reverse-sorted distributions. Selecting pivots randomly prevents worst-case $O(n^2)$ recursion depth on ordered inputs.
3. **Academic Observation**:
   - Single-run microbenchmarks reflect JVM JIT compilation stages, hardware branch prediction, and garbage collection. Both algorithms verify identical results on identical arrays.

---

## 12. Viva Examination Questions & Answers

1. **Q: Why is KMP better than Naive String Matching?**  
   *A*: Naive search backtracks the text index $i$ on every mismatch, leading to $O(n \cdot m)$ worst-case time on repetitive patterns like `AAAAAB`. KMP precomputes the Longest Prefix Suffix (LPS) array in $O(m)$ time, ensuring that the text pointer $i$ never rewinds, yielding strictly $O(n + m)$ time.

2. **Q: How does Prefix-Doubling construct a Suffix Array in $O(n \log^2 n)$?**  
   *A*: Instead of comparing full suffixes in $O(n)$ at each step, prefix-doubling sorts suffixes based on a 2-tuple of ranks: $(rank(2^{k-1}), rank(next\ 2^{k-1}))$. After $\lceil \log_2 n \rceil$ doubling steps, suffixes are sorted. Each step takes $O(n \log n)$ comparison sorting.

3. **Q: What recurrence relation defines Levenshtein Edit Distance?**  
   *A*:  
   $$DP[i][j] = \begin{cases} 
   DP[i-1][j-1] & \text{if } s_1[i-1] == s_2[j-1] \\ 
   1 + \min(DP[i-1][j], DP[i][j-1], DP[i-1][j-1]) & \text{otherwise} 
   \end{cases}$$  
   representing deletion, insertion, and substitution respectively.

4. **Q: Why does Edmonds–Karp terminate, and what is its complexity?**  
   *A*: Edmonds–Karp uses BFS to choose the shortest augmenting path (minimum number of edges) in the residual graph. The length of the shortest path from source to any node never decreases. Each edge can be critical at most $V/2$ times. With $E$ edges and BFS taking $O(E)$, the total time complexity is $O(V \cdot E^2)$.

5. **Q: Why is the Vertex Cover algorithm a 2-approximation?**  
   *A*: The edges picked by the algorithm form a maximal matching $M$ where no two edges share an endpoint. Any valid vertex cover must select at least one vertex from each independent edge in $M$, so $|C^*| \ge |M|$. Our algorithm takes both endpoints of each edge in $M$, giving $|C| = 2|M|$. Therefore, $|C| = 2|M| \le 2|C^*| = 2 \cdot OPT$.

6. **Q: What is the role of the random pivot in Randomized QuickSort?**  
   *A*: Deterministic QuickSort picking the first/last element has a worst-case $O(n^2)$ time on already-sorted or reverse-sorted lists. By picking a pivot uniformly at random from $[low..high]$, the expected tree depth is $2 \ln n \approx 1.39 \log_2 n$, ensuring expected $O(n \log n)$ time regardless of initial ordering.

---

## 13. Limitations & Future Enhancements

- **Educational Network Size**: The Edmonds-Karp network and Vertex Cover graph use curated 7-node and 8-node graphs for maximum readability and step-by-step tracing during viva presentations.
- **Future Expansion**:
  - Extend the network flow solver to support dynamic timetable-based time-expanded networks.
  - Implement Dinic's algorithm ($O(V^2 E)$) or Push-Relabel for large-scale national networks exceeding 10,000 stations.
  - Integrate A* search for multi-criteria train journey planning (shortest time + minimum fare).

---
*RailSync — Developed for Academic Excellence in Data Structures & Algorithms.*
