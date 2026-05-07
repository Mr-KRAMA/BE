import java.util.*;
import java.util.concurrent.*;

class Graph {
    private int V;
    private List<List<Integer>> adj;

    // Constructor
    Graph(int V) {
        this.V = V;
        adj = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    // Add edge
    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u); // Undirected graph
    }

    // 🔵 Parallel BFS
    void parallelBFS(int start) {
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();

        visited[start] = true;
        queue.add(start);

        System.out.print("\nParallel BFS Traversal: ");

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (!queue.isEmpty()) {

            int size = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            // Remove all nodes of current level
            for (int i = 0; i < size; i++) {
                currentLevel.add(queue.poll());
            }

            List<Future<List<Integer>>> futures = new ArrayList<>();

            for (int node : currentLevel) {

                futures.add(executor.submit(() -> {
                    List<Integer> newNodes = new ArrayList<>();

                    synchronized (System.out) {
                        System.out.print(node + " ");
                    }

                    for (int neighbor : adj.get(node)) {
                        synchronized (visited) {
                            if (!visited[neighbor]) {
                                visited[neighbor] = true;
                                newNodes.add(neighbor);
                            }
                        }
                    }

                    return newNodes;
                }));
            }

            // Add discovered nodes to queue
            for (Future<List<Integer>> future : futures) {
                try {
                    queue.addAll(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        executor.shutdown();
        System.out.println();
    }

    // 🔴 Parallel DFS Utility
    void parallelDFSUtil(int node, boolean[] visited, ExecutorService executor) {

        synchronized (visited) {
            if (visited[node]) {
                return;
            }

            visited[node] = true;
        }

        synchronized (System.out) {
            System.out.print(node + " ");
        }

        List<Future<?>> futures = new ArrayList<>();

        for (int neighbor : adj.get(node)) {
            futures.add(executor.submit(() -> {
                parallelDFSUtil(neighbor, visited, executor);
            }));
        }

        // Wait for all tasks
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 🔴 Parallel DFS
    void parallelDFS(int start) {

        boolean[] visited = new boolean[V];

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        System.out.print("\nParallel DFS Traversal: ");

        parallelDFSUtil(start, visited, executor);

        executor.shutdown();

        System.out.println();
    }
}