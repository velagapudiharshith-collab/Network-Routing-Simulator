import java.util.Random;
import java.util.Scanner;

/**
 * Menu-driven console demo of the Network Routing Simulator.
 * Contributor: Member 4
 */
public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final Random rnd = new Random();
    private static Graph graph = new Graph();

    public static void main(String[] args) {
        System.out.println("=== Network Routing Simulator (Fibonacci Heap Dijkstra) ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Choose an option: ");
            try {
                switch (choice) {
                    case "1": addNode(); break;
                    case "2": addLink(); break;
                    case "3": findRoute(); break;
                    case "4": updateLink(); break;
                    case "5": simulateCongestion(); break;
                    case "6": compareHeaps(); break;
                    case "7": runBenchmark(); break;
                    case "8": graph.print(); break;
                    case "9": loadSample(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add node");
        System.out.println("2. Add link");
        System.out.println("3. Find shortest route (Fibonacci heap)");
        System.out.println("4. Update link cost");
        System.out.println("5. Simulate congestion");
        System.out.println("6. Compare Fibonacci heap vs binary heap");
        System.out.println("7. Benchmark on a random network");
        System.out.println("8. Display network");
        System.out.println("9. Load sample network");
        System.out.println("0. Exit");
    }

    private static String prompt(String msg) {
        System.out.print(msg);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private static double readDouble(String msg) {
        try {
            return Double.parseDouble(prompt(msg));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Please enter a number");
        }
    }

    private static int readIntOr(String msg, int fallback) {
        String s = prompt(msg + " [default " + fallback + "]: ");
        if (s.isEmpty()) return fallback;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Please enter a whole number");
        }
    }

    private static void addNode() {
        int id = graph.addNode(prompt("Node name: "));
        System.out.println("Node ready: " + graph.getName(id));
    }

    private static void addLink() {
        String a = prompt("From node: ");
        String b = prompt("To node: ");
        graph.addLink(a, b, readDouble("Cost: "));
        System.out.println("Link saved.");
    }

    private static void updateLink() {
        String a = prompt("From node: ");
        String b = prompt("To node: ");
        double cost = readDouble("New cost: ");
        System.out.println(graph.updateLink(a, b, cost) ? "Link updated." : "That link does not exist.");
    }

    private static boolean hasNodes() {
        if (graph.size() == 0) {
            System.out.println("The network is empty. Add nodes or load the sample network first.");
            return false;
        }
        return true;
    }

    private static void findRoute() {
        if (!hasNodes()) return;
        int src = graph.getId(prompt("Source node: "));
        int dst = graph.getId(prompt("Destination node: "));
        System.out.println(Dijkstra.runFibonacci(graph, src, dst).describe());
    }

    private static void compareHeaps() {
        if (!hasNodes()) return;
        int src = graph.getId(prompt("Source node: "));
        int dst = graph.getId(prompt("Destination node: "));
        PathResult f = Dijkstra.runFibonacci(graph, src, dst);
        PathResult b = Dijkstra.runBinary(graph, src, dst);
        System.out.printf("Fibonacci heap: %s | extract-mins %d, decrease-keys %d, %.1f us%n",
                f.describe(), f.extractMins, f.decreaseKeys, f.nanos / 1000.0);
        System.out.printf("Binary heap   : %s | extract-mins %d, re-insertions %d, %.1f us%n",
                b.describe(), b.extractMins, b.decreaseKeys, b.nanos / 1000.0);
        System.out.println("(Tiny networks are dominated by timer noise; use option 7 for a fair comparison.)");
    }

    private static void simulateCongestion() {
        if (!hasNodes()) return;
        int count = readIntOr("How many links to change", 3);
        System.out.println(graph.simulateCongestion(rnd, count, 20) + " link cost(s) changed.");
    }

    private static void runBenchmark() {
        int n = readIntOr("Number of nodes", 2000);
        int m = readIntOr("Number of links", 20000);
        int rounds = readIntOr("Rounds", 20);
        Benchmark.run(n, m, rounds, 50);
    }

    private static void loadSample() {
        graph = new Graph();
        graph.addLink("A", "B", 4);
        graph.addLink("B", "E", 3);
        graph.addLink("A", "C", 2);
        graph.addLink("C", "E", 9);
        graph.addNode("F");   // isolated node, useful for the "no route" demo
        System.out.println("Sample network loaded (A-B 4, B-E 3, A-C 2, C-E 9, F isolated).");
    }
}
