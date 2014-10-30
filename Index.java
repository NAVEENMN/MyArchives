package org.myorg;

import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 *	Build link graphs
 *	Output: Title, rank - links 
 * 	Each page is initialized to 1-d with d = 0.85
 */

public class Index{
	// Mapper emits TItle, rand||links
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		private Text Title = new Text();
		private Text Refrences = new Text();
		String temp_title;
		String temp_ref;
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			String line = value.toString();
			temp_title = extract_title(line);
			temp_ref = extract_refrences(line);
			if(temp_title == null) return; // ignore empty titles
			Title.set(temp_title);
			Refrences.set("0.15"+temp_ref.toString());
			output.collect(Title, Refrences);
		}	
	}

	
	//Reducer just collects
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			if(values.hasNext()) output.collect(key, values.next());
			else throw new IOException("No links found for" + key.toString());
		}
	}

	public static String extract_title(String str) {               
        	final Pattern pattern = Pattern.compile("<title>(.+?)</title>");
        	final Matcher matcher = pattern.matcher(str);
       	 	matcher.find();
	    	return matcher.group(1);
	}
	
	public static String extract_refrences(String line){ 
		
		String result = line.substring(line.indexOf("<text")+26,line.indexOf("</text>"));
		int number_of_links = 0;
		Matcher matcher = Pattern.compile("\\[([^\\]]+)").matcher(result);

	    	ArrayList<String> tags = new ArrayList<>();

	    	int pos = -1;
	    	while (matcher.find(pos+1)){
	        	pos = matcher.start();
	        	tags.add(matcher.group(1).replace("[", "").replace("]", ""));
	   	 }
		Set<String> list_non_duplicate = new HashSet<String>(tags);
		number_of_links = list_non_duplicate.size();
		return("||"+Integer.toString(number_of_links)+"||"+list_non_duplicate.toString());
	}

}
