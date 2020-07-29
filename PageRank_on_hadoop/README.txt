This is the implementation of Page Ranking Algorithm  using Map Reduce.
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
MainDriver.java : This is the Main Driver class which executes Index.java, PageRankAnalysis.java(10 times), SortPage.java. It also takes care of setting up intermediate paths for input and outputs.

Input: input/*
Output: temporary folder (refer MainDriver.java)
-------------------
PreProcess.java: This class holds all the necessary methods to clean up the input. The input found in ~/data directory is from wikipedia and each line had tags like <title></title> [[]] etc which holds the informations.
The methods from this class will clean all unecssary information and fetches the desired informations which are title, rank and links.
-------------------
Index.java : This is a Map Reduce Hadoop java implementation which generated the Graph for rank calculation. It used the methods implemented in PreProcess to fecth the required information and generated a encoded line in this format.

Title1	rank||numberoflinks||link1||link2||.....||linkn
Title2	rank||numberoflinks||link1||link2||.....||linkn
.
.
.

The initial rank is set to (1-d) where d is 0.85 which is a damping factor.

Input: temporary folder
Output: temporary folder (refer MainDriver.java)
-------------------
PageRankAnalysis.java : This is a Map Reduce Hadoop java implementation which calculated rank over multiple iterations. Iterations are controlled by MainDriver.java

The core implementation here is the PageRank algorithm formula which is PR(T) = (1-d)+sigma(pr(t))|for i=0 to n ; where t is all titles connected to title T

Based on the coded format which we developed in Index.java we use tab as a token to extarct the tile and || as a token to extract rank, number of links and links

The mapper implemenatation here emits two set of key value pair
KV1: for each link, the rank contribution from page being processed:(TITLE, RANK_CONTRIBUTION)
KV2: for a given title links :(Title, "##links##||links")

The reducer based on the size of KV it recives it recognizes the type of KV and does rank contribution analysis or link generations.
---------------
SortPage.java : This is a Map Reduce Hadoop java implementation which sorts all the titles based on thier ranks, The map emits rank, title pair by proper float manuplication and the reducer just collects and orders them.
----------------
files in config directory: these shell scripts are used by query_run.py for setting up proper directories while running.
---------------


WRITEUP:
--------------

1) How much time did this lab take you? What was the challeing component?
A - It took me almost a week to get this thing running. The whole lab was challenging. I first started with Index.java as it was straight forward. I was able to repesent the data in a format which I wanted then I moved to MainDriver class which just ran Index.java. When I moved to PagaRankAnalysis
the jobs started to crash because of improper data extactions. I had to try out few formatting to and properly extarct rank, tile and links. Few links had embedded links and tokens like "|" and "tab" which geneated imporper splits and was crashing the tasks. PreProcess cleaned up this mess.
Next challenging component was rank calculation itself. I was emitting a key values with few logical errors and this took some time to figue out. Sorting was straight forward but it used generate in decesending order. I implmented flipper method to overcome this.

2) What are the 10 pages with highest PageRank? Does that surprise you? Why/why not? {input data source: wiki-micro.txt as I did not find the full dataset on project website}
A - Top 10
0.89784	United States
0.83265	The Real World (Miami)
0.57509	Canada
0.56654	England
0.56352	Norway
0.53249	Statutory Instrument
0.53249	October 2004
0.53249	Meiosis
0.42875	BMW Car Club of America
0.42377	Netherlands

This dint surprise me as its a clear indication that United States is well connected on Graph, But what did surprise me is Norway taking over Germany!!

3)  List any extensions you performed, assumptions you made about the nature of the problem,
A- I couldn`t implement an extensions on this. I have assumed token with intental refrences(link) in  like title:link or [title[link]] have been ignored as they had no significant contribution and preprocessing was made easy  

