from numpy import *
import random
import scipy
from scipy.spatial import *
import urllib2
import json
from firebase import Firebase
import time
import os, sys
from collections import OrderedDict

location = list() # [[latitude, longitude], ... ]
names = list() # [names, ... ]
id = list() # [id, ... ]
nodes = dict() #[node ojects, ...]
FIREBASE_URL = "https://met-ster-event.firebaseio.com/"

class node():
    name = None
    rating = None
    refrence = None
    latitude = None
    longitude = None
    address = None
    price_level = None
    types = None

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
    name : req_place_details
    @params : Double, Double
    @return: List (Double), List(String)
    @desp: This function takes latitude and longitude as input in form
    of double and fetches all the restraunts and returns its names
    and latitdues and longitudes
    '''
def req_place_details(lat, long, food_type, radius):
    radius = str( int(round(radius * 10,-1)) )
    ratings = list()
    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + long + "&radius=400"+"&types=food&keyword="+food_type+"&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE"
    response = urllib2.urlopen(url).read()
    jason_data = json.loads(response)
    for place in jason_data['results']:
        
        n = node()
        n.name = place['name']
        n.latitude = place['geometry']['location']['lat']
        n.longitude = place['geometry']['location']['lng']
        n.reference = place['reference']
        n.types = place['types']
        lati = place['geometry']['location']['lat']
        longi = place['geometry']['location']['lng']
        names.append(place['name'])
        location.append([lati, longi])
        id.append(place['reference'])
        
        if 'rating' in place:
            ratings.append(place['rating'])
            n.rating = place['rating']
        else:
            ratings.append(0.0) # need a better approch this is unfair approch
            n.rating = 0.0
        
        
        if 'price_level' in place:
            n.price_level = place['price_level']
        else:
            n.price_level = '0.0'


        if 'vicinity' in place:
            n.address = place['vicinity']
        else:
            n.address = "None"
    
        nodes[place['reference']] = n

    return location, names, ratings, id

'''
    name : get_place_details
    @params : String id (refrence in google places)
    @returns : details
    @desp : This function gets the complete place details
    '''

def get_place_details(id, rank, location):
    node = nodes[id]
    
    #building json data
    data = dict()
    
    data['name'] = node.name
    data['address'] = node.address
    data['rank'] = str(rank)
    data['location'] = location
    data['ref'] = id
    data['rating'] = node.rating
    data['price_level'] = node.price_level
    data['types'] = node.types
    
    return data

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
    name : initial_location
    @params :
    @return :
    @desp : This funtion computes the initial points to put query on.
    '''


'''
    name : get_features
    @params : String, fire_base_refrence, convince_dictonary
    @return : convince_feature
    @desp : This functions computes the ranking order of the places bases on convinces
    We first create a people to places matrix with distances as its value, Then
    we sort each row and that gives ranking order for each person. We create
    new ranks for each place and combine them and normalize them.
    '''
