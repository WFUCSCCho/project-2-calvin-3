/*∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*
  @file: TimingUtlis.java
  @description: This file stores utility methods used in the Proj2 file
  @author: Calvin Malaney
  @date: October 21, 2025
∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TimingUtils {
    private TimingUtils() {}// no instances

    /** Converts nanoseconds to seconds as a double. */
    public static double nsToSec(long ns) { return ns / 1_000_000_000.0; }

    /** Returns seconds per node given an elapsed time and item count (guards n≥1). */
    public static double ratePerNode(long ns, int n) { return nsToSec(ns) / Math.max(1, n); }

    /** Converts nanoseconds to milliseconds as a double. */
    public static double nsToMs(long ns) { return ns / 1_000_000.0; }

    /** Times inserting all items in order into a BST using its add method. */
    public static <T extends Comparable<? super T>>
    long timeInsertBST(BST<T> bst, List<T> data) {
        long start = System.nanoTime();
        for (T x : data) bst.add(x);
        return System.nanoTime() - start;
    }

    /** Times inserting all items in order into an AVL tree using insert. */
    public static <T extends Comparable<? super T>>
    long timeInsertAVL(AvLTree<T> avl, List<T> data) {
        long start = System.nanoTime();
        for (T x : data) avl.insert(x);
        return System.nanoTime() - start;
    }

    /** Times searching each query in a BST; ignores the boolean result. */
    public static <T extends Comparable<? super T>>
    long timeSearchBST(BST<T> bst, List<T> queries) {
        long start = System.nanoTime();
        for (T q : queries) bst.search(q);   // ignore return; just timing
        return System.nanoTime() - start;
    }

    /** Times searching each query in an AVL tree; ignores the boolean result. */
    public static <T extends Comparable<? super T>>
    long timeSearchAVL(AvLTree<T> avl, List<T> queries) {
        long start = System.nanoTime();
        for (T q : queries) avl.contains(q); // ignore return; just timing
        return System.nanoTime() - start;
    }

    /** Appends a CSV header (once) and a data row to the given file path. */
    public static void appendCsv(String path, String header, String row) {
        File f = new File(path);
        boolean writeHeader = !(f.exists() && f.length() > 0);
        try (FileWriter fw = new FileWriter(f, true);
             PrintWriter pw = new PrintWriter(fw)) {
            if (writeHeader) pw.println(header);
            pw.println(row);
        } catch (IOException e) {
            System.err.println("Failed to write timings to " + path + ": " + e.getMessage());
        }
    }
}


