 	import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

public class gen_rank {
	public static int sum = 0;

	public static class Map extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, Text> {
		private Text word = new Text();
		private Text ran = new Text();

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
		conf.setJobName("wordcount");

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
