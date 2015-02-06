'''
#  find_recent.py
#  Developed for EMC Interview Screeing test
#  Created by Naveen Mysore on 2/5/15.

#  USAGE : get_recent.py [filepath and name] [filepath and name] ...
#  URL : https://github.com/NAVEENMN/emc_screening
'''
import os
import sys, getopt
import time, datetime

# this function has a dictonary which converts months in word to numeric format
def look_up(data):
	Month_Day = {'Jan' : 1,'Feb' : 2,'Mar' : 3,'Apr' : 4,'May' : 5,'Jun' : 6,
		     'Jul' : 7,'Aug' : 8,'Sep' : 9,'Oct' : 10,'Nov' : 11,'Dec' : 12 
		    }
	return Month_Day[data]
# here in the main function we calculate epochs for all files and find the highesh epoch file
# and that should be the most recent one
def main(argv):
	files_list = list()
	file_epochs = list()
	for x in range(0, len(argv)):
		if os.path.exists(argv[x]):
        		files_list.append(argv[x])
			data =  time.ctime(os.path.getmtime(files_list[x])).split()
			year = int(data[4])
			month = int(look_up(data[1]))
			date = int(data[2])
			raw_time = data[3].split(':')
			hour = int(raw_time[0])
			minute = int(raw_time[1])
			seconds = int(raw_time[2])
        		epoch = datetime.datetime(year,month,date,hour,minute,seconds).strftime('%s')
        		file_epochs.append(epoch)
		else:
			print "file" + str(argv[x]) + "doesnot exists"
    	index  = file_epochs.index(max(file_epochs))
	print "most recent file: "
	print files_list[index]#this recent_file list has the recently used file

if __name__ == "__main__":
	if len(sys.argv) is 1:
		print "Usage: get_recent.py [filepath and name] [filepath and name] ..."
	else:
		main(sys.argv[1:])
