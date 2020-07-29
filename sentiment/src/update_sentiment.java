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
 * href="http://www.gnu.org/licenses/lgpl.html"
 * title="http://www.gnu.org/licenses/gpl.html"
 * >http://www.gnu.org/licenses/gpl.html</a> for more information.
 */

/*
 * This MapReduce task implements the learning technique for new words which enter in the inter.txt
 * for each new words entered in the train.txt we compare againts exisiting words in pool.txt
 * if this new word (word not in train.tsv) is encounterd and its present in pool.txt we find the average of old score and
 * current score. If this word is not present in pool.txt we just append it to the pool.txt
 * Over time the new words will learn new sentiment values.
 */
public class update_sentiment {
	public static int sum = 0;

	/*
	 * Mapper takes the words from both pool.txt and inter.txt and emits them
	 */
	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, Text> {
		private Text word = new Text();
		private Text ran = new Text();

		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			String[] wor = line.split("\t");
			word.set(wor[0]);// word
			ran.set(wor[1]);// sent

			output.collect(word, ran);

		}
	}

	/*
	 * Reducer collects the key values.
	 * If the keys are found across both inter.txt and pool.txt find the average and collect key and new sentiment value
	 * else just collect the key and current sentiment value
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			double count = 0;
			double rank = 0;
			while (values.hasNext()) {
				rank += Double.parseDouble(values.next().toString());
				count += 1;
			}

			output.collect(key, new Text(Double.toString(rank / count)));
		}

	}

	public static void runjob(String input, String output) throws Exception {
		JobConf conf = new JobConf(update_sentiment.class);
		conf.setJobName("Update_Sentiment_Train");

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
