def create_a_graph():
	'''
	nodes = raw_input("Enter all the nodes with space no seperation : ")
	node_list = list(nodes)
	graph = dict()
	for node in node_list:
		adjacent = raw_input("Enter adjancent nodes of node " + node + " ")
		print list(adjacent)
		graph[node] = set( list(adjacent) )
	'''
	graph = dict()
	graph['A'] = set( ['B', 'C'] )
	graph['B'] = set( ['A', 'C', 'D'] )
	graph['C'] = set( ['A', 'B', 'D'] )
	graph['D'] = set( ['C', 'B'] )
	return graph

def dfs(graph, start):
	visited, stack = set(), [start]
	while stack:
		node = stack.pop()
		if node not in visited:
			visited.add(node)
			stack.extend(graph[node] - visited)
	return visited
def bfs(graph, start):
	visited, queue = set(), [start]
	while queue:
		node = queue.pop(0)
		if node not in visited:
			visited.add(node)
			queue.extend(graph[node] - visited)
	return visited


def main():
	graph = create_a_graph()
	dfs_result = dfs(graph, 'A')
	bfs_result = bfs(graph, 'A')
	print dfs_result
	print bfs_result
if __name__ == "__main__":
	main()
