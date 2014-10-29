for x in `cd /etc/init.d;ls hadoop-hdfs-*`;do sudo service $x stop;done
