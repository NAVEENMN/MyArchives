package dijkstras;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

class network{
	ArrayList<String> vertexa = new ArrayList<String>();
	ArrayList<String> vertexb = new ArrayList<String>();
	ArrayList<Double> cost = new ArrayList<Double>();
}

public class driver {
	
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
	                    network.vertexa.add(source);
	                    network.vertexb.add(dest);
	                    network.cost.add(cost);
	                }
	                catch( NumberFormatException e )
	                  { System.err.println( "Skipping ill-formatted line " + line ); }
	             }
	            graphFile.close();
	         }
	         catch( IOException e )
	           { System.err.println( e ); }
		return;
	}
	
	public static void main(String[] args){
		/*
		 * This sections reads the network toplogy
		 */
		network network = new network();
		read_network(network, args[0]);
		Set<String> nodes = new HashSet<>(network.vertexa);
		nodes.addAll(network.vertexb);
		System.out.println("vertexs" + nodes);
		/*
		 *  This sections tells user about the available commands
		 *  and how they can use it
		 */
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
