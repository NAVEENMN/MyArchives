import urllib2
from firebase import Firebase
from pymongo import MongoClient
import json

client = MongoClient('localhost', 27017)
db = client.Chishiki

#FIREBASE_URL = "https://metster-movies.firebaseio.com"
FIREBASE_URL = "https://metstermovies.firebaseio.com/"
def get_data():
	fb_base_url = FIREBASE_URL+"/"+"movies"
	fb = Firebase(fb_base_url)
	dat = fb.get()
	for key in dat:
		inf = dat[key]
		for theater in inf:
			print theater
			movies = inf[theater]
			print movies[0]

def main():
	theaters = list()
	place = dict()
	print "setting up theaters..."
	cursor = db.theater.find({"zone":1})
	for doc in cursor:
		theaters.append(doc["key"])
	print theaters
	mov = list()
	with open('movies.txt') as f:
    		mov.append(f.readlines())
	for loc in theaters:
		place[loc] = mov
	fb_base_url = FIREBASE_URL+"/"+"movies"
	fb = Firebase(fb_base_url)
	fb.put(place)
	print place
	#get_data()

if __name__ == "__main__":
	main()
