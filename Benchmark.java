import java.util.Random;

/**
 * Builds random connected networks and times Fibonacci-heap vs binary-heap Dijkstra
 * while link costs keep changing (simulated congestion).
 *
 * Contributor: Member 4
 */
public class Benchmark {

    /** Random connected graph: a random spanning tree plus extra random links up to m. */
    public static Graph randomGraph(int n, int m, int maxWeight, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph();
        for (int i = 0; i < n; i++) g.addNode("N" + i);
        for (int i = 1; i < n; i++) {
            g.addLink("N" + rnd.nextInt(i), "N" + i, 1 + rnd.nextInt(maxWeight));
        }
        int attempts = 0;
        while (g.linkCount() < m && attempts < m * 20) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            attempts++;
            if (a != b) g.addLink("N" + a, "N" + b, 1 + rnd.nextInt(maxWeight));
        }
        return g;
    }

    public static void run(int n, int m, int rounds, int updatesPerRound) {
        if (n < 2) throw new IllegalArgumentException("Need at least 2 nodes");
        Graph g = randomGraph(n, m, 100, 42);
        Random rnd = new Random(7);

        for (int i = 0; i < 5; i++) {            // JIT warm-up, not timed
            Dijkstra.runFibonacci(g, 0, -1);
            Dijkstra.runBinary(g, 0, -1);
        }

        long fibNs = 0, binNs = 0, fibDecKeys = 0, binReinserts = 0;
        int mismatches = 0;
        for (int r = 0; r < rounds; r++) {
            g.simulateCongestion(rnd, updatesPerRound, 100);
            int src = rnd.nextInt(n);

            PathResult f = Dijkstra.runFibonacci(g, src, -1);   // full shortest-path tree
            PathResult b = Dijkstra.runBinary(g, src, -1);
            fibNs += f.nanos;
            binNs += b.nanos;
            fibDecKeys += f.decreaseKeys;
            binReinserts += b.decreaseKeys;

            int dst = rnd.nextInt(n);                           // correctness check (not timed)
            PathResult pf = Dijkstra.runFibonacci(g, src, dst);
            PathResult pb = Dijkstra.runBinary(g, src, dst);
            if (Double.compare(pf.cost, pb.cost) != 0) mismatches++;
        }

        System.out.printf("Network: %d nodes, %d links | %d rounds, %d congestion updates per round%n",
                g.size(), g.linkCount(), rounds, updatesPerRound);
        System.out.printf("Fibonacci heap : avg %.3f ms per run, %d decrease-keys total%n",
                fibNs / 1e6 / rounds, fibDecKeys);
        System.out.printf("Binary heap    : avg %.3f ms per run, %d re-insertions total%n",
                binNs / 1e6 / rounds, binReinserts);
        System.out.printf("Speed ratio (binary / Fibonacci): %.2f%n", (double) binNs / fibNs);
        System.out.println("Route-cost mismatches between the two versions: " + mismatches);
        System.out.println("Note: results depend on graph size and density; report the numbers you measure.");
    }
}
