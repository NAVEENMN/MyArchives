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

/**
 * Copyright 2010, Mysore Naveen, Bulbule Akash, <a href="http://devdaily.com" title="http://devdaily.com">http://devdaily.com</a>.
 *
 * This software is released under the terms of the
 * GNU LGPL license. See <a href="http://www.gnu.org/licenses/gpl.html" title="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>
 * for more information.
 */

/*
 * This a Main Driver Class which controls all the map reduce operations
 */
public class MainDriver {
	public static Double sum = 0.0;
	public static int uniflag = 0;
	public static Double words_count = 0.0;
	public static Double sentiment_value = 0.0;
	public static String userReview = null;
	/*
	 * This is the main function for Driver class input : arg[0] : input path
	 * 													  arg[1] : output path
	 * 													  arg[2] : output post path
	 * 													  arg[3] : review.txt path
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<String> biwords = new ArrayList<String>();
		ArrayList<String> uniwords = new ArrayList<String>();
		ArrayList<String> wordsused = new ArrayList<String>();
		Map<String, Double> map = new HashMap<String, Double>();
		Writer review_input = null;
		Process p;
		//  prompt the user to enter their review
	    System.out.print("Enter your review: ");
	 
	    //  open up standard input
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	    try {
	         userReview = br.readLine();
	    } catch (IOException ioe) {
	         System.out.println("IO error trying to read your review!");
	         System.exit(1);
	    }
	 
	    System.out.println("review, " + userReview);
		try {
			review_input = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("review.txt"), "utf-8"));
			review_input.write(userReview);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {review_input.close();} catch (Exception ex) {}
		}
		/*
		 * Run the python script pre_process_review.py which used nltp library to pre process the text and create uni grams and bi grams
		 */
		p = Runtime.getRuntime().exec("python /home/nmysore/Documents/pr/sen/pre_process_review.py");
		try {
		    //thread to sleep for the specified number of milliseconds to complete execution of python script
		    Thread.sleep(2000);
		} catch ( java.lang.InterruptedException ie) {
		    System.out.println(ie);
		}
		/*
		 * Initiate the Bucketing Map Reduce job
		 */
		Index job1 = new Index();
		/*
		 * Initiate the Bucket processing Map Reduce task
		 */
		process job2 = new process();
		
		String review;
		review = read_review(args[3]);
		/*
		 * After processing the buckets rearrange the input and output folders for sentiment analysis Map Reduce task
		 */
		for (int i = 0; i <= 4; i++) {
			job1.runjob(args[0], args[1], i);//Bucketing for bucket i
			job2.runjob(args[1] + Integer.toString(i), args[2], review, i);//Processing for bucket i
			p = Runtime.getRuntime().exec("cp /home/nmysore/Documents/pr/sen/outputs/output_post"+Integer.toString(i)+"/part-00000"+ " /home/nmysore/Documents/pr/sen/prerank/"+Integer.toString(i)+".txt");
			p = Runtime.getRuntime().exec("rm -rf /home/nmysore/Documents/pr/sen/outputs");
		}
		/*
		 * Initiate the sentiment analysis
		 */
		gen_rank job3 = new gen_rank();
		job3.runjob("/home/nmysore/Documents/pr/sen/prerank","/home/nmysore/Documents/pr/sen/postrank");
		
		/*
		 * Read the analysed file for sentiment calculation.
		 * If a unigram word is present in bi word then bi word is considered trashing the uni word
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
	            }
	            line = rk.readLine();
	        }
	    }
		/*
		 * By the end of the previous loop we will have a hash map set which words as key and double sentiment as values
		 * Keep track of number of words that contribute to sentiment and also find the sum of sentiments
		 */
		for(String str : map.keySet()){
			if(str.contains(" ")){
				//add to sum
				if(map.get(str) != 2){
					sum += map.get(str);
					wordsused.add(str);
					words_count++;
					//System.out.println(str);
				}
			}else{
				//check against bi word list
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
						wordsused.add(str);
						words_count++;
						//System.out.println(str);
					}
				}
			}
		}

		//System.out.println(biwords.toString());
		//System.out.println(uniwords.toString());
		if( words_count != 0 ){
			sentiment_value = sum/words_count;
		}else{
			sentiment_value = 2.0;
		}
		
		/*
		 * Initiate the training Class
		 */
		update_train ud = new update_train();
		ud.main(review, Double.toString(sentiment_value));
		/*
		 * Initiate the training sentiment value update MapReduce Task
		 */
		update_sentiment ss = new update_sentiment();
        ss.runjob("/home/nmysore/Documents/pr/sen/training/", "/home/nmysore/Documents/pr/sen/test/");
        /*
         * Post processing and setting up input output for next sentiment analysis
         */
        p = Runtime.getRuntime().exec("rm  /home/nmysore/Documents/pr/sen/training/pool.txt");
        p = Runtime.getRuntime().exec("cp  /home/nmysore/Documents/pr/sen/test/part-00000 /home/nmysore/Documents/pr/sen/training/pool.txt");
        p = Runtime.getRuntime().exec("rm  -rf /home/nmysore/Documents/pr/sen/postrank");
        p = Runtime.getRuntime().exec("rm  -rf /home/nmysore/Documents/pr/sen/test");
        
        print_report(map.toString(), biwords.toString(), uniwords.toString(), wordsused.toString());
	}
	
	/*
	 * This method prints the report
	 */
	 private static void print_report(String word_contri, String bi_words, String uni_words, String words_used){
		
		System.out.println("------------------------------");
		System.out.println("----- Sentiment Analysis -----");
		System.out.println("Scale: 0-very negative, 1-negative, 2-neutral, 3-positive, 4-very positive");
		System.out.println("Review: " + userReview);
		System.out.println("Sentiment Value: " + Double.toString(sentiment_value));
		System.out.println("------------------------------");
		System.out.println("Words and their contribution: " + word_contri);
		System.out.println("Bi grams involved: " + bi_words);
		System.out.println("Uni grams involved: " + uni_words);
		System.out.println("words contributed: " + words_used);
		System.out.println("------------------------------");
		
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
}
