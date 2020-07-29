import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright 2010, Mysore Naveen, Bulbule Akash, <a href="http://devdaily.com" title="http://devdaily.com">http://devdaily.com</a>.
 *
 * This software is released under the terms of the
 * GNU LGPL license. See <a href="http://www.gnu.org/licenses/gpl.html" title="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>
 * for more information.
 */

/*
 * This class creates collects the bunch of new words which the user entered in the review and were not found in the train.tsv
 * These new words will be placed in inter.txt in training folder
 */
public class update_train {
   
	public static void main(String original, String new_senti) throws Exception{
		Set<String> new_list = new TreeSet<String>();
		String[] originallist = original.split("-->");
		Set<String> original_list = new TreeSet<String>(Arrays.asList(originallist));
		try(BufferedReader rk = new BufferedReader(new FileReader("/home/nmysore/Documents/pr/sen/postrank/part-00000"))) {
	        String line = rk.readLine();
	        while (line != null) {
	        	String[] t = line.split("\t");
	            String key = t[0];
	            new_list.add(key);
	            line = rk.readLine();
	        }
	    }catch(Exception e){
	    	System.out.println("read error");
	    }
		
		System.out.println(original_list.toString());
		original_list.removeAll(new_list);
		System.out.println(original_list.toString());
		
        try {
          File file = new File("/home/nmysore/Documents/pr/sen/training/inter.txt");
          BufferedWriter output = new BufferedWriter(new FileWriter(file));
          for(String str : original_list){
          output.write(str+"\t"+new_senti+"\n");
          }
          output.close();
        } catch ( IOException e ) {
           e.printStackTrace();
        }
        
        
        	
	}
		
}
