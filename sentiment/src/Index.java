import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;


/**
 * Copyright 2010, Mysore Naveen, Bulbule Akash, <a href="http://devdaily.com" title="http://devdaily.com">http://devdaily.com</a>.
 *
 * This software is released under the terms of the
 * GNU GPL license. See <a href="http://www.gnu.org/licenses/gpl.html" title="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>
 * for more information
 */

/*
 * Build Buckets
 * Input: train.tsv in input directory 
 * Output: 4 files - intermediate - (0.txt, 1.txt, 2.txt, 3.txt) placed in prerank directory
 *
 */
public class Index {
	public static int bucketid;

	/*
	 * This is a mapper class where each line will be thrown into appropriate
	 * buckets each line is compared against iterator value and will be sent to
	 * reducer
	 */
	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, Text> {
		private Text sentence = new Text();
		private Text sentiment = new Text();
		private String str = null;

		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			str = get_senti(value.toString());
			sentence.set(value.toString());
			sentiment.set("");
			if (str.equals(Integer.toString(bucketid))) {
				output.collect(sentence, sentiment);
			}
		}
	}

	/*
	 * The job of reducer is just collecting
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			if (values.hasNext())
				output.collect(key, values.next());
			else
				throw new IOException("No traces found for" + key.toString());
		    }
	}

	/*
	 * This function gets the sentiment value for a given line input : String
	 * line output : String sentiment value
	 */
	public static String get_senti(String str) {
		String[] columnDetail = new String[11];
		columnDetail = str.split("\t");
		return columnDetail[3];
	}

	/*
	 * This functions takes care of running the jobs It also controls the input
	 * and output paths
	 */
	public void runjob(String inputPath, String OutputPath, int i) {
		JobConf conf = new JobConf(Index.class);
		conf.setJobName("Bucketing");
		bucketid = i;
		// paths for input, output and intermediate paths
		Path RawDataInPath = new Path(inputPath);// Path where train.tsv is stored
		Path IndexOutPath = new Path(OutputPath + Integer.toString(i));// Output it to intermediate folder outputs
		// Set up the classes for jobs
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Index.Map.class);
		conf.setReducerClass(Index.Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		// Set up the paths
		FileInputFormat.setInputPaths(conf, RawDataInPath);// In put is from args[0]
		FileOutputFormat.setOutputPath(conf, IndexOutPath);// In put from arg[1]
		// Build the link graph
		System.out.println("\n---------------------------");
		System.out.println("Creating bucket: " + Integer.toString(i));
		System.out.println("input taken from = " + RawDataInPath.toString());
		System.out.println("output to = " + IndexOutPath.toString());
		System.out.println("---------------------------");
		try {
			JobClient.runJob(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}