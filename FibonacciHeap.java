import java.util.ArrayList;
import java.util.List;

/**
 * Fibonacci Heap (min-heap). Each node stores an int vertex id and a double key.
 * Roots and siblings are kept in circular doubly linked lists.
 *
 */
public class FibonacciHeap {

    /** One node of the heap. Handles to these are kept so decrease-key is O(1). */
    public static class FibNode {
        public final int vertex;
        public double key;
        int degree;
        boolean marked;
        FibNode parent, child, left, right;

        FibNode(int vertex, double key) {
            this.vertex = vertex;
            this.key = key;
            this.left = this;
            this.right = this;
        }
    }

    private FibNode min;
    private int size;

    public boolean isEmpty() { return min == null; }

    public int size() { return size; }

    /** O(1): add a new single-node tree to the root list. */
    public FibNode insert(int vertex, double key) {
        FibNode node = new FibNode(vertex, key);
        addToRootList(node);
        size++;
        return node;
    }

    /** O(log n) amortized: remove and return the minimum node (null if empty). */
    public FibNode extractMin() {
        FibNode z = min;
        if (z == null) return null;

        // collect children first, because re-adding them rewrites their links
        List<FibNode> children = new ArrayList<>();
        if (z.child != null) {
            FibNode c = z.child;
            do {
                children.add(c);
                c = c.right;
            } while (c != z.child);
        }

        // remove z from the root list
        if (z.right == z) {
            min = null;
        } else {
            z.left.right = z.right;
            z.right.left = z.left;
            min = z.right;
        }
        z.child = null;
        z.left = z;
        z.right = z;

        // promote z's children to the root list
        for (FibNode ch : children) addToRootList(ch);

        size--;
        if (min != null) consolidate();
        return z;
    }

    /** O(1) amortized: lower a node's key, cutting it out of its tree if needed. */
    public void decreaseKey(FibNode x, double newKey) {
        if (newKey > x.key) {
            throw new IllegalArgumentException("New key is greater than current key");
        }
        x.key = newKey;
        FibNode y = x.parent;
        if (y != null && x.key < y.key) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x.key < min.key) min = x;
    }

    // ---- internal helpers ----

    private void addToRootList(FibNode node) {
        node.parent = null;
        node.marked = false;
        if (min == null) {
            node.left = node;
            node.right = node;
            min = node;
        } else {
            node.left = min;
            node.right = min.right;
            min.right.left = node;
            min.right = node;
            if (node.key < min.key) min = node;
        }
    }

    /** Merge roots of equal degree until every root has a different degree. */
    private void consolidate() {
        List<FibNode> roots = new ArrayList<>();
        FibNode cur = min;
        do {
            roots.add(cur);
            cur = cur.right;
        } while (cur != min);

        List<FibNode> byDegree = new ArrayList<>();   // index = degree
        for (FibNode w : roots) {
            FibNode x = w;
            int d = x.degree;
            while (true) {
                while (byDegree.size() <= d) byDegree.add(null);
                FibNode y = byDegree.get(d);
                if (y == null) break;
                if (y.key < x.key) {
                    FibNode tmp = x;
                    x = y;
                    y = tmp;
                }
                link(y, x);          // y becomes a child of x
                byDegree.set(d, null);
                d++;
            }
            byDegree.set(d, x);
        }

        // rebuild the root list and the min pointer
        min = null;
        for (FibNode n : byDegree) {
            if (n != null) addToRootList(n);
        }
    }

    private void link(FibNode y, FibNode x) {
        y.parent = x;
        y.marked = false;
        if (x.child == null) {
            x.child = y;
            y.left = y;
            y.right = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right.left = y;
            x.child.right = y;
        }
        x.degree++;
    }

    private void cut(FibNode x, FibNode y) {
        if (x.right == x) {
            y.child = null;
        } else {
            x.left.right = x.right;
            x.right.left = x.left;
            if (y.child == x) y.child = x.right;
        }
        y.degree--;
        addToRootList(x);
    }

    private void cascadingCut(FibNode y) {
        FibNode z = y.parent;
        if (z != null) {
            if (!y.marked) {
                y.marked = true;
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }
}
