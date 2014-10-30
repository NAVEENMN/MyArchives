package org.myorg;

import java.io.*;
import java.util.*;
import org.myorg.PreProcess;
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
		private Text title = new Text();
		private Text codedLinks = new Text();
		private PreProcess page = new PreProcess(null);
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output,Reporter reporter)throws IOException{
			page.setPage(value.toString());
			// ignore empty titles and special pages with ':' in title
			if(page.getTitle() == null) return;
			if(page.getTitle().contains(":")) return;
			title.set(page.getTitle());
			codedLinks.set("1.0||" + page.getCodedLinks());
			output.collect(title, codedLinks);
		}	
	}

	
	//Reducer just collects
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			if(values.hasNext()) output.collect(key, values.next());
			else throw new IOException("No links found for" + key.toString());
		}
	}
	

}
