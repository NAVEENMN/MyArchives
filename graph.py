class graph():
  def __init__(self,edges, node, distances):
    self.nodes = set()
    self.nodes = node
    self.edges = edges
    self.distances = distances

  def neighbor(self, node):
  	neigh = []
	Edges = self.edges
	for x in range(0,len(Edges)):
		single_edge = Edges[x]
		if(node == single_edge[0]):
 			neigh.append(single_edge[1])	
 	return(neigh)
 	
  def distance_between(self, f_node, t_node):
  	f = open('data.txt', 'r')
	f.readline()
	for line in f:
		if line[0] == f_node and line[2] == t_node :
			distance = int(line[4:])
			return(distance)
  	
  
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
		for x in range(0,len(neighborofu)):
			try:
				c = nodelist.index(neighborofu[x])
			except ValueError:
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
		print "-----------------------------------"
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
		print "-----------------------------------"
		nodelist.remove(u) # removed the accessed node from nodelist
		print "visited:", visited
		print "Path:", path
		print "Cost:", finalcost
	return finalcost, path
	#----------------------------------------------------------------------------------------------------------
def main():
	 edges = []
	 nodefirst = []#raw
	 distances = []
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
	 nd = set(nodefirst)
	 nodes = list (nd)
	 nodes.sort()#final
	 #-----------------------------------------------> data ready
	 o = graph(edges, nodes, distances)#initialize
	 cost, path = dijsktra(o, nodes[0])
	 #------------------------------------------------------------
	 print "The final path is:", path
	 print "The final costs are:", cost
if __name__ == "__main__":
    main()
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
