import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Undirected weighted network stored as an adjacency list.
 * Node names are case-insensitive (stored upper-case) and mapped to int ids with a HashMap.
 *
 * Contributor: Member 2
 */
public class Graph {
    private final List<List<Edge>> adj = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private final Map<String, Integer> idOf = new HashMap<>();
    private int linkCount = 0;

    private static String norm(String name) {
        return name.trim().toUpperCase();
    }

    /** Adds a node (or returns the existing id if the name is already present). */
    public int addNode(String name) {
        String key = norm(name);
        if (key.isEmpty()) throw new IllegalArgumentException("Node name cannot be empty");
        Integer existing = idOf.get(key);
        if (existing != null) return existing;
        int id = names.size();
        names.add(key);
        idOf.put(key, id);
        adj.add(new ArrayList<>());
        return id;
    }

    public int size() { return names.size(); }

    public int linkCount() { return linkCount; }

    public boolean hasNode(String name) { return idOf.containsKey(norm(name)); }

    public int getId(String name) {
        Integer id = idOf.get(norm(name));
        if (id == null) throw new IllegalArgumentException("Unknown node: " + name.trim());
        return id;
    }

    public String getName(int id) { return names.get(id); }

    public List<Edge> neighbors(int id) { return adj.get(id); }

    /** Adds an undirected link; if it already exists its cost is updated. */
    public void addLink(String a, String b, double weight) {
        validateWeight(weight);
        if (norm(a).equals(norm(b))) throw new IllegalArgumentException("Self-links are not allowed");
        int u = addNode(a);
        int v = addNode(b);
        Edge existing = find(u, v);
        if (existing != null) {
            existing.weight = weight;
            find(v, u).weight = weight;
            return;
        }
        adj.get(u).add(new Edge(v, weight));
        adj.get(v).add(new Edge(u, weight));
        linkCount++;
    }

    /** Changes the cost of an existing link. Returns false if the link does not exist. */
    public boolean updateLink(String a, String b, double weight) {
        validateWeight(weight);
        if (!hasNode(a) || !hasNode(b)) return false;
        int u = getId(a);
        int v = getId(b);
        Edge e1 = find(u, v);
        Edge e2 = find(v, u);
        if (e1 == null || e2 == null) return false;
        e1.weight = weight;
        e2.weight = weight;
        return true;
    }

    /** Randomly re-weights up to 'count' links (simulated traffic). Returns how many changed. */
    public int simulateCongestion(Random rnd, int count, int maxWeight) {
        if (linkCount == 0 || maxWeight < 1) return 0;
        int changed = 0;
        for (int i = 0; i < count; i++) {
            int u = rnd.nextInt(size());
            int tries = 0;
            while (adj.get(u).isEmpty() && tries++ < size()) u = (u + 1) % size();
            if (adj.get(u).isEmpty()) break;
            Edge e = adj.get(u).get(rnd.nextInt(adj.get(u).size()));
            double w = 1 + rnd.nextInt(maxWeight);
            e.weight = w;
            find(e.to, u).weight = w;
            changed++;
        }
        return changed;
    }

    public void print() {
        if (size() == 0) {
            System.out.println("(network is empty)");
            return;
        }
        for (int u = 0; u < size(); u++) {
            StringBuilder sb = new StringBuilder(names.get(u)).append(" -> ");
            List<Edge> list = adj.get(u);
            if (list.isEmpty()) sb.append("(no links)");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(names.get(list.get(i).to)).append("(").append(fmt(list.get(i).weight)).append(")");
            }
            System.out.println(sb);
        }
    }

    private Edge find(int u, int v) {
        for (Edge e : adj.get(u)) if (e.to == v) return e;
        return null;
    }

    private static void validateWeight(double w) {
        if (Double.isNaN(w) || Double.isInfinite(w) || w < 0) {
            throw new IllegalArgumentException("Link cost must be a non-negative number");
        }
    }

    private static String fmt(double w) {
        return (w == Math.rint(w)) ? String.valueOf((long) w) : String.format("%.2f", w);
    }
}
