This is the implementation of Sentiment analysis for the movie reviews by Rotten Tomatoes using MapReduce.
------------------ Input --------------------
Place your input files in txt format in /data directory
------------------ HADOOP DIRECTORY SETUP ----
/user/cloudera/QueryIndex

make sure this path is setup on hdfs before executing these files as python will places the input files present in ~/data directry to hdfs

the QueryIndex directory on hdfs has two directory input and output
ie: 
/user/cloudera/QueryIndex/input
/user/cloudera/QueryIndex/output

make sure /user/cloudera/QueryIndex this path is setup. The setup_dir.sh file in config directory will take care of setting up files into it when your run query_run.py
----------------------------------------------
information about file name convention used:
-------------------
query_run.py: To ease the process of running all the files and arranging input and output I have implemented this python script. It even takes care of setting up files on hdfs.

It moves the files present in input directory to /user/cloudera/QueryIndex/input on hdfs and then it starts to execute MainDriver java class.

Usage: python query_run.py
--------------------
If you wish to execute all files manually then:
sudo javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d Index_classes Index.java PreProcess.java PageRankAnalysis.java SortPage.java MainDriver.java
jar -cvf MainDriver.jar -C Index_classes/ .
sudo -u hdfs hadoop jar MainDriver.jar org.myorg.MainDriver /user/cloudera/QueryIndex/input /user/cloudera/QueryIndex/output
-------------------
