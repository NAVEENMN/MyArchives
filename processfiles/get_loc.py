from numpy import *
import random
import scipy
from scipy.spatial import *
import urllib2
import json
from firebase import firebase
import time
import os, sys
'''
    mp: meet point
    This is the implementation of the find the best meeting place
    if all nodes have same node score 
        then find the centriod and find its node score : 
        if its higher then the scores of all nodes then
            return it
        else
            find other mp
    else
        do 10 iterations if you still dont find mp the node with high score will be mp
'''

fb = firebase.FirebaseApplication('https://met-ster-event.firebaseio.com/', None)

class Node:
    def __init__(self, id, latitude, longitude, rating, convience, travel):
        self.id = id
        self.latitude = latitude
        self.longitude = longitude
        self.rating = rating
        self.convience = convience
        self.travel = travel

class mpNode:
    def __init__(self, id, latitude, longitude, rating, convience):
        self.id = id
        self.latitude = latitude
        self.longitude = longitude
        self.rating = rating
        self.convience = convience
        self.travel = 0.2

#this function retuns the distance
def get_distance(a, b):
    return distance.euclidean(a, b)

def req_node_score(lat, long):
    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + long + "&radius=1600&types=food&keyword="+"&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE"
    response = urllib2.urlopen(url).read()
    jason_data = json.loads(response)
    score = 0
    for place in jason_data['results']:
        if 'rating' in place: # for some resuts we dont have ratings
            score = score + float(place['rating'])
    return score

def print_all_nodes(nodes):
    for node in nodes:
        print "id: " + str(node.id)
        print "Latitude: " + str(node.latitude)
        print "Longitude: " + str(node.longitude)
        print "ratings: " + str(node.rating)
        print "convience: " + str(node.convience)
        print "travel: " + str(node.travel)
        print "net score: " + str(node.rating + node.convience)
        print "------------"

def create_dummy_nodes(number_of_nodes):
    nodes = list()
    #creating nodes
    for x in range(0, number_of_nodes):
        node = Node( None, None, None, None, None, None) # null id latitude longitude rating convience
        nodes.append(node)#nodes will have objects of ratings class
    return nodes

#load data to nodes
def load_fbdata_to_nodes(eventid,fb_res, nodes):
    i = 0
    for key in fb_res.keys():
        if "*" in str(key):
            s = 0
            #non_person_count = non_person_count + 1
        else:
            fb_lat = fb.get(eventid + key +'/Latitude', None)
            fb_lon = fb.get(eventid + key +'/Longitude', None)
            fb_travel = fb.get(eventid + key +'/travel', None)
            travel_factor = float(fb_travel)
            nodes[i].id = key
            nodes[i].latitude = fb_lat
            nodes[i].longitude = fb_lon
            nodes[i].travel = travel_factor
            i = i+1

def normalize_node_scores(nodes):
    ratings_of_nodes = list()
    for node in nodes:
        ratings_of_nodes.append(node.rating)#ratings are in float
    high_rating = max(ratings_of_nodes)
    if high_rating == 0.0:
        node.rating = 0.0
    else:
        for node in nodes:#normalize
            node.rating = (node.rating/high_rating)
    return high_rating

def req_convience_score(node, nodes):
    distances = list()
    posa = [node.latitude, node.longitude]
    for current_node in nodes:
        posb = [current_node.latitude, current_node.longitude]
        distances.append( get_distance(posa, posb) * node.travel)
    return sum(distances)

def normalize_node_conviences(nodes):
    convience_for_nodes = list()
    for node in nodes:
        convience_for_nodes.append(node.convience)
    high_convince = max(convience_for_nodes)
    for node in nodes:
        node.convience = (node.convience/high_convince)

def initialize_mp_node(nodes):
    mpnode = mpNode(None,None,None,None,None) # dummy node
    latitudes = list()
    longitudes = list()
    mpnode.id = "211--mp" # time and meetup
    for node in nodes:
        latitudes.append(node.latitude)
        longitudes.append(node.longitude)
    #centriod of the graph
    mpnode.latitude = (sum(latitudes)/size(latitudes))
    mpnode.longitude = (sum(longitudes)/size(longitudes))
    #get convience score
    mpnode.convience = None
    #get ratings score
    mpnode.rating = None
    nodes.append(mpnode)
    return mpnode

def add_node_to_fb(eventid, node):
    fb.put(eventid,"70909141991*799--center", {'Latitude': node.latitude, 'Longitude': node.longitude})
def add_best_node_to_fb(eventid, lat, lng):
    fb.put(eventid,"70909141991*799--center", {'Latitude': lat, 'Longitude': lng})

