package org.myorg;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
public class Index {
public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
private Text word = new Text();
private Text file_name = new Text();
public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
String line = value.toString();
StringTokenizer tokenizer = new StringTokenizer(line);
FileSplit fs = (FileSplit)reporter.getInputSplit();
String fn = fs.getPath().getName();
while (tokenizer.hasMoreTokens()) {
word.set(tokenizer.nextToken());
file_name.set(fn+", ");
output.collect(word, file_name);
}
}
}
public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
private Text list_of_file_names = new Text();
public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
String file_names = "";
String par_values = "";
StringBuilder sb = new StringBuilder();
String final_list = "";
while (values.hasNext()) {
par_values = values.next().toString();
file_names += par_values;
}
Set<String> file_names_non_duplicate = new HashSet<String>(Arrays.asList(file_names.split(" ")));
for(String s : file_names_non_duplicate){
if(s != null && !s.isEmpty())sb.append(s+" ");
}
final_list = sb.toString();
list_of_file_names.set(final_list);
output.collect(key, list_of_file_names);
}
}
public static void main(String[] args) throws Exception {
JobConf conf = new JobConf(Index.class);
conf.setJobName("Generate_Index");
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
