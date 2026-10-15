package railsync.algorithms.m2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * MODULE 2: SUFFIX STRUCTURES — SUFFIX ARRAY (PREFIX-DOUBLING)
 * ============================================================================
 * Problem Solved:
 * Enable fast full-text indexed searches across railway operational manuals,
 * circulars, dispatch logs, and safety instructions without full-text scanning.
 *
 * How it Works:
 * 1. A Suffix Array stores the starting indices of all suffixes of a string
 *    sorted in lexicographical order.
 * 2. Construction uses the Prefix-Doubling Algorithm:
 *    - In step 0, suffixes are ranked by their first character (length 2^0 = 1).
 *    - In step k, suffixes are sorted using a tuple of two ranks:
 *      (rank of prefix of length 2^(k-1), rank of next 2^(k-1) characters).
 *    - Since lengths double (1, 2, 4, 8, ...), at most ceil(log2 n) doubling
 *      phases are required.
 * 3. Pattern Matching:
 *    - Because all suffixes in the Suffix Array are sorted, all occurrences
 *      of any query pattern appear as a contiguous range [L, R] in the array.
 *    - We locate this range using two binary searches (lower and upper bounds).
 *
 * Time Complexity:
 * - Construction: O(n log^2 n) using comparison sorting at each doubling step.
 * - Pattern Search: O(m log n) where m is pattern length and n is document size.
 *
 * Space Complexity:
 * - O(n) integer array storage for suffix ranks and indices.
 * ============================================================================
 */
public class SuffixArray {

    /**
     * Helper struct to hold suffix info during prefix-doubling.
     */
    private static class SuffixRank implements Comparable<SuffixRank> {
        int index;      // Suffix original starting position
        int rank1;      // Rank of first half (length 2^(k-1))
        int rank2;      // Rank of second half (length 2^(k-1))

        @Override
        public int compareTo(SuffixRank other) {
            if (this.rank1 != other.rank1) {
                return Integer.compare(this.rank1, other.rank1);
            }
            return Integer.compare(this.rank2, other.rank2);
        }
    }

    /**
     * Builds a Suffix Array for the given text using prefix-doubling.
     *
     * @param text input document text
     * @return integer array 'sa' where sa[i] is the starting index of the i-th lexicographical suffix
     */
    public static int[] buildSuffixArray(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }

        int n = text.length();
        SuffixRank[] suffixes = new SuffixRank[n];

        // Step 0: Initial ranking by the first character
        for (int i = 0; i < n; i++) {
            suffixes[i] = new SuffixRank();
            suffixes[i].index = i;
            suffixes[i].rank1 = (int) text.charAt(i);
            suffixes[i].rank2 = (i + 1 < n) ? (int) text.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes);

        // Map to quickly fetch index positions
        int[] ind = new int[n];

        // Doubling phases: k = 4, 8, 16 ...
        for (int k = 4; k < 2 * n; k *= 2) {
            // Assign new rank1 values based on previous sort order
            int rank = 0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank1 = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank1 && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = rank;
                } else {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            // Assign rank2 for the second half of length k/2
            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank1 : -1;
            }

