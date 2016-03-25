import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *


client = MongoClient('localhost', 27017)
db = client.Chishiki

#------------------------  MOVIES OPEARTION
def insert_movie(payload):
	status = 1
	data = json.loads(payload) # decode json
	url = data['url']
	dat = dict()
	keys = ["Title", "Year", "Rated", "Released", "Runtime", "Genre", "Poster", "imdbRating"]
	jdata = urllib2.urlopen(url).read()
	data = json.loads(jdata)
	hobj = hashlib.md5(data["Title"]+data["Year"])
	dat["mid"] = hobj.hexdigest()
	for key in data:
		if key in keys:
			dat[key] = data[key]
	if db.movies.find({"mid": dat["mid"]}).count() >= 1:
		return 100013, "duplicate"
	else:
		result = str(db.movies.insert_one(dat))
	return status, data["Title"]
def delete_movie(payload):
	return "OK"
def find_movie(payload):
	return "OK"
#------------------------------------------


#------------------------  THEATER OPEARTION
def insert_theater(payload):
        return "OK"
def delete_theater(payload):
        return "OK"
def find_thater(payload):
        return "OK"
#------------------------------------------


#----------------------- MAIN
# 11 insert movie
# 12 delete movie
# 13 find movie
# 21 insert theater
# 22 delete theater
# 23 find theater
def main(table, oper, payload):
 	token = str(oper)
	status = 999999
	res = None
	print oper
	if table == "MOV": #movies
		if oper == 1000: # insert
			status, res = insert_movie(payload)
		if oper == 1001: # delete
			status, res = delete_movie(payload)
		if oper == 1002: # find
			status, res = find_movie(payload)
	if table == "THR": #theater
		if oper == 1000: # insert
			status, res = insert_theater(payload)
                if oper == 1001: # delete
			status, res = delete_theater(payload)
                if oper == 1002: # find
			status, res = find_theater(payload)

	return status, res
#---------------------------
if __name__ == "__main__":
	main()
