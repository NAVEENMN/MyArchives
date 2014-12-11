from numpy import *
import random
from scipy.spatial import *
import urllib2
import json
from firebase import firebase
import time

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
    def __init__(self, id, latitude, longitude, rating, convience):
        self.id = id
        self.latitude = latitude
        self.longitude = longitude
        self.rating = rating
        self.convience = convience

class mpNode:
    def __init__(self, id, latitude, longitude, rating, convience):
        self.id = id
        self.latitude = latitude
        self.longitude = longitude
        self.rating = rating
        self.convience = convience

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
        print "net score: " + str(node.rating + node.convience)
        print "------------"

def create_dummy_nodes(number_of_nodes):
    nodes = list()
    #creating nodes
    for x in range(0, number_of_nodes):
        node = Node( None, None, None, None, None) # null id latitude longitude rating convience
        nodes.append(node)#nodes will have objects of ratings class
    return nodes

#load data to nodes
def load_fbdata_to_nodes(eventid,fb_res, nodes):
    i = 0
    for key in fb_res.keys():
        fb_lat = fb.get(eventid + key +'/Latitude', None)
        fb_lon = fb.get(eventid + key +'/Longitude', None)
        nodes[i].id = key
        nodes[i].latitude = fb_lat
        nodes[i].longitude = fb_lon
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

def req_convience_score(node, nodes):
    distances = list()
    posa = [node.latitude, node.longitude]
    for current_node in nodes:
        posb = [current_node.latitude, current_node.longitude]
        distances.append( get_distance(posa, posb))
    return sum(distances)

def normalize_node_conviences(nodes):
    convience_for_nodes = list()
    for node in nodes:
        convience_for_nodes.append(node.convience)
    high_convince = max(convience_for_nodes)
    for node in nodes:
        node.convience = 1 - (node.convience/high_convince)

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
    fb.put(eventid,"34112121014--mp", {'Latitude': node.latitude, 'Longitude': node.longitude})

def main():
    event_id = "event-859842507380812/" # this you will get from app
    fb_res =  fb.get(event_id, None)#get data from firebase
    number_of_nodes = len(fb_res.keys()) # get the number of nodes
    nodes = create_dummy_nodes(number_of_nodes) #create dummy n nodes
    load_fbdata_to_nodes(event_id, fb_res, nodes) # load data to nodes
    mpnode = initialize_mp_node(nodes) # centroid node
    #get the node scores (ratings scores and convience scores) and normalize them
    for node in nodes:
       score = req_node_score(str(node.latitude), str(node.longitude));
       convience = req_convience_score(node, nodes) # compare each node againts all other nodes
       node.rating = float(score)
       node.convience = convience
    normalize_node_scores(nodes)
    normalize_node_conviences(nodes)
    #-------------------------
    print_all_nodes(nodes) # before mp
    add_node_to_fb(event_id, mpnode)


if __name__ == "__main__":
    main()
