import os

def main():
	print "\n\n"
	print "**** Welome to inverted/full inverted query on hadoop  ****"
	print "\n\n"
	query = raw_input("Please enter your query text:  ")
	query_terms = query.split(" ")
	os.system("sudo rm query_input/query.txt")
	f = open('query_input/query.txt','w')
	for x in range(0,len(query_terms)):
		f.write(query_terms[x]+"\n")
	f.close()
	print "\n"
	print "Setting up hdfs for input and output ....\n"
	os.system("sh config/setup_dir.sh")
	print "\nhdfs input/output is now setup."
	print "\nSetting up compliation envirornment..."
	os.system("sh config/com_setup.sh\n")
	print "\ncompling Index.java..\n"
	os.system("sudo javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d Index_classes Index.java")
	os.system("jar -cvf Index.jar -C Index_classes/ .")
	print "\nJar created.."
	print "\nExecuting jar on Hadoop...\n\n"
	os.system("sudo -u hdfs hadoop jar Index.jar org.myorg.Index /user/cloudera/QueryIndex/input /user/cloudera/QueryIndex/output")
	print "\n\n getting the response.."
	os.system("sudo rm -rf output")
	os.system("sudo mkdir output")
	os.system("sudo hadoop fs -get /user/cloudera/QueryIndex/output")
	print "\n now executing QueryIndex.java..."
	os.system("javac QueryIndex.java");
	os.system("java QueryIndex");
if __name__ == "__main__":
	main()
