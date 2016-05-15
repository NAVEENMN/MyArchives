'''
get_yelp_ranking.py
In Prod
This is the backend recommendation engine for yelp supporting countires.
'''

import argparse
import json
import pprint
import sys
import urllib
import urllib2
import oauth2
import random
import scipy
import time
import os, sys
import operator
from numpy import *
from scipy.spatial import *
from firebase import Firebase
from collections import OrderedDict
import math
import numpy as np
from numpy import linalg as LA
import multiprocessing
from mapbox import Distance
from mapbox import Geocoder
from pprint import pprint
import hashlib
from pymongo import MongoClient
import string

client = MongoClient('localhost', 27017)
db = client.Chishiki

API_HOST = 'api.yelp.com'
DEFAULT_TERM = 'dinner'
DEFAULT_LOCATION = 'San Francisco, CA'
SEARCH_LIMIT = 15
SEARCH_PATH = '/v2/search/'
BUSINESS_PATH = '/v2/business/'
FIREBASE_URL = "https://met-ster-event.firebaseio.com/"

# OAuth credential placeholders that must be filled in by users.
CONSUMER_KEY = 'Z0iY7ApEiif5H7VaWJYCMQ'
CONSUMER_SECRET = 'Q5akSU6NBRPr1wTjIy9NsB7tPC4'
TOKEN = 'LkX6HbwoiGPlPxzP7d9U8My2Zl8K4iMw'
TOKEN_SECRET = 'KAhDbHSHgoxEuYK9jwvYRW3awmw'

token = "pk.eyJ1IjoibXlzb3JuMSIsImEiOiJjaW1jcXZkd2UwMDI1dHNra3kyZzZ6YmZ5In0.XXYOEo0n7n0Kxg8ULBumAg"
os.environ["MAPBOX_ACCESS_TOKEN"] = token
service = Distance()


#food_cus = ["American","British", "Chinese", "French", "Greek", "Indian", "Italian", "Japanese", "Mediterranean", "Mexican", "Thai", "Vitnamese"]
other_food = ["icecream", "coffee", "tea", "yogurt", "Hookah", "bars", "nightlife"]
#movies_gen = ["Action","Animation","Comdey", "Documentary", "Family", "Horror", "Musical", "Romance", "Sport", "War", "Adventure", "Crime", "Drama", "Mystery", "Sci-fi", "Thriller"]

food_cus = ["American","Chinese","Japanese","Mexican" ,"French","Thai", "Indian", "Italian", "Vitnamese","Mediterranean"]
movies_gen = ["Action", "Animation", "Comedy","Romance","Sci-fi", "Family", "Horror", "Documentary","Adventure","Thriller","War"]

#------ food dic
food_lookup = dict()
food_lookup["a"] = "American"
food_lookup["b"] = "Chinese"
food_lookup["c"] = "Japanese"
food_lookup["d"] = "Mexican"
food_lookup["e"] = "French"
food_lookup["f"] = "Thai"
food_lookup["g"] = "Indian"
food_lookup["h"] = "Italian"
food_lookup["i"] = "Vietnamese"
food_lookup["j"] = "Mediterranean"
food_lookup["k"] = "British"
food_lookup["l"] = "Fast Food"
#---------------

def request(host, path, url_params=None):
    url_params = url_params or {}
    url = 'http://{0}{1}?'.format(host, urllib.quote(path.encode('utf8')))
    consumer = oauth2.Consumer(CONSUMER_KEY, CONSUMER_SECRET)
    oauth_request = oauth2.Request(method="GET", url=url, parameters=url_params)
    oauth_request.update(
                         {
                         'oauth_nonce': oauth2.generate_nonce(),
                         'oauth_timestamp': oauth2.generate_timestamp(),
                         'oauth_token': TOKEN,
                         'oauth_consumer_key': CONSUMER_KEY
                         }
                         )
    token = oauth2.Token(TOKEN, TOKEN_SECRET)
    oauth_request.sign_request(oauth2.SignatureMethod_HMAC_SHA1(), consumer, token)
    signed_url = oauth_request.to_url()
    conn = urllib2.urlopen(signed_url, None)
    try:
        response = json.loads(conn.read())
    finally:
        conn.close()
                                 
    return response


def search(term, location):
    url_params = {
        'term': term.replace(' ', '+'),
        'll': location,
        'limit': SEARCH_LIMIT
    }
    return request(API_HOST, SEARCH_PATH, url_params=url_params)

