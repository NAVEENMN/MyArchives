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
		if len(r['location']['display_address']) == 1 :
			address = r['location']['display_address'][0]
                if len(r['location']['display_address']) == 2 :
                        address = r['location']['display_address'][0] +", "+ r['location']['display_address'][1]
                if len(r['location']['display_address']) == 3 :
                        address = r['location']['display_address'][0] + ", "+ r['location']['display_address'][1] + ", " + r['location']['display_address'][2]
                info['address'] = address
                info['coordinate'] = str(r['location']['coordinate'])
                info['latitude'] = str(r['location']['coordinate']['latitude'])
                info['longitude'] = str(r['location']['coordinate']['longitude'])
                info['snippet']  = str(r['snippet_text'])
                info['image_url'] = str(r['image_url'])
                info['url'] = str(r['mobile_url'])
 		if len(r['categories']) == 1 :
                        types = r['categories'][0][0]
                if len(r['categories']) == 2 :
                        types = r['categories'][0][0] +", "+ r['categories'][0][1]
                if len(r['categories']) == 3 :
                        types = r['categories'][0][0] + ", "+ r['categories'][0][1] + ", " + r['categories'][0][2]
		if len(r['categories']) == 4 :
                        types = r['categories'][0][0] + ", "+ r['categories'][0][1] + ", " + r['categories'][0][2] + ", " + r['categories'][0][3]

                info['category'] = types
                place_details[data] = json.dumps(info)
            except:
                v = 0
    
    if(type == "b"):
        response = searchb(term, location)
        businesses = response.get('businesses')
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


def pull_data_from_firebase(event_id, choice):
    people = list() # holds data in this format [[latitude, longitude],[lat, long],[...]]
    '''
        The event ID standard is 7098908-->event-->0 and comes in this format
        '''
    user = event_id.rpartition('-->')[0]
    user = user.rpartition('-->')[0]
    fb_ref = FIREBASE_URL + user + "/" + event_id
    fb = Firebase(fb_ref)
    data = fb.get()
    for key in data.keys():
        if(data[key]['nodetype'] == "member" or data[key]['nodetype'] == "host"):
            fb_lat = float(data[key]['Latitude'])
            fb_lon = float(data[key]['Longitude'])
            people.append([fb_lat, fb_lon])
 
    payload = get_results(people, choice)
    return payload, people

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
    print "** matrix"
    print Matrix
    print "** ranked_list"
    print RANKED_LIST
    print "****"
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


'''
    name : ranking_based_on_convenience
    @params : json data of payload
    @return : ranked payload
    @desp: this function extends the based algorithm of ranking which was applied on google
            data in the previous version
'''

def ranking_based_on_convience(payload, people):
    #location section
    place_name_list = list()
    place_rating_list = list()
    place_location = list()
    #people section
    person_location = people
    
    for key in payload:
        place_name_list.append(key)
        data = json.loads(payload[key])
        location = str(data['coordinate'])
        ratings = float(data['ratings'])
        place_rating_list.append(ratings)
        parm = location.split(",")
        raw = parm[0].split(": ")
        latitude = float(raw[1])
        raw1 = parm[1].split(": ")
        longitude = float(raw1[1].replace("}",""))
        location = list()
        location.append(latitude)
        location.append(longitude)
        place_location.append(location)
    
    RANKED_LIST = ranking_base(place_name_list, place_rating_list, place_location, person_location)
    return RANKED_LIST


def main():
    eventid = sys.argv[1]
    choice = sys.argv[2]
    eventid = eventid.replace("--","-->")
 
    '''
        The event ID standard is 7098908-->event-->0 and comes in this format
    '''
    user = eventid.rpartition('-->')[0]
    user = user.rpartition('-->')[0]
    fb_ref = FIREBASE_URL + user + "/" + eventid
    fb = Firebase(fb_ref)
    data = fb.get()
    choice_list = list()
    choice_dict = dict()

    for key in data.keys():
        if(data[key]['nodetype'] == "member" or data[key]['nodetype'] == "host"):
            food = data[key]['food']
	    choice_list.append(food)
    
    # creating a dictonary for choice list
    for x in range(0, len(choice_list)):
    	if choice_dict.has_key(choice_list[x]):
		choice_dict[choice_list[x]] = choice_dict[choice_list[x]] + 1
	else:
		choice_dict[choice_list[x]] = 1

    set_choice = set(choice_list)
    out_payload = dict()
    RANK = 0
    for q_term in set_choice:
    	response, people = pull_data_from_firebase(eventid, q_term) # get results
    	RANKED_LIST = ranking_based_on_convience(response, people) # rank them
	#print "query term ", q_term
	weight = float(choice_dict[q_term]) / float(len(people))
        #print "weightage", weight
	number_of_results = int( weight * 20 )
	#print "# of results for this query", number_of_results
        clipped_list =  RANKED_LIST[0:number_of_results]
    	for key in response:#setup rank in json
        	pay_dict = dict()
		PAYLOAD = json.loads(response[key])
                if key in clipped_list:
			for term in PAYLOAD:
	 			if term == "rank":
					rank = RANKED_LIST.index(key)
					pay_dict["rank"] = RANK
					RANK = RANK + 1
				else:
					pay_dict[term] = PAYLOAD[term]
			out_payload[key] = json.dumps(pay_dict)
    print out_payload

if __name__ == "__main__":
    main()
