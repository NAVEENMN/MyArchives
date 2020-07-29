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
		/*
		 * This is the main function for Driver class
		 * input : arg[0] : input path
		 *	   arg[1] : output path
		 */
		public static void main(String[] args) throws Exception{
			Index job1 = new Index();
			for(int i = 0; i <= 4;i++ ){
				// This part executes MapReduce tasks for generating base rank and refrences
				job1.runjob(args[0], args[1], i);
		
			}
		}
}
