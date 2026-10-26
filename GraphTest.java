/** Tests for Graph. */
public class GraphTest {
    static int failed = 0;

    static void check(boolean ok, String msg) {
        if (!ok) { failed++; System.out.println("FAIL: " + msg); }
        else System.out.println("pass: " + msg);
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.addLink("A", "B", 4);
        check(g.size() == 2 && g.linkCount() == 1, "addLink creates both nodes and one link");
        check(g.neighbors(g.getId("A")).get(0).weight == 4
                && g.neighbors(g.getId("B")).get(0).weight == 4, "links are undirected");

        g.addLink("b", "a", 7);
        check(g.linkCount() == 1 && g.neighbors(g.getId("A")).get(0).weight == 7, "duplicate link updates cost (case-insensitive)");

        check(g.updateLink("A", "B", 2) && g.neighbors(g.getId("B")).get(0).weight == 2, "updateLink changes both directions");
        check(!g.updateLink("A", "Z", 1), "updateLink on unknown node returns false");

        boolean threw = false;
        try { g.addLink("A", "C", -1); } catch (IllegalArgumentException e) { threw = true; }
        check(threw && g.size() == 2, "negative cost rejected without creating nodes");

        threw = false;
        try { g.addLink("A", "a", 1); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "self-link rejected");

        threw = false;
        try { g.getId("Q"); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "unknown node lookup throws");

        System.exit(failed == 0 ? 0 : 1);
    }
}
