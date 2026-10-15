package railsync.algorithms.m1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * MODULE 1: STRING ALGORITHMS — KNUTH-MORRIS-PRATT (KMP)
 * ============================================================================
 * Problem Solved:
 * Efficiently search for keyword patterns (train names, station names, delay
 * reasons, or alert keywords) across extensive railway records without
 * redundant backtracking.
 *
 * How it Works:
 * 1. Preprocesses the pattern string into a Longest Prefix Suffix (LPS) array.
 *    LPS[i] stores the length of the longest proper prefix of pattern[0..i]
 *    that is also a suffix of pattern[0..i].
 * 2. When a mismatch occurs at character pattern[j] against text[i], the LPS
 *    array tells us how many characters in the pattern we can safely skip,
 *    avoiding re-examining matched characters in the text.
 *
 * Time Complexity:
 * - Preprocessing (LPS construction): O(m) where m is pattern length.
 * - Search Phase: O(n) where n is text length.
 * - Total Time: O(n + m). Compare this with Naive search: O(n * m) worst case.
 *
 * Space Complexity:
 * - O(m) auxiliary space for the LPS array.
 * ============================================================================
 */
public class KMP {

    /**
     * Constructs the Longest Prefix Suffix (LPS) array for pattern P of length m.
     *
     * @param pattern pattern string to preprocess
     * @return an integer array of size m containing prefix-suffix lengths
     */
    public static int[] computeLPS(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }

        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of the previous longest prefix suffix
        int i = 1;

        lps[0] = 0; // lps[0] is always 0

        // Loop calculates lps[i] for i = 1 to m-1
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                // Mismatch after len matches
                if (len != 0) {
                    // Do not increment i; fall back to previous known prefix length
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for all occurrences of pattern in text using KMP (exact match).
     *
     * @param text    the source text to search within
     * @param pattern the query pattern to find
     * @return a List of 0-based starting indices where pattern matches text
     */
    public static List<Integer> search(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return Collections.emptyList();
        }

        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                // Pattern found at index (i - j)
                matches.add(i - j);
                // Shift pattern according to LPS
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                // Mismatch after j matches
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Searches for pattern in text with case-insensitivity (normalized comparison).
     */
    public static List<Integer> searchIgnoreCase(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty()) {
            return Collections.emptyList();
        }
        return search(text.toLowerCase(), pattern.toLowerCase());
    }

    /**
     * Returns true if text contains pattern (case-insensitive).
     */
    public static boolean containsIgnoreCase(String text, String pattern) {
        return !searchIgnoreCase(text, pattern).isEmpty();
    }
}