def searchb(term, location):
    url_params = {
        'term': term.replace(' ', '+'),
        'bounds': location,
        'limit': SEARCH_LIMIT
    }
    return request(API_HOST, SEARCH_PATH, url_params=url_params)

def get_business(business_id):
    business_path = BUSINESS_PATH + business_id
    return request(API_HOST, business_path)

def query_api(term, location, type):
    place_details = dict()

    if(type == "a"):
        response = search(term, location)
        businesses = response.get('businesses')
        if not businesses:
            print u'No businesses for {0} in {1} found.'.format(term, location)
            return
    
        for x in range(0, len(businesses)):
            data = businesses[x]['id']
            r = get_business(data)
            info = dict()
	    address = None
	    types = None
            try:
                info['rank'] = str(x);
                info['ratings'] = str(r['rating'])
                info['name'] = str(r['name'])
                info['review_count'] = str(r['review_count'])
                info['phone'] = str(r['display_phone'])
                info['snippet']  = str(r['snippet_text'])
		for x in range(0, len(r['location']['display_address'])):
                	address = address +" "+r['location']['display_address'][x]
                info['address'] = str(address)
                info['coordinate'] = str(r['location']['coordinate'])
                info['latitude'] = str(r['location']['coordinate']['latitude'])
                info['longitude'] = str(r['location']['coordinate']['longitude'])
                info['snippet']  = str(r['snippet_text'])
                info['image_url'] = str(r['image_url'])
                info['url'] = str(r['mobile_url'])
		cat = list()
                for x in range(len(r['categories'])):
			typ = str(r['categories'][0][x]).lower()
                        cat.append(typ)
                cat = list(set(cat))
                cate = ""
                for tp in cat:
                        cate = cate + " " + tp
                info['category'] = str(cate)

                place_details[data] = json.dumps(info)
            except:
                v = 0
    
    if(type == "b"):
        response = searchb(term, location)
        businesses = response.get('businesses')
 	types = None
	address = None
        if not businesses:
            print u'No businesses for {0} in {1} found.'.format(term, location)
            return
    
        for x in range(0, len(businesses)):
            data = businesses[x]['id']
            r = get_business(data)
            info = dict()
            try:
                if(r['name']):
                    info['rank'] =str(x)
                    info['ratings'] = str(r['rating'])
                    info['name'] = str(r['name'])
                    info['review_count'] = str(r['review_count'])
                    info['phone'] = str(r['display_phone'])
                    info['address'] = str(r['location']['display_address'])
                    info['coordinate'] = str(r['location']['coordinate'])
                    info['latitude'] = str(r['location']['coordinate']['latitude'])
                    info['longitude'] = str(r['location']['coordinate']['longitude'])
                    info['snippet']  = "no"#str(r['snippet_text'])
                    info['url'] = str(r['mobile_url'])
                    info['image_url'] = str(r['image_url'])
                    info['category'] = str(r['categories'])
                    info['id'] = data.replace("-","")
                    place_details[data.replace("-","")] = json.dumps(info)
            except:
                v = 0

    return place_details

'''
    [[max(lat), max(lon)],[min(lat), min(lon)]]
    '''
def get_region(people):
    lats = list()
    longs = list()
    for person in people:
        lats.append(person[0])
        longs.append(person[1])
    top_right = [max(lats), max(longs)]
    left_bottom = [min(lats), min(longs)]
    return top_right, left_bottom


def get_results(people, item):
    
    payload = None
    
    lat = 0
    lon = 0
    for person in people:
        lat = lat + person[0]
        lon = lon + person[1]
    lat = lat / len(people)
    lon = lon / len(people)
    centroid_location = str(lat) + "," + str(lon)

    if(len(people) > 2):
        top_right, left_bottom = get_region(people)
        parta = str(top_right[0]) + "," + str(top_right[1])
        partb = str(left_bottom[0]) + "," + str(left_bottom[1])
        group_location = parta + "|" + partb
    
    if(len(people) == 1):
        # use location and make query
        try:
            data = query_api(item, str(people[0]).replace("[","").replace("]",""), "a")
            payload = data
        except urllib2.HTTPError as error:
            sys.exit('Encountered HTTP error {0}. Abort program.'.format(error.code))

    if(len(people) == 2):
        # compute centroid and make query
        try:
            point = centroid_location
            data = query_api(item, point, "a")
            payload = data
        except urllib2.HTTPError as error:
            sys.exit('Encountered HTTP error {0}. Abort program.'.format(error.code))

    if(len(people) > 2):
        #bounds=sw_latitude,sw_longitude|ne_latitude,ne_longitude
        try:
            data = query_api(item, group_location, "b")
            payload = data
        except urllib2.HTTPError as error:
            sys.exit('Encountered HTTP error {0}. Abort program.'.format(error.code))
    return payload


