import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *
import re
import get_theater_yelp as gt

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
	name = data["Title"]
	name = name.lower()
	name = re.sub(r'\s+', '', name)
	print name
	hobj = hashlib.md5(name+data["Year"])
	dat["mid"] = hobj.hexdigest()
	for key in data:
		if key in keys:
			dat[key] = data[key]
	if db.movies.find({"mid": dat["mid"]}).count() >= 1:
		return 100013, "duplicate"
	else:
		result = str(db.movies.insert_one(dat))
	return status, "inserted"
def delete_movie(payload):
	return "OK"
def find_movie(payload):
	status = 1
	res = list()
	data = json.loads(payload) # decode json
	genre = data['genre']
        q = genre
        # db.collectionname.find({'files':{'$regex':'^File'}})
	if db.movies.find({"mid" : q}).count() >= 1:
		cursor = db.movies.find({"mid" : q})
		for document in cursor:
			res.append(str(document))
			status = 1
	else:
		status = 100014
	return status, res	
#------------------------------------------


#------------------------  THEATER OPEARTION
def insert_place(theater):
	hobj = hashlib.md5(theater["key"])
	mid = hobj.hexdigest()
	if db.theater.find({"mid": mid}).count() >= 1:
		return 0
	else:
		theater["mid"] = mid
		result = str(db.theater.insert_one(theater))
		return 1

def insert_theater(payload):
	the_attr = ["name", "coordinate", "phone", "address", "ratings"]
	status = 1
	count = 0
	res = None
	data = json.loads(payload) # decode json
	latitude = data["latitude"]
	longitude = data["longitude"]
	res = gt.main(db, latitude, longitude)
	for key in res:
		theater = dict()
		info = res[key]
		details = json.loads(info)
		if details["category"] == "Cinema":
			theater["key"] = key
			for token in the_attr:
				theater[token] = None
				try:
					theater[token] = details[token]
				except ValueError:
					k = 0
			stat = insert_place(theater)
			if stat == 1:
				count = count + 1
			else:
				k = 0
		status = 1
        return 1, (str(count) + " inserted")# get lat and long and populate with q movies
def delete_theater(payload):
        return "OK"
def find_thater(payload):
        return "OK"
#------------------------------------------
def insert_show_time(jpayload):
	data = json.loads(jpayload)
	movie_mid = data["movie_mid"]
	theater_mid = data["theater_mid"]
	show_times = data["show_time"]
	is_theater = db.theater.find({"mid": theater_mid}).count()
	is_movie = db.movies.find({"mid": movie_mid}).count()
	if is_theater and is_movie:
		movies = list()
		if db.showtimes.find({"mid": theater_mid}).count() >= 1:
			collection = db.showtimes.find({"mid": theater_mid})
			for doc in collection:
				movies_list = list(doc["movies"])
				showtimelist = list()
				if movie_mid in movies_list:
					showtimelist = list(doc[movie_mid])
			showtimelist.append(show_times)
			movies_list.append(movie_mid)
			movies_list= list(set(movies_list))
			showtimelist = list(set(showtimelist))
			db.showtimes.update_one({"mid": theater_mid},{"$set": {"movies":movies_list}})
			db.showtimes.update_one({"mid": theater_mid},{"$set": {str(movie_mid):showtimelist}})
		else :
			movies = list()
			showtimes = list()
			movies.append(movie_mid)
			showtimes.append(show_times)
			result = str(db.showtimes.insert_one({"mid":theater_mid, "movies":movies, movie_mid : showtimes}))
		return 1, "done"
	else:
		return 100014, "not"

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
	if table == "ST":
		if oper == 1000:
			status, res = insert_show_time(payload)
	return status, res
#---------------------------
if __name__ == "__main__":
	main()
