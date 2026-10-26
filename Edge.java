/**
 * A weighted link from one node to another (stored in an adjacency list).
 * Contributor: Member 2
 */
public class Edge {
    public final int to;
    public double weight;

    public Edge(int to, double weight) {
        this.to = to;
        this.weight = weight;
    }
}
