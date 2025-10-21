/*∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*
  @file: Proj2.java
  @description: Compares insertion and search performance of BST and AVL trees using FIFA record data.
  @author: Dr. Samuel Cho, Calvin Malaney
  @date: October 21, 2025
∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;

public class Proj2 {

    // for better CSV formatting - avoids scientific notation
    private static String fmt(double x) {
        return String.format(Locale.US, "%.8f", x);
    }

    public static void main(String[] args) throws IOException {
        // Use command line arguments to specify the input file
        if (args.length != 2) {
            System.err.println("Usage: java TestAvl <input file> <number of lines>");
            System.exit(1);
        }

        String inputFileName = args[0];
        int numLines = Integer.parseInt(args[1]);

        // make sorted and randomized copies
        List<FIFARecord> original = Parser.loadCsv(inputFileName, numLines);

        // build sorted copy
        List<FIFARecord> sorted = new ArrayList<>(original);
        Collections.sort(sorted);

        // build random copy
        List<FIFARecord> randomized = new ArrayList<>(original);
        Collections.shuffle(randomized);

        int N = original.size();

        // build trees
        BST<FIFARecord> bstSorted = new BST<>();
        BST<FIFARecord> bstRandom = new BST<>();
        AvLTree<FIFARecord> avlSorted = new AvLTree<>();
        AvLTree<FIFARecord> avlRandom = new AvLTree<>();

        // timing - inserts
        long bstSortedInsertNs = TimingUtils.timeInsertBST(bstSorted,   sorted);
        long bstRandomInsertNs = TimingUtils.timeInsertBST(bstRandom,   randomized);
        long avlSortedInsertNs = TimingUtils.timeInsertAVL(avlSorted,   sorted);
        long avlRandomInsertNs = TimingUtils.timeInsertAVL(avlRandom,   randomized);

        // timing - search (using original order)
        long bstSortedSearchNs = TimingUtils.timeSearchBST(bstSorted,   original);
        long bstRandomSearchNs = TimingUtils.timeSearchBST(bstRandom,   original);
        long avlSortedSearchNs = TimingUtils.timeSearchAVL(avlSorted,   original);
        long avlRandomSearchNs = TimingUtils.timeSearchAVL(avlRandom,   original);

        // print
        System.out.println();
        System.out.println("=== Timing Results ===");
        System.out.printf("Lines: %,d%n", N);
        System.out.printf("BST  (sorted)   insert: %8.3f ms   search: %8.3f ms%n",
                TimingUtils.nsToMs(bstSortedInsertNs), TimingUtils.nsToMs(bstSortedSearchNs));
        System.out.printf("BST  (random)   insert: %8.3f ms   search: %8.3f ms%n",
                TimingUtils.nsToMs(bstRandomInsertNs), TimingUtils.nsToMs(bstRandomSearchNs));
        System.out.printf("AVL  (sorted)   insert: %8.3f ms   search: %8.3f ms%n",
                TimingUtils.nsToMs(avlSortedInsertNs), TimingUtils.nsToMs(avlSortedSearchNs));
        System.out.printf("AVL  (random)   insert: %8.3f ms   search: %8.3f ms%n",
                TimingUtils.nsToMs(avlRandomInsertNs), TimingUtils.nsToMs(avlRandomSearchNs));

        // csv with the 4 series
        String header = String.join(",",
                "dataset","lines","run_at",
                "bst_sorted_insert_s","avl_sorted_insert_s",
                "bst_sorted_search_s","avl_sorted_search_s",
                "bst_sorted_insert_spn","avl_sorted_insert_spn",
                "bst_sorted_search_spn","avl_sorted_search_spn"
        );

        String row = String.join(",",
                String.valueOf(N),
                // seconds
                fmt(TimingUtils.nsToSec(bstSortedInsertNs)),
                fmt(TimingUtils.nsToSec(avlSortedInsertNs)),
                fmt(TimingUtils.nsToSec(bstSortedSearchNs)),
                fmt(TimingUtils.nsToSec(avlSortedSearchNs)),
                // seconds per node
                fmt(TimingUtils.ratePerNode(bstSortedInsertNs, N)),
                fmt(TimingUtils.ratePerNode(avlSortedInsertNs, N)),
                fmt(TimingUtils.ratePerNode(bstSortedSearchNs, N)),
                fmt(TimingUtils.ratePerNode(avlSortedSearchNs, N))
        );

        TimingUtils.appendCsv("output.txt", header, row);
    }
}

