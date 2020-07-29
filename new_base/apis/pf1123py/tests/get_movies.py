import urllib2
import json
from pymongo import MongoClient
import hashlib

client = MongoClient('localhost', 27017)
db = client.Chishiki

def get_movies(zip):
	link = "http://data.tmsapi.com/v1.1/movies/showings?startDate=2016-04-12&zip="+zip+"&api_key=kvrnrwxdrpxvrpa5k83semqu"
	data = urllib2.urlopen(link).read()
	return data
# key -- genres, longDescription etc
# item[key] -- gives details
def link_data(data):
	dat = json.loads(data)
	ticket_url = None
	for movie in dat:
		nm = dict()
		atrs = ["genres", "title", "releaseDate", "advisories", "officialUrl", "releaseYear", "runTime", "shortDescription", "topCast", "topCast"]
		nm["genres"] = list(movie["genres"])
		nm["title"] = movie["title"]
		hobj = hashlib.md5(nm["title"])
		nm["mid"] = hobj.hexdigest()
		#nm["showtimes"] = movie["showtimes"]
		nm["release_date"] = movie["releaseDate"]
		if "advisories" in movie:
			nm["advisories"] = movie["advisories"]
		else:
			nm["advisories"] = None
		if "officialUrl" in movie:
			nm["url"] = movie["officialUrl"]
		else:
			nm["url"] = None
		nm["release_year"]  = movie["releaseYear"]
		if "runTime" in movie:
			nm["run_time"] = movie["runTime"]
		else:
			nm["run_time"] = None
		if "shortDescription" in movie:
			nm["description"] = movie["shortDescription"]
		else:
			nm["description"] = None
		if "topCast" in movie:
			nm["cast"] = movie["topCast"]
		else:
			nm["cast"] = None
		#--> now link show times
		st = movie["showtimes"]
		for show in st:
			if "ticketURI" in show:
				ticket_url = show["ticketURI"]
			else:
				ticket_url = None
			show_time = show["dateTime"]
			theater_name = show["theatre"]["name"]
			#find this theater in our theater db
			movie_list = list()
			if (db.theater.find({"name": theater_name}).count()) >= 1:
				cursor = db.theater.find({"name": theater_name})
				for doc in cursor:
					movie_list = list(doc["movies"])
				mov_st = [nm["mid"], show_time]
				movie_list.append(mov_st)
				db.theater.update_one({"name": theater_name},{"$set": {"movies":movie_list}})
				if len(movie_list) > 1:
					db.theater.update_one({"name": theater_name},{"$set": {"has_movies":"yes"}})
		#--> add movie to Movie database
		nm["ticket_url"] = ticket_url
		if (db.Movies.find({"mid": nm["mid"]}).count()) >= 1:
			k = 0
		else:
			db.Movies.insert_one(nm)

			
def get_zips():
	zips = list()
	cursor = db.theater.find()
	for doc in cursor:
		category = doc["category"]
		if category == "Cinema":
			address = doc["address"]
			if address != None:
				ad = str(address)
				ads = ad.split(" ")
				zip = ads[len(ads)-1]
				zips.append(zip)
	zips = list(set(zips))
	print zips

def main():
	#get_zips()
	zip = str(raw_input("zip: "))
	data = get_movies(zip)
	link_data(data)

if __name__ == "__main__":
	main()
