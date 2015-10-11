import json
import urllib2
import hashlib

URL = "http://data.tmsapi.com/v1.1/movies/showings?startDate=2015-10-10&zip="
DESP = "&radius=30&api_key="
KEY = "vrwqerdykcjp77emby33g3g5"
st = dict() # movies_id --> theater
mv = dict() # movies_id --> movie_data
def query_mov(zip_code):
	#format_name = movie_name.replace(" ", "+")
 	json_url = URL +zip_code+DESP+KEY
   	j = urllib2.urlopen(json_url)
	data = json.load(j)	
	for x in range(0, len(data)):
		movie_info = dict()
		title = data[x]['title']
		movie_info['title'] = title
     		mpayload = json.dumps(movie_info)
		# get the movie title to compyer the sha ash
		m = hashlib.sha1()
		m.update(title)
		mid = str(m.hexdigest())
		mv[mid] = mpayload
		noft = data[x]['showtimes']
		for place in noft:
			showtimes = place
			theater = showtimes['theatre']['name']
			if mid in st:
				mlist = st[mid]
				if theater not in mlist:
					mlist.append(theater)
                        		st[mid] = mlist
			else:
				movies_list = list()
                        	movies_list.append(theater)
                       		st[mid] = movies_list

def main():
    	query_mov("94043")
	for item in st:
		print item,mv[item], st[item]
if __name__ == "__main__":
	main()
