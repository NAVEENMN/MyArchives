package org.myorg;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Index {

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		private Text Title = new Text();
		private Text Refrences = new Text();
		String temp_title;
		ArrayList<String> temp_ref;
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			String line = value.toString();
			temp_title = extract_title(line);
			temp_ref = extract_refrences(line);
			Title.set(temp_title);
			Refrences.set(temp_ref.toString());
			output.collect(Title, Refrences);
		}	
	}

	

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		private Text list_of_file_names = new Text();
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			String par_values = "";
			String ref_names = "";
			String final_list = "";
			while (values.hasNext()) {
				par_values = values.next().toString();
				ref_names += par_values;
			}
			final_list = ref_names;
			list_of_file_names.set(final_list);
			output.collect(key, list_of_file_names);
		}
	}

	public static String extract_title(String str) {               
        	final Pattern pattern = Pattern.compile("<title>(.+?)</title>");
        	final Matcher matcher = pattern.matcher(str);
       	 	matcher.find();
	    	return matcher.group(1);
	}
	
	public static ArrayList<String> extract_refrences(String line){ 
		
		String result = line.substring(line.indexOf("<text")+26,line.indexOf("</text>"));
		
		Matcher matcher = Pattern.compile("\\[([^\\]]+)").matcher(result);

	    	ArrayList<String> tags = new ArrayList<>();

	    	int pos = -1;
	    	while (matcher.find(pos+1)){
	        	pos = matcher.start();
	        	tags.add(matcher.group(1).replace("[", "").replace("]", ""));
	   	 }
		return(tags);
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(Index.class);
		conf.setJobName("Generate_Index");
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf);
	}
}