            // Sort suffixes based on new rank pairs
            Arrays.sort(suffixes);
        }

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = suffixes[i].index;
        }
        return sa;
    }

    /**
     * Searches for occurrences of a pattern in the document using binary search over the suffix array.
     *
     * @param text    original document
     * @param sa      suffix array for the document
     * @param pattern search query
     * @return sorted list of 0-based character starting positions in ascending order
     */
    public static List<Integer> search(String text, int[] sa, String pattern) {
        if (text == null || sa == null || pattern == null || pattern.isEmpty() || sa.length == 0) {
            return Collections.emptyList();
        }

        int n = text.length();
        int m = pattern.length();

        // 1. Binary search for lower bound: smallest index in SA where suffix >= pattern
        int low = 0;
        int high = n - 1;
        int firstOccur = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);

            if (cmp >= 0) {
                if (cmp == 0) {
                    firstOccur = mid;
                }
                high = mid - 1; // Try to find earlier occurrence in SA
            } else {
                low = mid + 1;
            }
        }

        if (firstOccur == -1) {
            return Collections.emptyList(); // Pattern not present
        }

        // 2. Binary search for upper bound: largest index in SA where suffix starts with pattern
        low = firstOccur;
        high = n - 1;
        int lastOccur = firstOccur;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);

            if (cmp == 0) {
                lastOccur = mid;
                low = mid + 1; // Try to find later occurrence in SA
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        // Collect all matching character starting positions
        List<Integer> positions = new ArrayList<>();
        for (int i = firstOccur; i <= lastOccur; i++) {
            positions.add(sa[i]);
        }

        // Sort positions ascending for readable document order
        Collections.sort(positions);
        return positions;
    }

    /**
     * Compares text[suffixStart .. min(suffixStart+m, n)] with pattern.
     * Returns:
     *   0 if suffix starts with pattern
     *  <0 if suffix prefix is lexicographically smaller than pattern
     *  >0 if suffix prefix is lexicographically greater than pattern
     */
    private static int comparePrefix(String text, int suffixStart, String pattern) {
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i < m; i++) {
            if (suffixStart + i >= n) {
                return -1; // Suffix ended earlier than pattern length
            }
            char c1 = text.charAt(suffixStart + i);
            char c2 = pattern.charAt(i);
            if (c1 != c2) {
                return Character.compare(c1, c2);
            }
        }
        return 0; // Exactly matches pattern prefix
    }

    /**
     * Case-insensitive search helper that builds or utilizes lowercase text.
     */
    public static List<Integer> searchIgnoreCase(String text, int[] saCaseInsensitive, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty()) {
            return Collections.emptyList();
        }
        return search(text.toLowerCase(), saCaseInsensitive, pattern.toLowerCase());
    }

    /**
     * Computes human-readable metadata distinguishing character index from word position and line number.
     */
    public static MatchLocation computeLocation(String text, int charIndex) {
        if (text == null || charIndex < 0 || charIndex >= text.length()) {
            return new MatchLocation(charIndex, -1, -1);
        }

        int lineNumber = 1;
        int wordCount = 1;
        boolean inWord = false;

        for (int i = 0; i < charIndex; i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                lineNumber++;
            }
            if (Character.isWhitespace(c)) {
                inWord = false;
            } else {
                if (!inWord) {
                    wordCount++;
                    inWord = true;
                }
            }
        }
        return new MatchLocation(charIndex, wordCount, lineNumber);
    }

    public static class MatchLocation {
        public final int characterIndex;
        public final int wordNumber;
        public final int lineNumber;

        public MatchLocation(int characterIndex, int wordNumber, int lineNumber) {
            this.characterIndex = characterIndex;
            this.wordNumber = wordNumber;
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return String.format("Character Offset: %d | Word #: ~%d | Line #: %d",
                    characterIndex, wordNumber, lineNumber);
        }
    }

    /**
     * Provides an educational step-by-step trace using the classic "banana" example.
     */
    public static String getEducationalExample() {
        String s = "banana";
        int[] sa = buildSuffixArray(s);
        StringBuilder sb = new StringBuilder();
        sb.append("--- SUFFIX ARRAY DEMONSTRATION ON 'banana' ---\n");
        sb.append("Original String: 'banana' (Length = 6)\n\n");
        sb.append(String.format("%-6s | %-12s | %s\n", "SA[i]", "Start Index", "Suffix String"));
        sb.append("------------------------------------------\n");
        for (int i = 0; i < sa.length; i++) {
            sb.append(String.format("SA[%d]  | %-12d | %s\n", i, sa[i], s.substring(sa[i])));
        }
        sb.append("\nQuery: Binary searching for 'an' over SA finds range [1..2]:\n");
        sb.append("  -> SA[1] = 3 ('ana...')  [Char position 3]\n");
        sb.append("  -> SA[2] = 1 ('anana...') [Char position 1]\n");
        sb.append("Sorting occurrences in original text order -> Positions [1, 3].\n");
        return sb.toString();
    }
}
