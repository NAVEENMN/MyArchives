package dijkstras;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

class network{
	Map<String, Set<String>> topology = new HashMap<String, Set<String>>();
	Map<String, Boolean> topology_status = new HashMap<String, Boolean>();
	Map<String, Double> topology_cost = new HashMap<String, Double>();
	Map<String, Boolean> topology_cost_status = new HashMap<String, Boolean>();
}

class current_network{
	ArrayList<Vertex> nodes = new ArrayList<Vertex>();
	Map<String, Vertex> node_vertex = new HashMap<String, Vertex>();
}
class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Boolean visited;
    public Vertex(String argName) { name = argName;}
    public String toString() { return name; }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge
{
    public final Vertex target;
    public final double weight;
    public Edge(Vertex argTarget, double argWeight)
    { target = argTarget; weight = argWeight;}
}



public class driver {
	
	/*
	 * name : computePaths
	 * @Params : Vertex
	 * @Return: Void
	 * @desp: This function computes the graph path costs using Dijkstra`s algorithm. 
	 */
	public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        heap<Vertex> hp = new heap<Vertex>();
      	vertexQueue.add(source);
      	hp.insert(source);
      	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();
      	Vertex minimum_cost_node = hp.min();
            for (Edge e : u.adjacencies)
            {
            	if(e != null ){
                Vertex v = e.target;
                Vertex min = minimum_cost_node;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
				    vertexQueue.remove(v);
				    v.minDistance = distanceThroughU ;
				    v.previous = u;
				    vertexQueue.add(v);
				    hp.insert(v);
				}else{
					//System.out.println("skipping");
				}
            	}
            }
        }
    }
	
	/*
	 * name : getShortestPathTo
	 * @Params : Vertex
	 * @Return: Void
	 * @desp: This function retrieves the shortest path from a given node to
	 * 		  queried node. 
	 */
	public static List<Vertex> getShortestPathTo(Vertex node)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = node; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }
	
	/*
	 * name : check_if_node_exist
	 * @Params : String
	 * @Return: boolean
	 * @desp: This function checks if the given node name is correct or not . 
	 */
	private static boolean check_if_node_exist(current_network network, String node){
		if (network.nodes.contains(node) ){
			return true;
		}else{
			return false;
		}
	}
	/*
	 * name : depth_first_search
	 * @Params : Vertex
	 * @Return: Void
	 * @desp: This function finds all nodes reachable. 
	 * 	      The run time of this algorithm is O(|V|+|E|)
	 * 		  where is number of vertices present in this graph and E is 
	 * 		  corresponding edges.
	 */
	private static void breadth_first_search(Vertex root, network network){
		 //Since queue is a interface
        Queue<Vertex> queue = new LinkedList<Vertex>();

        if(root == null) return;

        root.visited = true;
         //Adds to end of queue
        queue.add(root);
        
        while(!queue.isEmpty())
        {
            //removes from front of queue
            Vertex r = queue.remove(); 
            System.out.print("  "+r.name + "\n");

            //Visit child first before grandchild
            for( Edge edges : root.adjacencies)
            {
                if(! edges.target.visited)
                {
                
                	if( network.topology_status.get(edges.target.name)){
                		queue.add(edges.target);
                	}
                		edges.target.visited = true;
                	
                }
            }
        }
	}
	/*
	 * name : execute
	 * @Params : String
	 * @Return: Void
	 * @desp: This function takes care of executing the
	 * 		  the commands the user issues. 
	 */
	private static void execute(network network, current_network curr_net, String command){
		String[] params = command.split(" ");
		switch(params[0]){
		case "print":
			System.out.println("Executing print..");
			setup_graph(network, curr_net);
			ArrayList<Vertex> vertex = curr_net.nodes;
			System.out.println(vertex.size());
			for (Vertex v : vertex){
				System.out.println(v);
				Edge[] adjacents = v.adjacencies;
				
				for(Edge e : adjacents){
					if(e != null){
						System.out.println("	" + e.target.name + " " + e.weight);
					} 	
				}
			}
			break;
		case "path":
			//if(check_if_node_exist(curr_net, params[1]) && check_if_node_exist(curr_net, params[2])){
				System.out.println("Executing path..");
				setup_graph(network, curr_net);
				ArrayList<Vertex> vertices = curr_net.nodes;
				computePaths(curr_net.node_vertex.get(params[1]));
				List<Vertex> path = getShortestPathTo(curr_net.node_vertex.get(params[2]));
			    System.out.println("Path: " + path +" "+curr_net.node_vertex.get(params[2]).minDistance);
			break;
		case "edgedown":
			System.out.println("Executing edgedown..");
			network.topology_cost_status.put(params[1]+"-->"+params[2], false);
			setup_graph(network, curr_net);
			break;
		case "vertexdown":
			System.out.println("Executing vertexdown..");
			network.topology_status.put(params[1], false);
			setup_graph(network, curr_net);
			break;
		case "edgeup":
			System.out.println("Executing edgeup..");
			network.topology_cost_status.put(params[1]+"-->"+params[2], true);
			setup_graph(network, curr_net);
			break;
		case "vertexup":
			System.out.println("Executing vertexup..");
			network.topology_status.put(params[1], true);
			setup_graph(network, curr_net);
			break;
		case "deleteedge":
			System.out.println("Executing deleteedge..");
			Set<String> edge_of_head = network.topology.get(params[1]);
			edge_of_head.remove(params[2]);
			network.topology.put(params[1], edge_of_head);
			Set<String> edge_of_tail = network.topology.get(params[2]);
			edge_of_tail.remove(params[1]);
			network.topology.put(params[2], edge_of_tail);
			network.topology_cost.remove(params[1]+"-->"+params[2]);
			network.topology_cost.remove(params[2]+"-->"+params[1]);
			setup_graph(network, curr_net);
			break;
		case "addedge":
			System.out.println("Executing addedge..");
			Set<String> edges_of_head = network.topology.get(params[1]);
			edges_of_head.add(params[2]);
			network.topology.put(params[1], edges_of_head);
			Set<String> edges_of_tail = network.topology.get(params[2]);
			edges_of_tail.add(params[1]);
			network.topology.put(params[2], edges_of_tail);
			network.topology_cost.put(params[1]+"-->"+params[2], Double.parseDouble(params[3]));
			network.topology_cost.put(params[2]+"-->"+params[1], Double.parseDouble(params[3]));
			setup_graph(network, curr_net);
			break;
		case "reachable":
			setup_graph(network, curr_net);
			for(Vertex nd :curr_net.nodes ){
				nd.visited = false;
			}
			for(String node : network.topology.keySet()){
				System.out.println(node);
				if( network.topology_status.get(node)){
					breadth_first_search(curr_net.node_vertex.get(node), network);
            	}
				for(Vertex nd :curr_net.nodes ){
					nd.visited = false;
				}
			}
			break;
		default:
			System.out.println("Unrecognized command");
			break;
		}
		return;
	}
	
	/*
	 * name : read_network
	 * @Params : network, String
	 * @Return: Void
	 * @desp: This function reads the network topology 
	 * 		  and loads the data to network class
	 * 		  defined in the the file network.txt. 
	 * 	      The data is defined in this form in the file
	 *        Vertexa Vetrexb costof edge [newline]
	 */
	@SuppressWarnings("null")
	private static void read_network(network network, String file_name){
		 try
	        {
	            FileReader fin = new FileReader( file_name );
	            Scanner graphFile = new Scanner( fin );
	            // Read the network
	            String line;
	            while( graphFile.hasNextLine( ) )
	            {
	                line = graphFile.nextLine( );
	                StringTokenizer st = new StringTokenizer( line );

	                try
	                {
	                	
	                    if( st.countTokens( ) != 3 )
	                    {
	                        System.err.println( "Skipping ill-formatted line " + line );
	                        continue;
	                    }
	                    String source  = st.nextToken( );
	                    String dest    = st.nextToken( );
	                    Double cost = Double.parseDouble(st.nextToken());
	                    Set<String> edges = network.topology.get(source);
	                    if ( edges != null ){
	                    	edges.add(dest);
	                    	network.topology.put(source, edges);
	                    	network.topology_cost.put(source + "-->" + dest, cost);
	                    	network.topology_cost.put(dest + "-->" + source, cost);
	                    	network.topology_cost_status.put(source + "-->" + dest, true);
	                    	network.topology_cost_status.put(dest + "-->" + source, true);
	                    }else{//new node to graph
	                    	ArrayList<String> edge = new ArrayList<String>(
	                    		    Arrays.asList(dest));
	                    	Set<String> foo = new HashSet<>(edge);
	                    	network.topology.put(source, foo);
	                    	network.topology_cost.put(source + "-->" + dest, cost);
	                    	network.topology_cost.put(dest + "-->" + source, cost);
	                    	network.topology_cost_status.put(source + "-->" + dest, true);
	                    	network.topology_cost_status.put(dest + "-->" + source, true);
	                    }                  
	                }
	                catch( NumberFormatException e )
	                  { System.err.println( "Skipping ill-formatted line " + line ); }
	             }
	            
	            System.out.println(network.topology_cost);
	            Set<String> nodes = new HashSet<>();
	    		for(String key : network.topology_cost.keySet()){
	    			String[] node = key.split("-->");
	    			nodes.add(node[0]);
	    		}
	            // adding remaing nodes as keys
	            for(String edge : network.topology_cost.keySet()){
	            	String[] node = edge.split("-->");
	            	Set<String> existing_edges = network.topology.get(node[0]);
	            	Set<String> edg = new HashSet<String>();
	            	if(existing_edges == null){
	            		for (String nd : nodes){
	            			if(network.topology_cost.containsKey(node[0] + "-->" + nd)){
	            				edg.add(nd);
	            			}
	            		}
	            		network.topology.put(node[0], edg);	
	            	}
	            }
	            
	            // creating bi directional graph
	            for(String edge : network.topology_cost.keySet()){
	            	String[] node = edge.split("-->");
	            	Set<String> existing_edges = network.topology.get(node[0]);
		            	existing_edges.add(node[1]);
		            	network.topology.put(node[0], existing_edges);
		            	network.topology_status.put(node[0], true);
		            	network.topology_status.put(node[1], true);
	            }
	            graphFile.close();
	         }
	         catch( IOException e )
	           { System.err.println( e ); }
		return;
	}
	
	
	private static void setup_graph(network network, current_network current_network){
		
		Set<String> nodes = new HashSet<>();
		for(String key : network.topology_cost.keySet()){
			String[] node = key.split("-->");
			nodes.add(node[0]);
		}
		
		/*
		 * Setup vertices for calculation
		 */
		current_network.nodes.clear();
		for (String node : nodes){
			if(network.topology_status.get(node)){
				Vertex temp_node = new Vertex(node);
				current_network.node_vertex.put(node, temp_node);
				current_network.nodes.add(temp_node);// to retrive these nodes save them
			}
		}
		
		/*
		 * Setup edges for calculation
		 */
		for(Vertex node : current_network.nodes){
			Set<String> edges_for_current_node = network.topology.get(node.name);
			//System.out.println("edges for " + node.name + " are" + edges_for_current_node);
			Edge[] edges_array = new Edge[edges_for_current_node.size()];
			int i = 0;
			for (String edge : edges_for_current_node){
				Vertex v = current_network.node_vertex.get(edge);
				Double cost = network.topology_cost.get(node.name + "-->" + edge);
				if(network.topology_cost_status.get(node.name + "-->" + edge)){
					edges_array[i] = new Edge(v, cost); // add only if edge is up
				}
				i++;
			}
			node.adjacencies = edges_array;
		}
		return;	
	}
	
	
	public static void main(String[] args){
		/*
		 * This sections reads the network toplogy
		 */
		network network = new network();
		current_network current_network = new current_network();
		/*
		 *  This sections tells user about the available commands
		 *  and how they can use it
		 */
		/*
		System.out.println("Please use following commands"
				+ "\nprint"
				+ "\npath vertex1 vertex2"
				+ "\nedgedown vertex1 vertex2"
				+ "\nvertexdown vertex"
				+ "\nprint reachable"
				+ "\nedgeup vertex1 vertex2 "
				+ "\nvertexup Belk"
				+ "\ndeleteedge vertex1 vertex2"
				+ "\naddedge vertex1 vertex2 cost"
				+ "\nquit");
				*/
		/*
		 * Take command from user in this section
		 */
		Scanner user_input = new Scanner(System.in);
		String command = "start";
		read_network(network, args[0]);
		setup_graph(network, current_network);
		while(!command.contains("quit")){
			System.out.println("please enter your command: ");
			command = user_input.nextLine();
			if(!command.contains("quit")){
				execute(network, current_network, command);
			}
		}
		// User has chosen to quit
		System.out.println("Exiting the program...");
		user_input.close();
	}
	
}
