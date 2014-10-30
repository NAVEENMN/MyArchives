package org.myorg;
import org.myorg.MainDriver;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/*
 * page rank algorithm
 */
public class PageRankAnalysis{
	// mapper will emit key value pair,
	// KV1: for each link, the rank contribution from page being processed:(TITLE, RANK_CONTRIBUTION)
	// KV2: for a given title links :(Title, "##links##||links")
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
		private String RNL;//rank-numberoflinks-links
		private Text link = new Text();
		private Text TitleRank = new Text();
		private Text RNLText = new Text();
		private float rank, count;
		public void map(LongWritable offset, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException{
			Text key = new Text();
			// Input is of form <title>	rank||numberoflinks||link1||link2||...||linkn||
			String[] val = value.toString().split("\t");// split across the tab space to get two components
			key.set(val[0]);// has the title
			value.set(val[1]);// has the rank-numberoflinks-links
			RNL = value.toString();// has the rank-numberoflinks-links
			int group = RNL.indexOf("||");
			rank = new Float(RNL.substring(0, group));
			RNL = RNL.substring(group+2);// now it has numeroflinks||link1||...||linkn
			RNLText.set("##links##" + "||" + RNL);//##links## is a token used to recogize the links
			output.collect(key, RNLText);// emit KV1
			group = RNL.indexOf("||");
			// if group < 0 then it has no titles so ignore them
			if(group >= 0){
				count = new Float(RNL.substring(0, group));
				RNL = RNL.substring(group+2);// RNL now = link1||...||linkN
				String[] links = RNL.split("\\|\\|");
				for(String l : links){
					link.set(l);
					TitleRank.set(String.valueOf(rank/count));
					output.collect(link, TitleRank);
				}
			}
		}
	}
	// reducer outputs (title	rank-and-links )
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
		private Text outRNLText = new Text();
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException{
			String Rank_Links = "0";
			float rank = 1 - MainDriver.d;
			while(values.hasNext()){
				String inRNL = values.next().toString();
				int split = inRNL.indexOf("||");
				if(split < 0){// this input just has the rank contribution
					rank += MainDriver.d * new Float(inRNL);
				}else{// this input has the rank-#oflinks- links
					Rank_Links = inRNL.substring(split + 2);
				}
			}
			// output the rank and coded links
			outRNLText.set(String.valueOf(rank) + "||" + Rank_Links);
			output.collect(key, outRNLText);
		}
	}

}
