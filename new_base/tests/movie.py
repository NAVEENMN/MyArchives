import json
import urllib2
import hashlib

URL = "http://www.omdbapi.com/?t=" #the+martian
DESP = "&y=&plot=short&r=json"

def query_mov(movie_name):
	format_name = movie_name.replace(" ", "+")
 	json_url = URL + format_name + DESP
   	j = urllib2.urlopen(json_url)
	data = json.load(j)
	m = hashlib.sha1()
	m.update(movie_name)
	mid = m.hexdigest()
	print "-----"
	print mid
	print data['Title'], data['Rated'], data['Runtime'], data['imdbRating']
	print data['Genre']
	print data['Plot']
	print "------"

def main():
    	new_movies = list()
	new_movies.append("The Martian")
	new_movies.append("The Intern")
	new_movies.append("The Walk")
	new_movies.append("Hotel Transylvania 2")
	for movie in new_movies:
		query_mov(movie)

if __name__ == "__main__":
	main()