'''
    name : get_distance
    @params : Double, Double
    @return : Double
    @desp : This function computes the euclidean distance between given two points
    We need to change this to actual distance
'''
def get_distance(a, b):
    return distance.euclidean(a, b)



'''
    name : new_ranking_base
    @prams: lists
    @return
    @desp :This function computes the ranking order for the places. 
'''

def new_ranking_base(place_name_list, place_location, person_location):

    all_places = list()
    ranked_result = list()

    # merge all locations
    for x in range(0, len(person_location)):
        lat = float(person_location[x][0])
        lon = float(person_location[x][1])
        member = {'type': 'Feature', 'properties': {'name': 'user'}, 'geometry': {'type': 'Point','coordinates': [lon, lat]}}
        all_places.append(member)
    for x in range(0, len(place_location)):
        lat = float(place_location[x][0])
        lon = float(place_location[x][1])
        place = {'type': 'Feature', 'properties': {'name': 'place'}, 'geometry': {'type': 'Point','coordinates': [lon, lat]}}
        all_places.append(place)
    
    service = Distance()
    res = service.distances(all_places, 'driving')
    distances = res.json()['durations']
    DM = np.matrix(distances)
    CDM = DM[:len(person_location),len(person_location):]
    sumdistances = CDM.sum(axis=0)
    distances = sumdistances.tolist()
    dis = distances[0]
    temp_dis = list(dis)
    temp_dis.sort()
    for x in range(0, len(temp_dis)):
        td = temp_dis[x]
        idx = dis.index(td)
        ranked_result.append(place_name_list[idx])
    return ranked_result
'''
    name : ranking_base
    @params : lists
    @return :
    @desp : This function computes the ranking order for the places.
'''
def ranking_base(place_name_list, place_rating_list, place_location, person_location):
    ranked = dict()
    ranked_ratings = dict()
    ratings_ranking = dict()
    
    # all places are equally preferred
    for x in range(0, len(place_name_list)):
        ranked[place_name_list[x]] = 0.0
        ratings_ranking[place_name_list[x]] = place_rating_list[x]
    
    #sort based on ratings
    #better rating -> lower ranking
    #note : The places are in correct order
    ranked_ratings =  sorted(ratings_ranking.items(), key=lambda x: x[1])

    # create a matirx of dimension x = len(locations) and y = len(person)
    Matrix = [[0 for x in range(len(place_name_list))] for x in range(len(person_location))]

    #update distance to Matrix
    max_distance = 0
    
    # find the maximum distance in this network
    for x in range(0, len(Matrix)):
        for y in range(0,len(Matrix[x])):
            Matrix[x][y] = get_distance(person_location[x],place_location[y])
            if Matrix[x][y] > max_distance:
                max_distance = Matrix[x][y]

    #normalize the matirix
    for x in range(0, len(Matrix)):
        for y in range(0,len(Matrix[x])):
            Matrix[x][y] = Matrix[x][y] / max_distance


    #for each person sort based on how close that place is
    # the value of x refer to the people.
    for x in range(0, len(Matrix)):
        row = list(Matrix[x])# retrives the distances to the places from the person.
        row = sort(row) # row[0] is the closet distance to the person
        for y in range(0, len(row)):
            # for this person find the location in the row where this distance exists.
            current_place = place_name_list[Matrix[x].index(row[y])] # get that place name.
            ranked[current_place] = float( ranked[current_place] + y )

    # we have to sort the dictonary ranked
    sorted_ranked = sorted(ranked.items(), key=operator.itemgetter(1))

    # the sorted dict returns a list so we need to clean the data
    RANKED_LIST = reduce_features(sorted_ranked, ranked_ratings)
    return RANKED_LIST
    # now we need to reduce the features and get a single ranked list

