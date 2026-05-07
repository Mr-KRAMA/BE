from collections import deque
import multiprocessing as mp
from multiprocessing import Pool


class Graph:

    def __init__(self, vertices):

        self.V = vertices
        self.graph = [[] for _ in range(vertices)]

    def add_edge(self, v1, v2):

        self.graph[v1].append(v2)
        self.graph[v2].append(v1)

    # Sequential BFS
    def sequential_bfs(self, start):

        visited = [False] * self.V

        queue = deque([start])

        visited[start] = True

        print("\nSequential BFS Traversal:", end=" ")

        while queue:

            vertex = queue.popleft()

            print(vertex, end=" ")

            for neighbor in self.graph[vertex]:

                if not visited[neighbor]:

                    visited[neighbor] = True
                    queue.append(neighbor)

        print()

    # Helper function for parallel BFS
    def process_level(self, args):

        vertex, visited = args

        neighbors = []

        for neighbor in self.graph[vertex]:

            if not visited[neighbor]:
                neighbors.append(neighbor)

        return neighbors

    # Parallel BFS
    def parallel_bfs(self, start):

        visited = mp.Manager().list([False] * self.V)

        visited[start] = True

        current_level = [start]

        print("Parallel BFS Traversal:", end=" ")

        pool = Pool(processes=4)

        while current_level:

            print(*current_level, end=" ")

            args = [(v, visited) for v in current_level]

            next_level = []

            for neighbors in pool.map(self.process_level, args):

                for n in neighbors:

                    if not visited[n]:

                        visited[n] = True
                        next_level.append(n)

            current_level = next_level

        print()

        pool.close()
        pool.join()


def main():

    # Input from user
    vertices = int(input("Enter number of vertices: "))

    edges = int(input("Enter number of edges: "))

    g = Graph(vertices)

    print("Enter edges (u v):")

    for _ in range(edges):

        u, v = map(int, input().split())

        g.add_edge(u, v)

    start = int(input("Enter starting vertex: "))

    # BFS Traversal
    g.sequential_bfs(start)

    g.parallel_bfs(start)


if __name__ == "__main__":

    main()