sudo -u hdfs hadoop fs -rm -rf /user/cloudera/QueryIndex/*
sudo -u hdfs hadoop fs -mkdir /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -put output/* /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -put query_input/* /user/cloudera/QueryIndex/input
sudo -u hdfs hadoop fs -rm -r /user/cloudera/QueryIndex/output*
