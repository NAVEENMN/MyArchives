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
 * Copyright 2010, Mysore Naveen, Bulbule Akash, <a href="http://devdaily.com"
 * title="http://devdaily.com">http://devdaily.com</a>.
 * 
 * This software is released under the terms of the GNU LGPL license. See <a
 * href="http://www.gnu.org/licenses/gpl.html"
 * title="http://www.gnu.org/licenses/gpl.html"
 * >http://www.gnu.org/licenses/gpl.html</a> for more information.
 */

/*
 * This MapReduce takes care of preparing data for actual sentiment value
 * calculation Input: intermediate folder prerank Outout: intermediate folder
 * postrank
 */
public class gen_rank {
	public static int sum = 0;

	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, Text> {
		private Text word = new Text();
		private Text ran = new Text();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.hadoop.mapred.Mapper#map(java.lang.Object,
		 * java.lang.Object, org.apache.hadoop.mapred.OutputCollector,
		 * org.apache.hadoop.mapred.Reporter) The job of Mapper is simply taking
		 * all the inputs from buckets and emitting them for post analysis
		 */
		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			String[] wor = line.split("\t");
			word.set(wor[0]);
			ran.set(wor[1]);
			output.collect(word, ran);

		}
	}

	/*
	 * The job of reducer is just collecting the emitted keys and values
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

	public static void runjob(String input, String output) throws Exception {
		JobConf conf = new JobConf(gen_rank.class);
		conf.setJobName("Preparing_data");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, new Path(output));

		JobClient.runJob(conf);
	}
}
