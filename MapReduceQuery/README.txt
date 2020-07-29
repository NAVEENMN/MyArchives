This is the implementation of Query Search in a given set of files using Map Reduce.
------------------ HADOOP DIRECTORY SETUP ----
/user/cloudera/QueryIndex

make sure this path is setup on hdfs before executing these files

the QueryIndex directory on hdfs has two directory input and output
ie: 
/user/cloudera/QueryIndex/input
/user/cloudera/QueryIndex/output

make sure /user/cloudera/QueryIndex this path is setup. The setup_dir.sh file in config directory will take care of setting up files into it when your run query_run.py
----------------------------------------------
information about file name convention used:
-------------------
query_run.py: To ease the process of running all the files and arranging input and output I have implemented this python script. It even takes care of setting up files on hdfs.

It asks the user to enter his/her query (Example: this and the ) and it automatically creates query.txt file and it places it in query_input directory. Then is asks user for choice of simple inverted or full inverted index (1 for inverted index and 2 for full inverted index).

It then executes (Index.java+QueryIndex.java) or (FullIndex.java+QueryFullIndex.java) based on your choice.

Usage: python query_run.py
--------------------
If you wish to execute all files manually then:
-------------------
Index.java : This is a Map Reduce Hadoop java implementation which generates simple inverted index for the input files present in input/ directory

Input: input/*
Output: output/part-00000 

Usage: sudo javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d Index_classes Index.java

       jar -cvf Index.jar -C Index_classes/ .
       sudo -u hdfs hadoop jar Index.jar org.myorg.Index /user/cloudera/QueryIndex/input /user/cloudera/QueryIndex/output
       sudo hadoop fs -get /user/cloudera/QueryIndex/output

after executing output directory will have part-00000 file which holds response.
-------------------
QueryIndex.java : This is a java implementation for processing the output generated after map reduce. This implemenation reads data present in query.txt and filters the part-00000 file so the user can see the response to his/her query

Input: output/part-00000
Output: query_output.txt

Usage:
javac QueryIndex.java
java QueryIndex output/part-00000 query_input/query.txt
------------------
FullIndex.java: This is a Map Reduce Hadoop java implementation which generates Full inverted index for the input files present in input/ directory

Input: input/*
Output: output/part-00000

Usage:
sudo javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d Index_classes FullIndex.java
jar -cvf FullIndex.jar -C Index_classes/ .
sudo -u hdfs hadoop jar FullIndex.jar org.myorg.FullIndex /user/cloudera/QueryIndex/input /user/cloudera/QueryIndex/output
sudo hadoop fs -get /user/cloudera/QueryIndex/output
-----------------
QueryFullIndex.java: This is the java implementation for processing the output generated after map reduce. This implementation reads data present in query.txt and filters the part-00000 file so the user can see the response to his/her query

Input: output/part-00000
Output: query_output.txt

Usage:
javac QueryFullIndex.java
java QueryFullIndex query_input/query.txt output/part-00000 query_output.txt
----------------
files in config directory: these shell scripts are used by query_run.py for setting up proper directories while running.
---------------
