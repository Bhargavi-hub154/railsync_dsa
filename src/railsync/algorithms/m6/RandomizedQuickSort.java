package railsync.algorithms.m6;

import railsync.model.Train;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * ============================================================================
 * MODULE 6: RANDOMIZED ALGORITHMS — RANDOMIZED QUICKSORT
 * ============================================================================
 * Problem Solved:
 * Sorts train operational records (by delay minutes, passenger volume, or
 * train ID) to prioritize dispatching, passenger crowd control, or timetable
 * audits without risk of worst-case execution stalls on pre-sorted lists.
 *
 * Why Random Pivot Selection?
 * - Standard deterministic QuickSort (e.g., choosing first or last element)
 *   suffers from catastrophic worst-case O(n^2) performance if the input is
 *   already sorted, reverse-sorted, or has many identical elements.
 * - Railway timetables and records are frequently partially or fully ordered.
 * - By selecting the partition pivot uniformly at random from [low..high],
 *   the probability of encountering unbalanced partitions is negligible.
 * - The expected time complexity is guaranteed to be O(n log n) regardless
 *   of the initial input ordering.
 *
 * Time Complexity:
 * - Expected Time: O(n log n)
 * - Worst-Case Time: O(n^2) (occurs with astronomically small probability)
 * - Best-Case Time: O(n log n)
 *
 * Space Complexity:
 * - O(log n) auxiliary stack space for recursion.
 * ============================================================================
 */
public class RandomizedQuickSort {

    private static final Random RANDOM = new Random();

    // Standard comparators for railway train records
    public static final Comparator<Train> BY_DELAY_DESC =
            (t1, t2) -> Integer.compare(t2.getDelayMinutes(), t1.getDelayMinutes());

    public static final Comparator<Train> BY_DELAY_ASC =
            (t1, t2) -> Integer.compare(t1.getDelayMinutes(), t2.getDelayMinutes());

    public static final Comparator<Train> BY_PASSENGERS_DESC =
            (t1, t2) -> Integer.compare(t2.getPassengerCount(), t1.getPassengerCount());

    public static final Comparator<Train> BY_TRAIN_ID_ASC =
            (t1, t2) -> Integer.compare(t1.getTrainId(), t2.getTrainId());

    /**
     * Sorts a List in-place using Randomized QuickSort.
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        quickSortList(list, 0, list.size() - 1, comparator, RANDOM);
    }

    /**
     * Sorts a primitive integer array in-place using Randomized QuickSort.
     */
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortInt(arr, 0, arr.length - 1, RANDOM);
    }

    /**
     * Recursive quicksort on a List with random pivot selection.
     */
    private static <T> void quickSortList(List<T> list, int low, int high, Comparator<T> comp, Random rng) {
        if (low < high) {
            int pIndex = partitionList(list, low, high, comp, rng);
            quickSortList(list, low, pIndex - 1, comp, rng);
            quickSortList(list, pIndex + 1, high, comp, rng);
        }
    }

    private static <T> int partitionList(List<T> list, int low, int high, Comparator<T> comp, Random rng) {
        // Choose a random pivot index in [low, high]
        int randomPivotIdx = low + rng.nextInt(high - low + 1);
        swapList(list, randomPivotIdx, high);

        T pivot = list.get(high);
        int i = low; // boundary of smaller elements

        for (int j = low; j < high; j++) {
            if (comp.compare(list.get(j), pivot) <= 0) {
                swapList(list, i, j);
                i++;
            }
        }
        swapList(list, i, high);
        return i;
    }

    private static <T> void swapList(List<T> list, int i, int j) {
        if (i != j) {
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    /**
     * Recursive quicksort for integer arrays with random pivot.
     */
    private static void quickSortInt(int[] arr, int low, int high, Random rng) {
        if (low < high) {
            int pIndex = partitionInt(arr, low, high, rng);
            quickSortInt(arr, low, pIndex - 1, rng);
            quickSortInt(arr, pIndex + 1, high, rng);
        }
    }

    private static int partitionInt(int[] arr, int low, int high, Random rng) {
        int randomPivotIdx = low + rng.nextInt(high - low + 1);
        swapInt(arr, randomPivotIdx, high);

        int pivot = arr[high];
        int i = low;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                swapInt(arr, i, j);
                i++;
            }
        }
        swapInt(arr, i, high);
        return i;
    }

    private static void swapInt(int[] arr, int i, int j) {
        if (i != j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    /**
     * Returns a copy of the list sorted by the specified comparator.
     */
    public static <T> List<T> sortedCopy(List<T> list, Comparator<T> comparator) {
        if (list == null) return new ArrayList<>();
        List<T> copy = new ArrayList<>(list);
        sort(copy, comparator);
        return copy;
    }
}