def travel_prefrences(nodes,current_best_node, high_rating):
    car_lat = list()
    car_lng = list()
    bike_lat = list()
    bike_lng = list()
    bus_lat = list()
    walk_lat = list()
    bus_lng = list()
    walk_lng = list()
    car_centroid = [0,0]
    bus_centroid = [0,0]
    bike_centroid = [0,0]
    walk_centroid = [0,0]
    best_scores = 0.0
    best_centroid = [0,0]
    for node in nodes:
        if(node.travel == 5):
            car_lat.append(node.latitude)
            car_lng.append(node.longitude)
        if(node.travel == 4):
            bus_lat.append(node.latitude)
            bus_lng.append(node.longitude)
        if(node.travel == 3):
            bike_lat.append(node.latitude)
            bike_lng.append(node.longitude)
        if(node.travel == 1):
            walk_lat.append(node.latitude)
            walk_lng.append(node.longitude)
    for x in range(1, 6):
        if x == 1:
            if(len(walk_lat) >= 1):
                print "walk"
                walk_centroid = [sum(walk_lat)/len(walk_lat),sum(walk_lng)/len(walk_lng)]
                score = req_node_score(str(walk_centroid[0]), str(walk_centroid[1]));
                print walk_centroid
                print score/high_rating
                if score/high_rating > best_scores :
                    best_scores = score/high_rating
                    best_centroid = walk_centroid
        if x == 3:
            if(len(bike_lat) >= 1):
                print "bike"
                bike_centroid = [sum(bike_lat)/len(bike_lat),sum(bike_lng)/len(bike_lng)]
                score = req_node_score(str(bike_centroid[0]), str(bike_centroid[1]));
                print bike_centroid
                print score/high_rating
                if score/high_rating > best_scores :
                    best_scores = score/high_rating
                    best_centroid = bike_centroid
        if x == 4:
            if(len(bus_lat) >= 1):
                print "bus"
                walk_centroid = [sum(bus_lat)/len(bus_lat),sum(bus_lng)/len(bus_lng)]
                score = req_node_score(str(bus_centroid[0]), str(bus_centroid[1]));
                print bus_centroid
                print score/high_rating
                if score/high_rating > best_scores :
                    best_scores = score/high_rating
                    best_centroid = bus_centroid
        if x == 5:
            if(len(car_lat) >= 1):
                print "car"
                car_centroid = [sum(car_lat)/len(car_lat),sum(car_lng)/len(car_lng)]
                score = req_node_score(str(car_centroid[0]), str(car_centroid[1]));
                print car_centroid
                print score/high_rating
                if score/high_rating > best_scores :
                    best_scores = score/high_rating
                    best_centroid = car_centroid
    return best_centroid, best_scores

def main():
    #event_id = "event-859842507380812/" # this you will get from app
    event_id = str(sys.argv[1])+"/" # this you will get from app    
    fb_res =  fb.get(event_id, None)#get data from firebase
    person_count = 0
    non_person_count = 0
    current_best_node = None
    local_best = 0;
    local_best_loc = [0,0]
    #loop through and find the number of people
    for key in fb_res.keys():
        if "*" in str(key):
            non_person_count = non_person_count + 1
        else:
            person_count = person_count + 1
    print person_count
    nodes = create_dummy_nodes(person_count) #create dummy n nodes
    load_fbdata_to_nodes(event_id, fb_res, nodes) # load data to nodes
    mpnode = initialize_mp_node(nodes) # centroid node
    #get the node scores (ratings scores and convience scores) and normalize them
    for node in nodes:
       score = req_node_score(str(node.latitude), str(node.longitude));
       if score > local_best:
            local_best = score
            local_best_loc = [node.latitude, node.longitude]
       convience = req_convience_score(node, nodes) # compare each node againts all other nodes
       node.rating = float(score)
       node.convience = convience
    high_rating  = normalize_node_scores(nodes)
    normalize_node_conviences(nodes)

    #-------------------------
    print_all_nodes(nodes) # before mp
    current_best_node = mpnode
    preference = 1 #walk
    best_centroid, best_scores = travel_prefrences(nodes,current_best_node, high_rating)
    lt = (best_centroid[0]+mpnode.latitude)/2
    ln = (best_centroid[1]+mpnode.longitude)/2
    score = req_node_score(str(mpnode.latitude), str(mpnode.longitude));
    if(score == 0.0 and best_scores == 0.0):
        add_best_node_to_fb(event_id, local_best_loc[0], local_best_loc[1])
    else:
        if(score > best_scores):
            add_node_to_fb(event_id, mpnode)
        else:
            add_best_node_to_fb(event_id, lt, ln)		

if __name__ == "__main__":
    main()
