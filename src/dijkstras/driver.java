package dijkstras;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

class network{
	Map<String, Set<String>> topology = new HashMap<String, Set<String>>();
	Map<String, Double> topology_cost = new HashMap<String, Double>();
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
    public Vertex(String argName) { name = argName; }
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
    { target = argTarget; weight = argWeight; }
}



public class driver {
	
	public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(source);

	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
		if (distanceThroughU < v.minDistance) {
		    vertexQueue.remove(v);
		    v.minDistance = distanceThroughU ;
		    v.previous = u;
		    vertexQueue.add(v);
		}
            }
        }
    }
	
	public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }
	
	/*
	 * name : execute
	 * @Params : String
	 * @Return: Void
	 * @desp: This function takes care of executing the
	 * 		  the commands the user issues. 
	 */
	private static void execute(String command){
		String[] params = command.split(" ");
		switch(params[0]){
		case "print":
			System.out.println("Executing print..");
			break;
		case "path":
			System.out.println("Executing path..");
			break;
		case "edgedown":
			System.out.println("Executing edgedown..");
			break;
		case "vertexdown":
			System.out.println("Executing vertexdown..");
			break;
		case "edgeup":
			System.out.println("Executing edgeup..");
			break;
		case "vertexup":
			System.out.println("Executing vertexup..");
			break;
		case "deleteedge":
			System.out.println("Executing deleteedge..");
			break;
		case "addedge":
			System.out.println("Executing addedge..");
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
	                    }else{//new node to graph
	                    	ArrayList<String> edge = new ArrayList<String>(
	                    		    Arrays.asList(dest));
	                    	Set<String> foo = new HashSet<>(edge);
	                    	network.topology.put(source, foo);
	                    	network.topology_cost.put(source + "-->" + dest, cost);
	                    	network.topology_cost.put(dest + "-->" + source, cost);
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
	            	System.out.println(node[0]);
	            	Set<String> existing_edges = network.topology.get(node[0]);
		            	existing_edges.add(node[1]);
		            	network.topology.put(node[0], existing_edges);
	            	
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
		// We now have all nodes in the graph
		
		/*
		 * Setup vertices for calculation
		 */
		for (String node : nodes){
			Vertex temp_node = new Vertex(node);
			current_network.node_vertex.put(node, temp_node);
			current_network.nodes.add(temp_node);// to retrive these nodes save them
		}
		
		/*
		 * Setup edges for calcutaion
		 */
		for(Vertex node : current_network.nodes){
			Set<String> edges_for_current_node = network.topology.get(node.name);
			System.out.println("edges for " + node.name + " are" + edges_for_current_node);
			Edge[] edges_array = new Edge[edges_for_current_node.size()];
			int i = 0;
			for (String edge : edges_for_current_node){
				Vertex v = current_network.node_vertex.get(edge);
				Double cost = network.topology_cost.get(node.name + "-->" + edge);
				edges_array[i] = new Edge(v, cost);
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
		read_network(network, args[0]);
		setup_graph(network, current_network);
		ArrayList<Vertex> vertex = current_network.nodes;
		computePaths(vertex.get(0));
		for (Vertex v : vertex)
		{
		    System.out.println("Distance to " + v + ": " + v.minDistance);
		    List<Vertex> path = getShortestPathTo(v);
		    System.out.println("Path: " + path);
		}
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
		while(!command.contains("quit")){
			System.out.println("please enter your command: ");
			command = user_input.nextLine();
			if(!command.contains("quit")){
				execute(command);
			}
		}
		// User has chosen to quit
		System.out.println("Exiting the program...");
		user_input.close();
	}
	
	
}
