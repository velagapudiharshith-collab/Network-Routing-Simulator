import java.util.Arrays;
import java.util.Random;

/** Tests for Dijkstra (both versions). Contributor: Member 3 */
public class DijkstraTest {
    static int failed = 0;

    static void check(boolean ok, String msg) {
        if (!ok) { failed++; System.out.println("FAIL: " + msg); }
        else System.out.println("pass: " + msg);
    }

    static Graph sample() {
        Graph g = new Graph();
        g.addLink("A", "B", 4);
        g.addLink("B", "E", 3);
        g.addLink("A", "C", 2);
        g.addLink("C", "E", 9);
        g.addNode("F");                      // isolated node
        return g;
    }

    public static void main(String[] args) {
        Graph g = sample();
        int a = g.getId("A"), e = g.getId("E"), f = g.getId("F");

        PathResult fib = Dijkstra.runFibonacci(g, a, e);
        PathResult bin = Dijkstra.runBinary(g, a, e);
        check(fib.cost == 7 && fib.path.equals(Arrays.asList("A", "B", "E")), "Fibonacci: A to E costs 7 via B");
        check(bin.cost == 7 && bin.path.equals(Arrays.asList("A", "B", "E")), "Binary: A to E costs 7 via B");

        PathResult same = Dijkstra.runFibonacci(g, a, a);
        check(same.reachable && same.cost == 0 && same.path.size() == 1, "source equals destination costs 0");

        check(!Dijkstra.runFibonacci(g, a, f).reachable && !Dijkstra.runBinary(g, a, f).reachable,
                "isolated node is unreachable");

        Graph t = new Graph();
        t.addLink("A", "B", 1);
        t.addLink("A", "C", 1);
        t.addLink("B", "D", 1);
        t.addLink("C", "D", 1);
        check(Dijkstra.runFibonacci(t, t.getId("A"), t.getId("D")).cost == 2, "equal-weight ties still give cost 2");

        g.updateLink("C", "E", 1);
        check(Dijkstra.runFibonacci(g, a, e).cost == 3, "route re-computed after a link cost update");

        // cross-check both versions against Floyd-Warshall on random graphs (zero weights included)
        Random rnd = new Random(5);
        boolean allOk = true;
        for (int round = 0; round < 200 && allOk; round++) {
            int n = 2 + rnd.nextInt(12);
            Graph r = new Graph();
            for (int i = 0; i < n; i++) r.addNode("V" + i);
            int links = rnd.nextInt(n * 2);
            for (int k = 0; k < links; k++) {
                int x = rnd.nextInt(n), y = rnd.nextInt(n);
                if (x != y) r.addLink("V" + x, "V" + y, rnd.nextInt(10));
            }
            double[][] fw = new double[n][n];
            for (double[] row : fw) Arrays.fill(row, Double.POSITIVE_INFINITY);
            for (int i = 0; i < n; i++) fw[i][i] = 0;
            for (int u = 0; u < n; u++) for (Edge ed : r.neighbors(u)) fw[u][ed.to] = Math.min(fw[u][ed.to], ed.weight);
            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        if (fw[i][k] + fw[k][j] < fw[i][j]) fw[i][j] = fw[i][k] + fw[k][j];
            for (int s = 0; s < n && allOk; s++) {
                for (int d = 0; d < n; d++) {
                    PathResult pf = Dijkstra.runFibonacci(r, s, d);
                    PathResult pb = Dijkstra.runBinary(r, s, d);
                    boolean reach = fw[s][d] != Double.POSITIVE_INFINITY;
                    if (pf.reachable != reach || pb.reachable != reach
                            || (reach && (pf.cost != fw[s][d] || pb.cost != fw[s][d]))) {
                        allOk = false;
                        break;
                    }
                }
            }
        }
        check(allOk, "both versions match Floyd-Warshall on 200 random graphs");

        System.exit(failed == 0 ? 0 : 1);
    }
}
