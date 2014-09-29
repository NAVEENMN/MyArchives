sudo rm -rf wordcount_classes
mkdir wordcount_classes
rm wordcount.jar
sudo -u hdfs hadoop fs -rm -r /user/cloudera/wordcount/output