def get_features(eventid,fb_res, ranking, rating_ranking, choice, point, radius):
    people = list()
    ranked = dict()
    ranked_ratings = dict()
    centroid = None
    food_type = None
    # get people`s location
    for key in fb_res.keys():
        if(fb_res[key]['nodetype'] == "member" or fb_res[key]['nodetype'] == "host"):
            fb_lat = float(fb_res[key]['Latitude'])
            fb_lon = float(fb_res[key]['Longitude'])
            people.append([fb_lat, fb_lon])

    # get location centroid
    lat = 0
    lon = 0
    for person in people:
        lat = lat + person[0]
        lon = lon + person[1]
    lat = lat / len(people)
    lon = lon / len(people)
    centroid = [point[0], point[1]]
    food_type = choice
    # Let have the centroid on maps for verification
    #fb.put(eventid,"709091411991*799--center", {'Latitude': lat, 'Longitude': lon})
    # For this version we are considering centroid but we need better approach
    locations, names, ratings, id = req_place_details(str(centroid[0]), str(centroid[1]),food_type, radius)

    # all places are equally preferred
    for x in range(0, len(names)):
        ranking[names[x]] = 0.0
        rating_ranking[names[x]] = ratings[x]
    
    #sort based on ratings
    ranked_ratings =  sorted(rating_ranking.items(), key=lambda x: x[1])

    rank = 0.0
    for place in ranked_ratings[::-1]:
        rating_ranking[ place[0] ] = rank
        rank = rank + 1
        rank = rank / len(ranked_ratings)

    # create a matirx of dimension x = len(locations) and y = len(person)
    Matrix = [[0 for x in range(len(locations))] for x in range(len(person))]
    #update distance to Matrix
    max_distance = 0
    
    # find the maximum distance in this network
    for x in range(0, len(Matrix)):
        for y in range(0,len(Matrix[x])):
            Matrix[x][y] = get_distance(person[x],locations[y])
            if Matrix[x][y] > max_distance:
                max_distance = Matrix[x][y]

    #normalize the matirix
    for x in range(0, len(Matrix)):
        for y in range(0,len(Matrix[x])):
            Matrix[x][y] = Matrix[x][y] / max_distance
    
    #for each person sort based on how close that place is
    for x in range(0, len(Matrix)):
        row = list(Matrix[x])
        row = sort(row)
        for y in range(0, len(row)):
            current_place = names[Matrix[x].index(row[y])]
            ranking[current_place] = float( ranking[current_place] + y )

    ranked =  sorted(ranking.items(), key=lambda x: x[1])
    normalize_ranklist( ranked, ranking )

# We have now normalized conviences ranklist and can be treated as a feature.
def normalize_ranklist(ranked, ranklist):
    rank = 0.0
    for item in ranked:
        ranklist[ item[0] ] = rank / len( ranklist )
        rank = rank + 1

'''
    name : reduce_features
    @params : dictonary(key:name, value: rank), dictonary(key:name, value: rank)
    @return : dictonary(key:name, value: rank)
    @descp : This function takes two features in form of dictonaries and reduces to
    a ranked order of new single dictonary
    '''
def reduce_features(featurea, featureb):
    result = dict()
    temp_ordera = list()
    temp_orderb = list()
    places_names = list()
    temp_result = list()
    final_result = list()
    # the order is preserved ie. same for both features
    for key in featurea.keys():
        temp_ordera.append(featurea[key])
    for key in featureb.keys():
        temp_orderb.append(featureb[key])
        places_names.append(key) # Both features have same order to refer to place
    for x in range(0, len(temp_ordera)):
        temp_result.append([temp_ordera[x], temp_orderb[x]])
    for x in range(0, len(temp_ordera)):
        result[places_names[x]] = temp_result[x] # the new dictonary holds places as key and the feature pairs as values
    
    temp_result.sort() # sort the pair order this will be feature space scarpping from left to right

    for pair in temp_result:
        for key in result.keys():
            if result[key] == pair:
                final_result.append(key)
    final_result = final_result[::-1] # The order is in reverse so flip it

    return final_result


'''
    for key in featurea.keys():
    print key
    print temp_result[ref]
    result[temp_result[key]] = key
    ref = ref + 1 '''

def main(event_id, choice, point, radius):
    convience_feature = dict()
    rating_feature = dict()
    user = event_id.rpartition('-->')[0]
    user = user.rpartition('-->')[0]
    fb_ref = FIREBASE_URL + user + "/" + event_id
    fb = Firebase(fb_ref)
    data = fb.get()
    get_features(event_id, data, convience_feature, rating_feature, choice, point, radius)
    ranked_palaces = reduce_features(convience_feature, rating_feature)
    
    # look dictonary holds names as key and location as value
    look = dict()
    id_dict = dict()
    for x in range(0, len(names)):
        look[names[x]] = location[x]
        id_dict[names[x]] = id[x]
    
    #building json in ranked order
    data = {}
    rank = 0
    for place in ranked_palaces:
        data[id_dict[place]] = get_place_details(id_dict[place], rank, look[place])
        rank = rank + 1
    json_places_data = json.dumps(OrderedDict(data))

    print " "
    return json_places_data

if __name__ == "__main__":
	main(eventid, choice)
