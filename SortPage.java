package org.myorg;
import org.myorg.MainDriver;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/*
 * SortPage.java will sort all title based on their ranks
 */
public class SortPage
{
	public static final float fact = 100000.0f; 
	public static final int flipper = 1000000000; // this fillper is used to order the utputs while sorting in acsending order
	
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
		private Text rank = new Text();
		private Text title = new Text();
		public void map(LongWritable offset, Text value,OutputCollector<Text, Text> output,Reporter reporter)throws IOException{
			/*
			 * input is of form title	rank||numberoflinks||link1||..||linkn
			 */
			String[] val = value.toString().split("\t");// split across the tab and fetch the title
			title.set(val[0]);// make this as value
			int split = val[1].indexOf("||");//split across || on the other part
			float r = new Float(val[1].substring(0, split));
			int rnk = flipper - (int)(fact * r);//fetch the rank and multiply 
			rank.set(String.valueOf(rnk));
			output.collect(rank, title);//emit rank and title
		}
	}
	// reducer converts rank back to normal value and formats it
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
		private Text value = new Text();
		public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output,Reporter report) throws IOException{
			while(values.hasNext()){
				float rk = (float)(flipper -new Integer(key.toString()))/fact;
				value.set(String.valueOf(rk));
				output.collect(value, values.next());
			}
		}
	}
}
