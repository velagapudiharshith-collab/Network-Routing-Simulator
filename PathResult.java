import java.util.List;

/**
 * Result of one shortest-path run, including counters used for benchmarking.
 */
public class PathResult {
    public final boolean reachable;
    public final List<String> path;
    public final double cost;
    public final long extractMins;
    public final long decreaseKeys;   // for the binary heap this counts re-insertions
    public final long nanos;

    public PathResult(boolean reachable, List<String> path, double cost,
                      long extractMins, long decreaseKeys, long nanos) {
        this.reachable = reachable;
        this.path = path;
        this.cost = cost;
        this.extractMins = extractMins;
        this.decreaseKeys = decreaseKeys;
        this.nanos = nanos;
    }

    public String describe() {
        if (!reachable) return "No route found";
        if (path.isEmpty()) return "Full shortest-path tree computed";
        return String.join(" -> ", path) + String.format("  (cost %.2f)", cost);
    }
}
