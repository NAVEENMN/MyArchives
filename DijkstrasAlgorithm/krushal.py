parent = dict()
rank = dict()
graph = dict()

def make_set(vertice):
    parent[vertice] = vertice
    rank[vertice] = 0

def find(vertice):
    if parent[vertice] != vertice:
        parent[vertice] = find(parent[vertice])
    return parent[vertice]

def union(vertice1, vertice2):
    root1 = find(vertice1)
    root2 = find(vertice2)
    if root1 != root2:
        if rank[root1] > rank[root2]:
            parent[root2] = root1
        else:
            parent[root1] = root2
            if rank[root1] == rank[root2]: rank[root2] += 1

def kruskal(graph):
    for vertice in graph['vertices']:
        make_set(vertice)

    minimum_spanning_tree = set()
    edges = list(graph['edges'])
    edges.sort()
    for edge in edges:
        weight, vertice1, vertice2 = edge
        if find(vertice1) != find(vertice2):
            union(vertice1, vertice2)
            minimum_spanning_tree.add(edge)
    return minimum_spanning_tree
'''
graph = {
        'vertices': ['A', 'B', 'C', 'D', 'E'],
        'edges': set([
            (10, 'A', 'B'),
            (3, 'A', 'C'),
            (1, 'B', 'C'),
            (2, 'B', 'D'),
            (4, 'C', 'B'),
            (8, 'C', 'D'),
            (2, 'C', 'E'),
            (7, 'D', 'E'),
            (9, 'E', 'D'),
            ])
        }
'''
def main():
	edges = []
	nodefirst = []#raw
	distances = []
	combs = []
	f = open('data.txt', 'r')
	f.readline()
	for line in f:
		edge = []
	 	num = int(line[4:])
	 	distances.append(num)
	 	edge.append(num)
	 	edge.append(line[0])
	 	edge.append(line[2])
	 	nodefirst.append(line[0])
	 	nodefirst.append(line[2])
	 	edges.append(edge)
	 	combs.append((num, line[0], line[2]))
	nd = set(nodefirst)
	nodes = list (nd)
	edg = set(combs)
	nodes.sort()#final
	graph = { 
		'vertices': nodes,
		'edges': edg
	}
	grap = kruskal(graph)
	print grap
if __name__ == "__main__":
    main()
