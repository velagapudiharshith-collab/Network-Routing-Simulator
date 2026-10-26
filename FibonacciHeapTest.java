import java.util.Arrays;
import java.util.Random;

/** Tests for FibonacciHeap. */
public class FibonacciHeapTest {
    static int failed = 0;

    static void check(boolean ok, String msg) {
        if (!ok) { failed++; System.out.println("FAIL: " + msg); }
        else System.out.println("pass: " + msg);
    }

    public static void main(String[] args) {
        Random rnd = new Random(1);

        FibonacciHeap h = new FibonacciHeap();
        check(h.isEmpty() && h.extractMin() == null, "empty heap returns null");

        int n = 1000;
        double[] keys = new double[n];
        for (int i = 0; i < n; i++) {
            keys[i] = rnd.nextInt(10000);
            h.insert(i, keys[i]);
        }
        double[] sorted = keys.clone();
        Arrays.sort(sorted);
        boolean ok = true;
        for (int i = 0; i < n; i++) {
            if (h.extractMin().key != sorted[i]) ok = false;
        }
        check(ok && h.isEmpty(), "extractMin returns keys in sorted order");

        FibonacciHeap h2 = new FibonacciHeap();
        FibonacciHeap.FibNode[] nodes = new FibonacciHeap.FibNode[100];
        for (int i = 0; i < 100; i++) nodes[i] = h2.insert(i, 100 + i);
        h2.extractMin();                       // forces consolidation so trees form
        h2.decreaseKey(nodes[99], 1);
        check(h2.extractMin().vertex == 99, "decreaseKey moves a deep node to the front");

        boolean threw = false;
        try { h2.decreaseKey(nodes[50], 9999); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "decreaseKey rejects a larger key");

        int m = 500;
        FibonacciHeap h3 = new FibonacciHeap();
        double[] ref = new double[m];
        boolean[] in = new boolean[m];
        FibonacciHeap.FibNode[] hn = new FibonacciHeap.FibNode[m];
        for (int i = 0; i < m; i++) {
            ref[i] = 1000 + rnd.nextInt(1000);
            hn[i] = h3.insert(i, ref[i]);
            in[i] = true;
        }
        boolean stressOk = true;
        for (int step = 0; step < m * 3 && !h3.isEmpty(); step++) {
            if (rnd.nextBoolean()) {
                int i = rnd.nextInt(m);
                if (in[i] && ref[i] > 1) {
                    double nk = rnd.nextInt((int) ref[i]);
                    h3.decreaseKey(hn[i], nk);
                    ref[i] = nk;
                }
            } else {
                double expected = Double.POSITIVE_INFINITY;
                for (int i = 0; i < m; i++) if (in[i] && ref[i] < expected) expected = ref[i];
                FibonacciHeap.FibNode got = h3.extractMin();
                if (got.key != expected) stressOk = false;
                in[got.vertex] = false;
            }
        }
        check(stressOk, "random insert / decreaseKey / extractMin matches a reference");

        System.exit(failed == 0 ? 0 : 1);
    }
}
