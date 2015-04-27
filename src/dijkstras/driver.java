package dijkstras;

import java.util.Scanner;

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
	
	
	public static void main(String[] args){
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
