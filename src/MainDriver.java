import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

/*
 * This a Driver Class which controls all the map reduce operations
 */
public class MainDriver {
	/*
	 * This is the main function for Driver class input : arg[0] : input path
	 * 													  arg[1] : output path
	 * 													  arg[2] : output post path
	 * 													  arg[3] : review.txt path
	 */
	public static void main(String[] args) throws Exception {
		
		Writer review_input = null;
		Process p;
		//  prompt the user to enter their review
	    System.out.print("Enter your review: ");
	 
	    //  open up standard input
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	 
	    String userName = null;
	    try {
	         userName = br.readLine();
	    } catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	    }
	 
	      System.out.println("review, " + userName);
		try {
			review_input = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("review.txt"), "utf-8"));
			review_input.write(userName);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {review_input.close();} catch (Exception ex) {}
		}
		
		p = Runtime.getRuntime().exec("python /home/nmysore/Documents/pr/sen/pre_process_review.py");
		try {
		    //thread to sleep for the specified number of milliseconds
		    Thread.sleep(2000);
		} catch ( java.lang.InterruptedException ie) {
		    System.out.println(ie);
		}
		Index job1 = new Index();
		process job2 = new process();
		String review;
		/*
		 * String raw_data = arg[0]
		 * job1_output = arg[1]
		 * job2_input = 
		 * job2_output = 
		 */
		/*
		 * This is the bucketing part This executes each MapReduce tasks for
		 * generating base rank and refrences
		 */
		review = read_review(args[3]);
		for (int i = 0; i <= 4; i++) {
			job1.runjob(args[0], args[1], i);//Bucketing for bucket i
			job2.runjob(args[1] + Integer.toString(i), args[2], review, i);//Processing for bucket i
			p = Runtime.getRuntime().exec("cp /home/nmysore/Documents/pr/sen/outputs/output_post"+Integer.toString(i)+"/part-00000"+ " /home/nmysore/Documents/pr/sen/prerank/"+Integer.toString(i)+".txt");
			p = Runtime.getRuntime().exec("rm -rf /home/nmysore/Documents/pr/sen/outputs");
		}
		
		 //organize_job3_input();
		// delete_folders();
	}

	/*
	 * This method will organize the input for job3
	 */
	
	private static void organize_job3_input(){
		for(int i=0; i<=4 ; i++){
		try {
			Process p = Runtime.getRuntime().exec("rm -rf /home/nmysore/Documents/pr/sen/outputs/output"+"i");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	/*
	 * This method reads the review
	 */
	private static void deleteFolder(String deletePath) {
		try {
			String deleteCommand = "rm -r ";
			// Run unix command to delete folder using path
			String[] command = { "bash", "-c", deleteCommand + deletePath };
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 *  This method reads the review
	 */
	private static String read_review(String review_path) {
		String content = null;
		File file = new File(review_path); // for ex foo.txt
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/*
	 * This method takes care of running the processing tasks input : line :
	 * String which is the actual review bucketid : Integer which refers to the
	 * bucket id
	 */
	private static void process(String line, int bucketid) {
	}
}
