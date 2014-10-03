package org.myorg;

import java.io.IOException;
import java.util.*;
import java.io.*;
import java.net.URI;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryIndex {

  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    private Text word = new Text();
    private Text file_name = new Text();
   // Configuration conf = new Configuration();
    //FileSystem fsa = FileSystem.get(conf);
    //Path pt = new Path("hdfs://localhost:80080/user/cloudera/QueryInput/input/query.txt");
    //try{
    //BufferedReader br = new BufferedReader(new InputStreamReader(fsa.open(pt)));}catch(IOException e){
//	e.printStacktrace();
//}
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      Log log = LogFactory.getLog(QueryIndex.class);
      String line = value.toString();
      StringTokenizer tokenizer = new StringTokenizer(line);
      FileSplit fs = (FileSplit)reporter.getInputSplit();
      // String line;
      // line=br.readLine();
      // while (line != null){
      // 	System.out.println(line);
      //  line=br.readLine();
      // }
      String fn = fs.getPath().getName();		
      while (tokenizer.hasMoreTokens()) {
	String tok = tokenizer.nextToken();
	word.set(tok);
	if (tok.equals("yellow")){
	file_name.set(fn);
	System.out.println("hey");	
	output.collect(word, file_name);
	log.info("Hello world");	
	}
      }
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
    private Text list_of_file_names = new Text();
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      String file_names = "";
      StringBuilder sb = new StringBuilder();
      String final_list = "";
      while (values.hasNext()) {
        file_names += values.next().toString();
      }
      Set<String> file_names_non_duplicate = new HashSet<String>(Arrays.asList(file_names.split(" ")));
      for(String s : file_names_non_duplicate){

      if(s != null && !s.isEmpty())sb.append(s+" ");}
      
      final_list = sb.toString();
      list_of_file_names.set(final_list);
      output.collect(key, list_of_file_names);
    }
  }

  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(QueryIndex.class);
    conf.setJobName("Query_Index");
    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);

    conf.setMapperClass(Map.class);
    conf.setCombinerClass(Reduce.class);
    conf.setReducerClass(Reduce.class);

    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));

    JobClient.runJob(conf);
  }
}
