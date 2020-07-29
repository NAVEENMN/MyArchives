import re
import json
import urllib2
import requests
import os
import sys, traceback
import string
import random
import math
from flask import request, render_template, send_from_directory
from app import app

def generate_circle_points(lng, lat):
	res = ""
	delta = 0.002

	for i in xrange(360):
		res += str(lng + (delta * math.cos( ( 2 * 3.14159 * i ) / 360 ))) + "," + str(lat + (delta * math.sin( ( 2 * 3.14159 * i ) / 360 ))) + ",500\n"
	res += str(lng + (delta * math.cos(0))) + "," + str(lat + (delta * math.sin(0))) + ",500\n"

	return res

def generate_placemark(name, lng, lat):
	circle_points = generate_circle_points(lng, lat)

	plc = '''
  <Folder>
    <name>Place data</name>
    <visibility>1</visibility>
    <Placemark>
      <name>circle</name>
      <visibility>1</visibility>
      <Style>
        <geomColor>ff0000ff</geomColor>
        <geomScale>1</geomScale>
      </Style>
	    <Style id="exampleBalloonStyle">
			<BalloonStyle>
			  <!-- a background color for the balloon -->
			  <bgColor>ffffffbb</bgColor>
			  <!-- styling of the balloon text -->
			  <text><![CDATA[
			  <b><font color="#CC0000" size="+3">%s</font></b>
			  <br/><br/>
			  <font face="Courier">%s</font>
			  <br/><br/>
			  Extra text that will appear in the description balloon
			  <br/><br/>
			  ]]></text>
			</BalloonStyle>
			 <geomColor>ff0000ff</geomColor>
        <geomScale>1</geomScale>
		  </Style>
	<Polygon>
	  <!-- specific to Polygon -->
	  <name>BalloonStyle</name>
	  <extrude>1</extrude>                       <!-- boolean -->
	  <tessellate>0</tessellate>                 <!-- boolean -->
	  <altitudeMode>relativeToGround</altitudeMode>
	  <styleUrl>#exampleBalloonStyle</styleUrl>
	  <outerBoundaryIs>
		<LinearRing>
		          <coordinates> 
		          	%s
					</coordinates>
		</LinearRing>
	  </outerBoundaryIs>
	</Polygon>
    </Placemark>
  </Folder>
	''' % (name, name, circle_points)

	#placement = '''<Placemark><name>%s</name>
    #  		<Point>
    #    		<coordinates>%f,%f</coordinates>
    #  		</Point>
    # 	</Placemark>''' % (data.get("name"), data.get("lng"), data.get("lat"))
	return plc

def id_generator(size=6, chars=string.ascii_uppercase + string.digits):
	return ''.join(random.choice(chars) for x in range(size))

# Given the dictionaries, describing places, generated the kml file
def generate_kml(places):
	result = '''<?xml version="1.0" encoding="UTF-8"?>
				<kml xmlns="http://www.opengis.net/kml/2.2"><Folder>'''

	for data in places:
		result += generate_placemark(data.get("name"), data.get("lng"), data.get("lat"))

  	result += '''</Folder></kml>'''

	return result.encode('utf-8')

def get_foursqure(query, near):
	assert query != None
	assert near != None

	CLIENT_ID = "YTA4TE2OHYRRJKNSH0IISLA2ISF0EZRRUUAQMICXZLAJY5CT"
	CLIENT_SECRET = "NMCQ5Z2W3RUJWWS1G2M2OFDFSUYQ5XGIITGERPCDAJ15KL1R"
	to_parse="https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&near=" + near + "&query=" + query + "&v=20130815"
	
	fields = requests.get(to_parse).content
	obj = json.loads(fields)
	
	res = []
	venues = obj.get("response").get("venues")

	if not venues:
		return res

	for item in venues:
		try:
			generated = {"name": item.get("name"), "lat": item.get("location").get("lat"), "lng": item.get("location").get("lng"), "checkins": item.get("stats").get("checkinsCount")}
			res.append(generated)
		except:
			pass

	return res

def get_google(query, near):
	assert query != None
	assert near != None
	API_KEY = "AIzaSyA8H80z37hBd_lLpowHKl73l2wP650VUEs"
	to_parse="https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "+in+" + near +"&sensor=false&key="+API_KEY
	
	fields = requests.get(to_parse).content
	objs - json.loads(fields)
	res = []
	for item in obj.get("results"):
		try:
			generated = {"name": item.get("name"), "lat": item.get("geometry").get("lat"), "lng": item.get("geometry").get("lng"), "rating": item.get("rating")}
			res.append(generated)
		except:
			pass
	return res


@app.route('/generate', methods=['GET'])
def generate():

	query = request.args.get('query')
	near = request.args.get('near')
	
	places_foursqaure = get_foursqure(query, near)
	places_google = get_foursqure(query, near)

	places_combined = places_foursqaure
	for place in places_google:
		places_combined.append(place)

	kml = generate_kml(places_combined)
	
	id = id_generator()
	f = open('app/static/x' + id + '.kml', 'w+')

	try:
		f.write(kml)
		f.close()
	except:
		return request.url_root + 'static/x' + id + '.kml'

	return request.url_root + 'static/x' + id + '.kml' # OK (kml)

@app.route('/')
@app.route('/home')
def main():
    return render_template("index.html")

@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404

@app.errorhandler(500)
def page_not_found(e):
    return render_template('500.html'), 500
