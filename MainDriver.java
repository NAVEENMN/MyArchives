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
		
		// This part executes MapReduce tasks for generating base rank and refrences
		JobConf conf = new JobConf(Index.class);
		conf.setJobName("Generate_title_links");
		FileSystem fs = FileSystem.get(conf);
		
		// paths for input, output and intermediate paths
		Path InPath = new Path(args[0]);
		Path OutPath = new Path(args[1]);
		Path lgPath = new Path("/user/cloudera/QueryIndex/output");
		Path prPath = new Path("/user/cloudera/PageRank/output");
		if(fs.exists(OutPath)) fs.delete(OutPath, true); // clean up the contents if this path exists already
		if(fs.exists(lgPath))fs.delete(lgPath, true);
		if(fs.exists(prPath))fs.delete(prPath, true);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Index.Map.class);
		conf.setReducerClass(Index.Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, InPath);
		FileOutputFormat.setOutputPath(conf, lgPath);
		
		// Build the link graph
		System.out.println("\n---------------------------");
		System.out.println( " build link graph ");
		System.out.println("input = " + InPath.toString());
		System.out.println("output = " + OutPath.toString());
		System.out.println( "---------------------------");
		
		JobClient.runJob(conf);

		Path prInputPath = null;
		//This part exceute the Rank Analysis 10 times
		Path prOutputPath = null;
		
		for(int i=1; i<=4; i++){

			// delete previous input path
			if(prInputPath != null) fs.delete(prInputPath, true);
			// set up the paths
			prInputPath = (i == 1) ? lgPath : prOutputPath;
			prOutputPath = new Path(prPath, String.valueOf(i));
			// run page rank
			System.out.println( "---------------------------");
			System.out.println( " iteration "+i);
			System.out.println("input = " + prInputPath.toString());
			System.out.println("output = " + prOutputPath.toString());
			System.out.println( "---------------------------");
			conf = new JobConf(PageRankAnalysis.class);
			conf.setJobName("page_rank_analysis");
			fs = FileSystem.get(conf);
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(Text.class);
			//conf.setMapperClass(PageRank.Map.class);
			conf.setReducerClass(PageRankAnalysis.Reduce.class);
			conf.setMapperClass(PageRankAnalysis.MapText.class);
			conf.setInputFormat(TextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			FileInputFormat.setInputPaths(conf,
			new Path(prInputPath, "part-*"));
			FileOutputFormat.setOutputPath(conf, prOutputPath);
			JobClient.runJob(conf);
		
		}

	}
}
