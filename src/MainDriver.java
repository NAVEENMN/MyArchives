import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * This a Driver Class which controls all the map reduce operations
 */
public class MainDriver {
	public static Double sum = 0.0;
	public static int uniflag = 0;
	public static Double words_count = 0.0;
	/*
	 * This is the main function for Driver class input : arg[0] : input path
	 * 													  arg[1] : output path
	 * 													  arg[2] : output post path
	 * 													  arg[3] : review.txt path
	 */
	public static void main(String[] args) throws Exception {
			ArrayList<String> biwords = new ArrayList<String>();
			ArrayList<String> uniwords = new ArrayList<String>();
			Map<String, Double> map = new HashMap<String, Double>();
		Writer review_input = null;
		Process p;
		ArrayList<Integer> sentis = new ArrayList<Integer>();
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
		
		gen_rank job3 = new gen_rank();
		job3.runjob("/home/nmysore/Documents/pr/sen/prerank","/home/nmysore/Documents/pr/sen/postrank");
		
		/*
		 * We will have a hash dataset ready by the end of this loop
		 */
		try(BufferedReader rk = new BufferedReader(new FileReader("/home/nmysore/Documents/pr/sen/postrank/part-00000"))) {
	        String line = rk.readLine();
	        while (line != null) {
	            String[] pra = line.split("\t");
	            if(pra[0].contains(" ")){
	            		biwords.add(pra[0]);
	            		map.put(pra[0], Double.parseDouble(pra[1]));
	            }else{
	            	uniwords.add(pra[0]);
	            	map.put(pra[0], Double.parseDouble(pra[1]));
	            }/*
	            String rank = pra[1];
	            sentis.add(Integer.parseInt(rank));
	            */
	            line = rk.readLine();
	        }
	    }
		
		for(String str : map.keySet()){
			if(str.contains(" ")){
				//add to sum
				if(map.get(str) != 2){
					sum += map.get(str);
					words_count++;
					System.out.println(str);
				}
			}else{
				//check againt bi word list
				uniflag = 0;
				for(int i=0;i<biwords.size();i++){
					if(biwords.get(i).contains(str)){
						uniflag = 1;
						break;
					}
				}
				if(uniflag == 0){
					if(map.get(str) != 2){
						sum += map.get(str);
						words_count++;
						System.out.println(str);
					}
				}
			}
		}

		
		System.out.println(biwords.toString());
		System.out.println(uniwords.toString());
		if( words_count != 0 ){
		System.out.println(Double.toString(sum/words_count));
		}else{
			System.out.println("2.0");
		}
		System.out.println(map.toString());
		
		 //organize_job3_input();
		// delete_folders();
	}
	
	/*
	 * This method will sum up the sentiment scores
	 */

	private static void sumup(Double score){
		sum += score;
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
