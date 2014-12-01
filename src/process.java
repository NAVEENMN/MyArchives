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
 * GNU LGPL license. See <a href="http://www.gnu.org/licenses/gpl.html" title="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>
 * for more information.
 */

/*
 * Review processing
 * Output: Intermediate file for sentiment value calculation
 */
public class process {
	public static int bucketid;
	public static String testline;

	/*
	 * This is a mapper class where each line from different buckets will be considered
	 * and compared against the processed user review file which is the ouput of python script
	 * for every uni and bi words from the user review we emit the word and its sentiment value
	 */
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		private Text sentence = new Text();
		private Text sentiment = new Text();
		private String str = null, str1 = null;

		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			str = get_trainline(value.toString());
			str1 = Index.get_senti(value.toString());
			String[] test_split_line = testline.split("-->");
			sentence.set(str);
			sentiment.set(str1);
			for (String testwrd : test_split_line) {
				if (str.toLowerCase().equals(testwrd.toLowerCase())) {
					output.collect(sentence, sentiment);
				}
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
				throw new IOException("No links found for" + key.toString());
		}
	}

	/*
	 * This functions takes care of returning the actual sentence and removing id`s associated in the line
	 * with the word.
	 */
	public static String get_trainline(String str) {
		String[] columnDetail = new String[11];
		columnDetail = str.split("\t");
		return columnDetail[2];
	}

	/*
	 * This functions takes care of running the jobs It also controls the input
	 * and output paths
	 */
	public void runjob(String inputPath, String OutputPath, String review, int i) {
		JobConf conf = new JobConf(process.class);
		conf.setJobName("Bucket_processing");
		testline = review;
		Path RawDataInPath = new Path(inputPath);// Path where the processed ouputs are present
		Path IndexOutPath = new Path(OutputPath + Integer.toString(i));// Output it to prerank folder which is intermediate
		// Set up the classes for jobs
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		FileInputFormat.setInputPaths(conf, RawDataInPath);
		FileOutputFormat.setOutputPath(conf, IndexOutPath);
		System.out.println("\n---------------------------");
		System.out.println("Proceessing bucket: ");
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
