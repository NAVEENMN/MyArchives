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

/*
 * Review processing
 * Output: Sentiment value
 *
 */
public class process {
	public static int bucketid;
	public static String testline;

	/*
	 * This is a mapper class where each line will to thrown into appropriate
	 * buckets Each line is compared againts iterator value and will be sent to
	 * reducer
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
			// sentiment.set(Integer.toString(bucketid));
			sentiment.set(str1);
			// if (str.equals(Integer.toString(bucketid))){
			// System.out.println("hello");
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
	 * This functions takes care of returning the input file line to compare
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
		// conf.set("bucketid",Integer.toString(i));
		// paths for input, output and intermediate paths
		testline = review;
		Path RawDataInPath = new Path(inputPath);// Path where the orignal raw
		// data is stored
		Path IndexOutPath = new Path(OutputPath + Integer.toString(i));// Output
		// Set up the classes for jobs
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		// conf.set("bucketid", Integer.toString(i));
		// Set up the paths
		FileInputFormat.setInputPaths(conf, RawDataInPath);// In put is from
		// args[0]
		FileOutputFormat.setOutputPath(conf, IndexOutPath);// In put from arg[1]
		// Build the link graph
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
