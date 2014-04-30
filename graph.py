"""graph.py - A Python code for analysing Dijkstra`s and Kruskal Algorithm."""
'''
__name__ = "graph"
__author1__ = "Naveen Mysore <nmysore@uncc.edu>"
__author2__ = "Apoorva Katti <akatti@uncc.edu>"
'''
import time
#--------------------------------------------------------------------------
parent = dict()
rank = dict()
graph = dict()
global isDAG
isDAG = "true"
#--------------------------------------------------------------------------
class graph():
	def __init__(self,edges, node, distances): #constructor makes initialization
   		self.nodes = set()
    		self.nodes = node
    		self.edges = edges
    		self.distances = distances

  	def neighbor(self, node): # fetches all the neighbor for a given a node
  		neigh = []
		Edges = self.edges
		for x in range(0,len(Edges)):
			single_edge = Edges[x]
			if(node == single_edge[0]):
 				neigh.append(single_edge[1])	
 		return(neigh)
 	
  	def distance_between(self, f_node, t_node): # fetches the distances between from node and to node
  		f = open('data.txt', 'r')
		f.readline()
		for line in f:
			if line[0] == f_node and line[2] == t_node :
				distance = int(line[4:])
				return(distance)
				
	def make_set(self, vertice): # makes a set for a given vertice and marks rank as zero
    		parent[vertice] = vertice
    		rank[vertice] = 0
    	
    	def find(self, vertice): # find if a given vertice is then in the graph
    		if parent[vertice] != vertice:
       			 parent[vertice] = self.find(parent[vertice])
    		return parent[vertice]
    	
    	def union(self, vertice1, vertice2): # create a set union for the given two vertice
    		root1 = self.find(vertice1)
    		root2 = self.find(vertice2)
    		if root1 != root2:
        		if rank[root1] > rank[root2]:
            			parent[root2] = root1
        		else:
            			parent[root1] = root2
            			if rank[root1] == rank[root2]: rank[root2] += 1
	#---------------------------------------------> 			
def krushkal(Graph, Nodes, Edges):
	
	graph = { 	# creating a dictonary for graph with vertices and edges
		'vertices': Nodes,
		'edges': Edges
	}
	for vertice in graph['vertices']:
        	Graph.make_set(vertice)

    	minimum_spanning_tree = set() # create a set called for minimum spanning tree
    	edges = list(graph['edges'])
    	edges.sort() # sort the edges to get in proper order like A, B, C D, ...
    	for edge in edges:
        	weight, vertice1, vertice2 = edge
        	print "Checking edge: ", edge
        	if Graph.find(vertice1) != Graph.find(vertice2): # if two nodes are there in same selected edges in a graph then discard else add
            		Graph.union(vertice1, vertice2)
            		minimum_spanning_tree.add(edge)
            		print "adding ", edge, "to minimum spanning tree"
            	else:
            		print "Edge ", edge, "is not added to minimum spanning tree"
            	print "----------------------------------------"
    	return minimum_spanning_tree  	
  	#---------------------------------------------->
def dijsktra(graph, StartNode):
	#----------------------------------------------- Declaration 
	cost = []
	path = []
	finalcost = []
	costofneighbors = []
	visited = []
	Q = graph.nodes
	nodelist = list(Q) # a copy of nodes
	for x in range(0,len(graph.nodes)):
		cost.append(999) # set up costs to infinity
		finalcost.append(999) #copy of costs
		visited.append(0) # initially nothing is visted
	cost[0] = 0
	finalcost[0] = 0
	costofneighbors.append(0)
	u = StartNode
	neighborofu = graph.neighbor(u) # neighbors of initial node
	visited[0] = 1 # first is visted now	#-------------------------------------------------------------------------------------------------------
	while len(nodelist):
		#--------------------------- finding minimum costs in neighbors
		global isDAG
		for x in range(0,len(neighborofu)):
			try:
				c = nodelist.index(neighborofu[x])
			except ValueError:
				isDAG = "false"
				return finalcost, path
			if c:
				costofneighbors.append(cost[Q.index(neighborofu[x])])
		minimumcost = min(costofneighbors)
		costofneighbors = [] #once found reset cost of neighbors to zero
		u = Q[cost.index(minimumcost)]
		path.append(u)
		#--------------------------------------------------------------
		posofu = Q.index(u) #pos of u
		neighborofu = graph.neighbor(u)
		print "-----------------------------------"
		print "Current node selected:", u
		print "neighbor of", u, ":", neighborofu
		for x in range(0,len(neighborofu)):
			posofv = Q.index(neighborofu[x])
			visited [posofv] = 1 #the next node visited
			#print "position of u", posofu
			#print "position of v", posofv
			distance = graph.distance_between(u,neighborofu[x])
			print "distance between", u, "and", neighborofu[x], ":", distance
			currentcost = cost[posofu]+distance
			if currentcost < finalcost[posofv]:
				cost[posofv] = currentcost
				finalcost[posofv] = currentcost
				currentcost = 0
		cost[posofu] = 999 # make the already acceses node unreacheable 
		nodelist.remove(u) # removed the accessed node from nodelist
		print "visited:", visited
		print "Path:", path
		print "Cost:", finalcost
	return finalcost, path
	#----------------------------------------------------------------------------------------------------------
def main():
	 #-----------------------------------------------------------> Declarations
	 edges = []
	 nodefirst = []#raw
	 distances = []
	 edgesforkrushkal = []
	 global isDAG
	 #------------------------------------------------------------> Reading data from file data.txt and fetching node, edges and distances
	 f = open('data.txt', 'r')
	 f.readline()
	 for line in f:
	 	 edge = []
	 	 num = int(line[4:])
	 	 distances.append(num)
	 	 edge.append(line[0])
	 	 edge.append(line[2])
	 	 nodefirst.append(line[0])
	 	 nodefirst.append(line[2])
	 	 edges.append(edge)
	 	 edgesforkrushkal.append((num, line[0], line[2]))
	 nd = set(nodefirst)
	 EdgesKrushkal = set(edgesforkrushkal)
	 nodes = list (nd)
	 nodes.sort()#final
	 #-----------------------------------------------> Dijsktras execution
	 o = graph(edges, nodes, distances)#initialize
	 start_time1 = time.time()
	 print "============================================="
	 print "	Dijkstra`s Algorithm analysis  	     "
	 print "============================================="
	 cost, path = dijsktra(o, nodes[0])
	 end_time1 = time.time() - start_time1
	 #--------------------------------------------------------------------
	 #-----------------------------------------------> Krushkal execution
	 print "============================================="
	 print "	Kruskal`s Algorithm analysis  	     "
	 print "============================================="
	 start_time2= time.time()
	 sptree = krushkal(o, nodes, EdgesKrushkal)
	 end_time2= time.time() - start_time2
	 print "============================================="
	 print "	Dijkstra`s Algorithm Report  	     "
	 print "============================================="
	 print "The final path is:", path
	 print "The final costs are:", cost
	 if (isDAG == "true"):
	 	print "The given graph is a DAG..."
	 else :
	 	print "Not a dag the execution terminates here."
	 print "Total execution time:", end_time1, " Seconds"
	 print "============================================="
	 print "	Kruskal`s Algorithm Report  	     "
	 print "============================================="
	 print "The minimum spanning tree is a graph contaning", sptree
	 print "Total execution time:", end_time2, " Seconds"
if __name__ == "__main__":
    main()
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
