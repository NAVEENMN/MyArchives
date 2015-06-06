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
    
    if(type == "a"):
        place_details = dict()
        response = search(term, location)
        businesses = response.get('businesses')
        if not businesses:
            print u'No businesses for {0} in {1} found.'.format(term, location)
            return
    
        for x in range(0, len(businesses)):
            data = businesses[x]['id']
            r = get_business(data)  
	    info = dict()
	    try:
	    	info['ratings'] = str(r['ratings'])
		info['name'] = str(r['name'])
		info['review_count'] = str(r['review_count'])
		info['phone'] = str(r['display_phone'])
		info['snippet']  = str(r['excerpt'])
		info['address'] = str(r['location']['display_address'])
		info['coordinate'] = str(r['location']['coordinate'])
		info['snippet']  = str(r['snippet_text'])
		info['url'] = str(r['mobile_url'])
		info['category'] = str(r['categories'])
	    except:
	    	v = 0 
	    place_details[data] = json.dumps(info)
        return place_details

    if(type == "b"):
        place_details = dict()
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
	    	info['ratings'] = str(r['rating'])
		info['name'] = str(r['name'])
		info['review_count'] = str(r['review_count'])
		info['phone'] = str(r['display_phone'])
		info['address'] = str(r['location']['display_address'])
		info['coordinate'] = str(r['location']['coordinate'])
		info['snippet']  = str(r['snippet_text'])
		info['url'] = str(r['mobile_url'])
		info['category'] = str(r['categories'])
	    except:
	    	v = 0 
	    place_details[data] = json.dumps(info)
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
    		data = query_api(item, str(people[0]).replace("[","").replace("]",""))
        	payload = data
    	except urllib2.HTTPError as error:
        	sys.exit('Encountered HTTP error {0}. Abort program.'.format(error.code))

    if(len(people) == 2):
    # compute centroid and make query
    	try:
        	data = query_api(item, centroid_location, "a")
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
        The event ID standard is 7098908-->event-->0
        but for parsing data comes in 0989887--event--0
    '''
    user = event_id.rpartition('--')[0]
    user = user.rpartition('--')[0]
    fb_ref = FIREBASE_URL + user + "/" + event_id
    fb = Firebase(fb_ref)
    data = fb.get()
    for key in data.keys():
        if(data[key]['nodetype'] == "member" or data[key]['nodetype'] == "host"):
            fb_lat = float(data[key]['Latitude'])
            fb_lon = float(data[key]['Longitude'])
            people.append([fb_lat, fb_lon])

    payload = get_results(people, choice)
    print payload

def main():
    eventid = sys.argv[1]
    choice = sys.argv[2]
    eventid = eventid.replace("--","-->")
    pull_data_from_firebase(eventid, choice)

if __name__ == "__main__":
    main()
