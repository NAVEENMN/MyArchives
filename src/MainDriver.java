import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
		}
		// calculate_score();
		// delete_folders();
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
