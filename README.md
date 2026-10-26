# Network Routing Simulator (Fibonacci Heap Dijkstra)

Finds the cheapest route between two nodes of a weighted network and compares a
Fibonacci-heap Dijkstra against a binary-heap Dijkstra while link costs change.

## Build and run
```
javac *.java
java Main
```
Run the tests:
```
java FibonacciHeapTest
java GraphTest
java DijkstraTest
```

## Who owns what
| Member | Files |
|---|---|
|Harshith | FibonacciHeap.java, FibonacciHeapTest.java, README.md  |
|Shahil   | Edge.java, Graph.java, GraphTest.java |
|Manas    | Dijkstra.java, PathResult.java, DijkstraTest.java |
|Devansh  |Main.java, Benchmark.java |

## Complexity
Fibonacci heap: insert O(1), extract-min O(log V) amortized, decrease-key O(1) amortized.
Dijkstra with it: O(E + V log V). With a binary heap: O(E log V). Space: O(V + E).
