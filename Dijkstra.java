import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra's algorithm in two versions: Fibonacci Heap (main) and binary heap (baseline).
 * Pass dst = -1 to compute the whole shortest-path tree (used by the benchmark).
 *
 */
public class Dijkstra {
    private static final double INF = Double.POSITIVE_INFINITY;

    /** Fibonacci-heap version: uses decrease-key on a handle stored per vertex. */
    public static PathResult runFibonacci(Graph g, int src, int dst) {
        long start = System.nanoTime();
        int n = g.size();
        double[] dist = new double[n];
        Arrays.fill(dist, INF);
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        boolean[] done = new boolean[n];
        FibonacciHeap.FibNode[] handle = new FibonacciHeap.FibNode[n];

        dist[src] = 0;
        FibonacciHeap heap = new FibonacciHeap();
        for (int v = 0; v < n; v++) handle[v] = heap.insert(v, dist[v]);

        long extracts = 0, decKeys = 0;
        while (!heap.isEmpty()) {
            FibonacciHeap.FibNode node = heap.extractMin();
            if (node.key == INF) break;          // everything left is unreachable
            int u = node.vertex;
            done[u] = true;
            extracts++;
            if (u == dst) break;
            for (Edge e : g.neighbors(u)) {
                if (done[e.to]) continue;
                double nd = dist[u] + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    prev[e.to] = u;
                    heap.decreaseKey(handle[e.to], nd);
                    decKeys++;
                }
            }
        }
        return build(g, dst, dist, prev, extracts, decKeys, System.nanoTime() - start);
    }

    /** Binary-heap version: decrease-key is simulated by re-inserting and skipping stale entries. */
    public static PathResult runBinary(Graph g, int src, int dst) {
        long start = System.nanoTime();
        int n = g.size();
        double[] dist = new double[n];
        Arrays.fill(dist, INF);
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        boolean[] done = new boolean[n];

        PriorityQueue<double[]> pq = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
        dist[src] = 0;
        pq.add(new double[]{0, src});

        long extracts = 0, reinserts = 0;
        while (!pq.isEmpty()) {
            double[] top = pq.poll();
            int u = (int) top[1];
            if (done[u]) continue;               // stale entry
            done[u] = true;
            extracts++;
            if (u == dst) break;
            for (Edge e : g.neighbors(u)) {
                if (done[e.to]) continue;
                double nd = dist[u] + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    prev[e.to] = u;
                    pq.add(new double[]{nd, e.to});
                    reinserts++;
                }
            }
        }
        return build(g, dst, dist, prev, extracts, reinserts, System.nanoTime() - start);
    }

    /** Rebuilds the route by following predecessors back from dst, using a stack to reverse it. */
    private static PathResult build(Graph g, int dst, double[] dist, int[] prev,
                                    long extracts, long decKeys, long nanos) {
        if (dst < 0) {
            return new PathResult(true, Collections.<String>emptyList(), 0, extracts, decKeys, nanos);
        }
        if (dist[dst] == INF) {
            return new PathResult(false, Collections.<String>emptyList(), INF, extracts, decKeys, nanos);
        }
        Deque<Integer> stack = new ArrayDeque<>();
        for (int at = dst; at != -1; at = prev[at]) stack.push(at);
        List<String> path = new ArrayList<>();
        while (!stack.isEmpty()) path.add(g.getName(stack.pop()));
        return new PathResult(true, path, dist[dst], extracts, decKeys, nanos);
    }
}
