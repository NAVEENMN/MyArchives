'''
#  find_recent.py
#  Developed for EMC Interview Screeing test
#  Created by Naveen Mysore on 2/5/15.

#  USAGE : get_recent.py [filepath and name] [filepath and name] ...
#  URL : https://github.com/NAVEENMN/emc_screening
'''

import os, time
import sys, getopt

# this node structure holds all information about the file
class node:
	def __init__(self, file_name, year, month, date, hour, minute, seconds):
		self.file_name = file_name
		self.year = year
		self.month = month
		self.date = date
		self.hour = hour
		self.minute = minute
		self.seconds = seconds

# this function has a dictonary which converts months in word to numeric format
def look_up(data):
	Month_Day = {'Jan' : 1,'Feb' : 2,'Mar' : 3,'Apr' : 4,'May' : 5,'Jun' : 6,
		     'Jul' : 7,'Aug' : 8,'Sep' : 9,'Oct' : 10,'Nov' : 11,'Dec' : 12 
		    }
	return Month_Day[data]

# this funtion finds the most recently used file
def get_recent_file(file_list):
	if len(file_list) is 1 :
		return file_list
	#---------------------------
    	#finding the most recent year
	recent_year = 0
	for x in range(0, len(file_list)):
		if file_list[x].year > recent_year:
			recent_year = file_list[x].year
	#Knocking out all files which are not recent with refrence to recent year
	temp_list = list(file_list)
	for x in range(0, len(file_list)):
		if file_list[x].year < recent_year:
			temp_list.remove(file_list[x])
	file_list = list(temp_list)
	if len(file_list) is 1 :
		return file_list
	#---------------------------
    	#finding the most recent month
	recent_month = 0
	for x in range(0, len(file_list)):
		if file_list[x].month > recent_month:
			recent_month = file_list[x].month
	#Knocking out all files which are not recent with refrence to recent year
	temp_list = list(file_list)
	for x in range(0, len(file_list)):
		if file_list[x].month < recent_month:
			temp_list.remove(file_list[x])
	file_list = list(temp_list)
	if len(file_list) is 1 :
		return file_list
	#---------------------------
	#finding the most recent date
	recent_date = 0
    	for x in range(0, len(file_list)):
        	if file_list[x].date > recent_date:
            		recent_date = file_list[x].date
    	#Knocking out all files which are not recent with refrence to recent year
    	temp_list = list(file_list)
    	for x in range(0, len(file_list)):
        	if file_list[x].date < recent_date:
            		temp_list.remove(file_list[x])
    	file_list = list(temp_list)
	if len(file_list) is 1 :
		return file_list
	#---------------------------
    	#finding the most recent hour
    	recent_hour = 0
    	for x in range(0, len(file_list)):
        	if file_list[x].hour > recent_hour:
            		recent_hour = file_list[x].hour
    	#Knocking out all files which are not recent with refrence to recent hour
    	temp_list = list(file_list)
    	for x in range(0, len(file_list)):
        	if file_list[x].hour < recent_hour:
            		temp_list.remove(file_list[x])
    	file_list = list(temp_list)
	if len(file_list) is 1 :
		return file_list
	#-----------------------------
    	#finding the most recent minute
    	recent_minute = 0
    	for x in range(0, len(file_list)):
        	if file_list[x].minute > recent_minute:
            		recent_minute = file_list[x].minute
    	#Knocking out all files which are not recent with refrence to recent minute
    	temp_list = list(file_list)
    	for x in range(0, len(file_list)):
        	if file_list[x].minute < recent_minute:
            		temp_list.remove(file_list[x])
    	file_list = list(temp_list)
	if len(file_list) is 1 :
		return file_list
	#------------------------------
    	#finding the most recent seconds
    	recent_seconds = 0
    	for x in range(0, len(file_list)):
        	if file_list[x].seconds > recent_seconds:
            		recent_seconds = file_list[x].seconds
    	#Knocking out all files which are not recent with refrence to recent seconds
    	temp_list = list(file_list)
    	for x in range(0, len(file_list)):
        	if file_list[x].seconds < recent_seconds:
           		temp_list.remove(file_list[x])

	file_list = list(temp_list)
	return file_list

def main(argv):
	file_list = list()
	files = list()
	for x in range(0, len(argv)):
		files.append(argv[x])
	# create new nodes and add file informations to nodes
	# insert all these nodes to a linked list
	for x in range(0, len(files)):
		data =  time.ctime(os.path.getmtime(files[x])).split()
		year = int(data[4])
		month = int(look_up(data[1]))
		date = int(data[2])
		raw_time = data[3].split(':')
		hour = int(raw_time[0])
		minute = int(raw_time[1])
		seconds = int(raw_time[2])
		new_node = node(files[x], year, month, date, hour, minute, seconds)
		file_list.append(new_node)
    
    	recent_file = get_recent_file(file_list)
	print recent_file[0].file_name #this recent_file list has the recently used file

if __name__ == "__main__":
	if len(sys.argv) is 1:
		print "Usage: get_recent.py [filepath and name] [filepath and name] ..."
	else:
		main(sys.argv[1:])
