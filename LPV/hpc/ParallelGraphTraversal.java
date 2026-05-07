import java.util.Scanner;
public class ParallelGraphTraversal {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of vertices: ");
        int V = sc.nextInt();

        Graph g = new Graph(V);

        System.out.print("Enter number of edges: ");
        int E = sc.nextInt();

        System.out.println("Enter edges (u v):");

        for (int i = 0; i < E; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();

            g.addEdge(u, v);
        }

        System.out.print("Enter starting vertex: ");
        int start = sc.nextInt();

        g.parallelBFS(start);
        g.parallelDFS(start);

        sc.close();
    }
}
