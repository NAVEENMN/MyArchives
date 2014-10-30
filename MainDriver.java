package org.myorg;
import org.myorg.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

/*
 * 	This a Driver Class which controls all the map reduce operations
 */

public class MainDriver {
		
		public static float d = 0.85f;
		public static void main(String[] args) throws Exception{
		//---------------   <Title><Rank> Graph Creation ---------------------------
		
		// This part executes MapReduce tasks for generating base rank and refrences
		JobConf conf = new JobConf(Index.class);
		conf.setJobName("Generate_title_links");
		FileSystem fs = FileSystem.get(conf);
		
		// paths for input, output and intermediate paths
		Path RawDataInPath = new Path(args[0]);// Path where the orignal raw data is stored
		Path IndexOutPath = new Path(args[1]);// Output the result to ~/QueryIndex/output
		Path GraphPath = new Path("/user/cloudera/QueryIndex/output");// for Graph generation OutPath becomes input here
		Path PageRankPath = new Path("/user/cloudera/PageRank/output");
		
		// clean up the contents for these paths
		if(fs.exists(IndexOutPath)) fs.delete(IndexOutPath, true);
		if(fs.exists(GraphPath))fs.delete(GraphPath, true);
		if(fs.exists(PageRankPath))fs.delete(PageRankPath, true);
		
		//Set up the classes for jobs
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Index.Map.class);
		conf.setReducerClass(Index.Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		// Set up the paths
		FileInputFormat.setInputPaths(conf, RawDataInPath);// In put is from args[0]
		FileOutputFormat.setOutputPath(conf, GraphPath);
		
		// Build the link graph
		System.out.println("\n---------------------------");
		System.out.println( " Generating <Title><Rank> Graph ");
		System.out.println("input taken from = " + RawDataInPath.toString());
		System.out.println("output to = " + IndexOutPath.toString());
		System.out.println( "---------------------------");
		
		JobClient.runJob(conf);


		//-------------  Page Rank Analysis   ----------------------------------
		
		//These are intermidiate paths used as input/output within the loop
		Path TempInputPath = null;
		Path TempOutputPath = null;
		
		for(int i=1; i<=4; i++){

			// delete previous input path
			if(TempInputPath != null) fs.delete(TempInputPath, true);
			/*
			 * For the first time Graph path as input
			 * after first loop get the intermediate output as input.
			 */
			TempInputPath = (i == 1) ? GraphPath : TempOutputPath;
			TempOutputPath = new Path(PageRankPath, String.valueOf(i));
			// run page rank
			System.out.println( "---------------------------");
			System.out.println( "Running PageRank analysis # "+i);
			System.out.println( "input taken from = " + TempInputPath.toString());
			System.out.println( "output to = " + TempOutputPath.toString());
			System.out.println( "---------------------------");
			//Setup job
			conf = new JobConf(PageRankAnalysis.class);
			conf.setJobName("page_rank_analysis");
			fs = FileSystem.get(conf);
			// Setup classed for jpbs
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(Text.class);
			conf.setReducerClass(PageRankAnalysis.Reduce.class);
			conf.setMapperClass(PageRankAnalysis.Map.class);
			conf.setInputFormat(TextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			// Setup paths
			FileInputFormat.setInputPaths(conf, new Path(TempInputPath, "part-*"));
			FileOutputFormat.setOutputPath(conf, TempOutputPath);
			// Run the job
			JobClient.runJob(conf);
		
		}

	}
}
