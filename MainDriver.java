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
		if(fs.exists(OutPath)) fs.delete(OutPath, true); // clean up the contents if this path exists already
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Index.Map.class);
		conf.setReducerClass(Index.Reduce.class);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, InPath);
		FileOutputFormat.setOutputPath(conf, OutPath);
		
		// Build the link graph
		System.out.println("\n---------------------------");
		System.out.println( " build link graph ");
		System.out.println("input = " + InPath.toString());
		System.out.println("output = " + OutPath.toString());
		System.out.println( "---------------------------");
		
		JobClient.runJob(conf);
	}

}

