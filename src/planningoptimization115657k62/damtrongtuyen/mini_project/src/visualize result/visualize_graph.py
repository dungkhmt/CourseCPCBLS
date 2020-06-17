import networkx as nx
import numpy as np
import matplotlib.pyplot as plt

def visualize(graph, clusters, N, K):

    edges = dict()
    for i in range(N):
        for j in range(N):
            if graph[i][j] > 0 and i != j:
                edges[(i, j)] = graph[i][j]

    cmap = plt.get_cmap('viridis')
    colors = cmap(np.linspace(0, 1, K))
    node_colors = [colors[i] for i in clusters]
    edges_labels = list(edges.values())

    g = nx.Graph()
    for e in edges.items():
        g.add_edge(e[0][0], e[0][1], length = 10.0/e[1])
  
    pos = nx.spring_layout(g)
    nx.draw(g, pos, node_color=node_colors, with_labels=True)

    edges_label = nx.draw_networkx_edge_labels(g, pos, edge_labels = edges, node_color=node_colors, with_labels=True)
    plt.show()

if __name__ == "__main__":
    PATH_DATA = r"graph_14_39_result.txt"
    with open(PATH_DATA, "r") as reader:
        N, K = reader.readline().split()
        N, K = int(N), int(K)
        graph = []
        for i in range(N):
            d = list(map(int, reader.readline().split()))
            graph.append(d)
        clusters = list(map(int, reader.readline().split()))
    visualize(graph, clusters, N, K)
      
