import yelpapi
import googleapi
import json
from firebase import Firebase
import hashlib
import sys

FIREBASE_URL = "https://met-ster-event.firebaseio.com/"

# these are dictonaries which keep holds keys and nodes
google_lookup = dict()
yelp_lookup = dict()
payload = dict()

# pulls data from yelp and creates multiple nodes
class yelp_node():
    name = None
    rating = None
    refrence = None
    latitude = None
    longitude = None
    address = None
    price_level = None
    review_count = None
    image_url = None
    category = None
    snippet = None
    url = None
    phone = None

# pulls data from google and creats multiple nodes.
class google_node():
    name = None
    rank = None
    rating = None
    refrence = None
    location = None
    address = None
    price_level = None

'''
    name : get_region
    params : people list
    return : location top_right, location left_bottom
    desp : This function gets the list of people`s location
            and finds the boundary which accomodates all tghe points
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

'''
    name : print_all
    params : dictonary
    return : null
    desp: This function for a given dictnary it prints all the defined nodes
'''
def print_all(dic_data):
    for key in dic_data:
        node  = dic_data[key]
        print node.name


'''
    name : main
    params : None
    return : null
    desp : This the main function which pulls data from yelp and google
            first we create node for google and yelp
            loop through google json and creat nodes
            loop though yelp nodes and update relevant nodes.
'''
def main():
    people = list() # holds data in this format [[latitude, longitude],[lat, long],[...]]
    #event_id = "859842507380812-->event-->0"
    event_id = sys.argv[1]
    choice = sys.argv[2]
    # handing firebase
    eventid = event_id.replace("--","-->")
    user = eventid.rpartition('-->')[0]
    user = user.rpartition('-->')[0]
    fb_ref = FIREBASE_URL + user + "/" + eventid
    fb = Firebase(fb_ref)
    data = fb.get()
    
    point = None
    radius = None
    for key in data.keys():
        if(data[key]['nodetype'] == "member" or data[key]['nodetype'] == "host"):
            fb_lat = float(data[key]['Latitude'])
            fb_lon = float(data[key]['Longitude'])
            people.append([fb_lat, fb_lon])
    
    if len(people) == 1:
        for person in people:
            lat = lat + person[0]
            lon = lon + person[1]
        lat = lat / len(people)
        lon = lon / len(people)
        point = [lat, lon]
        radius = 5000
        
    if len(people) == 2:
        for person in people:
            lat = lat + person[0]
            lon = lon + person[1]
        lat = lat / len(people)
        lon = lon / len(people)
        point = [lat, lon]
        radius = 5000

    if len(people) > 2:
        tr, lb = yelpapi.get_region(people)
        cent_lat =  (tr[0]+lb[0])/2
        cent_long = (tr[1]+lb[1])/2
        point = [cent_lat, cent_long]
        top_right = tr
        radius = googleapi.get_distance(point, tr)
        radius = radius * 1000 * 10

    # get data from google and yelp
    yelp_data = yelpapi.pull_data_from_firebase(eventid, choice)
    google_data = googleapi.main(eventid, choice, point, radius)
    yelp_json = json.loads(json.dumps(yelp_data))
    for key in yelp_json:
        yn = yelp_node()
        place = json.loads(yelp_json[key])
        yn.name = place['name']
        yn.rating = place['ratings']
        yn.review_count = place['review_count']
        yn.category = place['category']
        yn.snippet = place['snippet']
        yn.url = place['url']
	yn.image_url = place['image_url']
        yn.phone = place['phone']
        yelp_lookup[yn.name] = yn
    '''
        look though the google json and build data
        if key match update from yelp
    '''
    google_json = json.loads(google_data)
    for key in google_json:
        data = dict()
        data['name'] = google_json[key]['name']
        data['rank'] = google_json[key]['rank']
        data['category'] = google_json[key]['types']
        data['rating'] = google_json[key]['rating']
        data['review_count'] = None
        data['price_level'] = google_json[key]['price_level']
        data['address'] = google_json[key]['address']
        data['location'] = google_json[key]['location']
        data['url'] = None
        data['phone'] = None
        data['snippet'] = None
	data['image_url'] = None
        if(yelp_lookup.has_key(data['name'])):
            yn = yelp_lookup[data['name']]
            data['rating'] = yn.rating
            data['review_count'] = yn.review_count
            data['snippet'] = yn.snippet
            data['category'] = yn.category
            data['url'] = yn.url
            data['phone'] = yn.phone
            data['image_url'] = yn.image_url
        payload[key] = json.dumps(data)

    print payload

if __name__ == "__main__":
    main()
