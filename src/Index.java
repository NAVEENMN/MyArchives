import java.io.IOException;
import java.nio.file.FileSystem;
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
 * Build Buckets
 * Output: 4 files
 *
 */
public class Index {
	public static int bucketid;

	/*
	 * This is a mapper class where each line will to thrown into appropriate
	 * buckets Each line is compared againts iterator value and will be sent to
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
				throw new IOException("No links found for" + key.toString());
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
		// conf.set("bucketid",Integer.toString(i));
		bucketid = i;
		// paths for input, output and intermediate paths
		Path RawDataInPath = new Path(inputPath);// Path where the orignal raw
													// data is stored
		Path IndexOutPath = new Path(OutputPath + Integer.toString(i));// Output
																		// the
																		// result
																		// to
																		// ~/QueryIndex/output
		// Path IntrPath = new Path("/user/cloudera/QueryIndex/output");// Use
		// this for intermediate paths
		// Path INTR1Path = new Path("/user/cloudera/PageRank/output");
		// Path TempOutputPath = new Path(OutputPath+ Integer.toString(i),
		// String.valueOf(i));
		// clean up the contents for these paths
		// if(fs.exists(IndexOutPath)) fs.delete(IndexOutPath, true);
		// if(fs.exists(IntrPath))fs.delete(GraphPath, true);
		// if(fs.exists(INTR1Path))fs.delete(PageRankPath, true);
		// Set up the classes for jobs
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Index.Map.class);
		conf.setReducerClass(Index.Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		// conf.set("bucketid", Integer.toString(i));
		// Set up the paths
		FileInputFormat.setInputPaths(conf, RawDataInPath);// In put is from args[0]
		FileOutputFormat.setOutputPath(conf, IndexOutPath);// In put from arg[1]
		// Build the link graph
		System.out.println("\n---------------------------");
		System.out.println(" Creating bucket: " + Integer.toString(i));
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