'''
    name :reduce_features
    @params : lista (place_name, ranking), listb (place_name, ranking)
    @return: listc (place_name, ranking)
    @desp: This function takes two lists of features and returns a ranked list (combined featues)
'''
def reduce_features(lista, listb):
    ratings_list = list()
    featurea = dict()
    featureb = dict()
    # converting list to dict
    for x in range(0, len(lista)):
        featurea[lista[x][0]] = lista[x][1]
        featureb[listb[x][0]] = listb[x][1]

    for x in range(0 , len(lista)):
        values = list()
        #get the value for featurea
        fa_value = featurea[lista[x][0]]
        fb_value = featureb[lista[x][0]]
        values.append(fa_value)
        values.append(fb_value)
        ratings_list.append(lista[x][0])
        ratings_list.append(values)

    # sort based on the even attrb and find the sorted index in ratings_list, index -1 should be the place.

    pair_list = list()
    pair_dict = dict()
    for x in range(1, len(ratings_list), 2):
        pair_list.append(ratings_list[x])
        pair_dict[ratings_list[x-1]] = ratings_list[x]

    pair_list.sort()

    RANKED_LIST = list()
    for pair in pair_list:
        for key in pair_dict.keys():
            if pair_dict[key] == pair:
                RANKED_LIST.append(key)
                pair_dict[key] == None # prevent this from adding again.

    return RANKED_LIST
'''
    name : print_matrix
    @params : 2 Dim Array Int
    @return : void
    @desp : This function prints the matrix
    '''
def print_matrix(Matrix):
    for x in range(0, len(Matrix)):
        print Matrix[x]


def get_movies(pdic):
	for key in pdic:
		hobj = hashlib.md5(key)
		mid = hobj.hexdigest()
		if db.theater.find({"mid": mid}).count() > 0:
			if db.showtimes.find({"mid": mid}).count() > 0:
				cursor = db.showtimes.find({"mid": mid})
				for doc in cursor:
					print doc
			else:
				print "not in list"
		else:
			print "not in db"
		

'''
    name : ranking_based_on_convenience
    @params : json data of payload
    @return : ranked payload
    @desp: this function extends the based algorithm of ranking which was applied on google
            data in the previous version
'''

def ranking_based_on_convenience(payload, people):
  
    place_name_list = list()
    place_location = list()
    person_location = people
    
    for key in payload:
        place_name_list.append(key)
        data = json.loads(payload[key])
        location = str(data['coordinate'])
        parm = location.split(",")
        raw = parm[0].split(": ")
        latitude = float(raw[1])
        raw1 = parm[1].split(": ")
        longitude = float(raw1[1].replace("}",""))
        location = list()
        location.append(latitude)
        location.append(longitude)
        place_location.append(location)
  
    return new_ranking_base(place_name_list, place_location, person_location)

def get_pref_vec(pref):
    vec = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
    val = [0.880797077978, 0.869891525637, 0.8581489351, 0.845534734916, 0.832018385134, 0.817574476194, 0.802183888559, 0.785834983043, 0.768524783499, 0.750260105595, 0.73105857863, 0.72, 0.71 ]
    pvec = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    for x in range (0, len(pref)):
        h = pref[x]
	ind = vec.index(h)
	pvec[ind] = val[x]
    return np.array(pvec)

def main(db, query, eventid):
    print ("enter the search")
    print query
    #eventid = "10103884620845515--event--0"#sys.argv[1]
    cursor = db.events.find({"mid":eventid})
    people = list()
    location = list()
    choices = list()
    pref_vec = list()
    pref_vec = list()
    prefs = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
   
    # get all members
    for document in cursor:
	members = document["event_members"]	
    	if members[0] is not "none":
	    for x in range(0, len(members)):
	        people.append(members[x])

    # for all members get locations and preference
    for person in people:
	cursor = db.accounts.find({"mid":person})
	for document in cursor:
                pref = str(document["food_pref"])
		lat = float(document["latitude"])	
		lon = float(document["longitude"])
		loc = [lat, lon]
	pref = pref.lower()
	nap = get_pref_vec(pref)
	pref_vec.append(nap)		
	location.append(loc)
        #by deafult go with group
	# we need to make queries based on distrubtion of mean theta
	# for this version lets just pick top two 
    mpv = np.zeros(13)
    for x in range(0, len(pref_vec)):
	mpv = mpv + pref_vec[x]
    mpvlist = mpv.tolist()
    mpvcopy = list(mpvlist)
    mpvcopy.sort()
    ftp = mpvcopy[len(mpvcopy) - 1]
    stp = mpvcopy[len(mpvcopy) - 2]
    idx_ftp = mpvlist.index(ftp)
    idx_stp = mpvlist.index(stp)
    choices.append(food_cus[idx_ftp])
    choices.append(food_cus[idx_stp]) 

    merged_results = dict()
    final_results = dict()
    print location
    if query != "movies":
        response = get_results(location, query)
        merged_results.update(response)
        CONV_RANKED_LIST = ranking_based_on_convenience(merged_results, location)
    for term in CONV_RANKED_LIST:
        final_results[term] = merged_results[term]

    print final_results
    return final_results

if __name__ == "__main__":
    main()
