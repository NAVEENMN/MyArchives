sudo -u hdfs hadoop fs -rm -r /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -mkdir /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -put input/* /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -rm -r /user/cloudera/QueryIndex/output
