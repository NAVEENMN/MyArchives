import re
import json
import urllib2
import requests

from flask import request, render_template, send_from_directory
from app import app

from pykml.factory import KML_ElementMaker as KML
from pykml.factory import ATOM_ElementMaker as ATOM
from pykml.factory import GX_ElementMaker as GX
from lxml import etree

# Given the dictionaries, describing places, generated the kml file
def generate_kml(places):
	#doc = KML.kml(
	#    KML.Document(
	#        KML.Name("Awesome places"),
	#        KML.Style(
	#            KML.IconStyle(
	#                KML.scale(1.2),
	#                KML.Icon(
	#                    KML.href("http://maps.google.com/mapfiles/kml/pal4/icon28.png")
	#                ),
	#            )
	#        )
	#    )
	#)

	#for data in places:
	#	pm = KML.Placemark(
   	#		KML.name(data.get("name")),
   	#		KML.Point(KML.coordinates(str(data.get("lng")) + "," + str(data.get("lat"))))
  	#	)
  	#	doc.Document.append(pm)

	#result = etree.tostring(doc, pretty_print=True)
	#result.replace("placemark", "Placemark")
	#result.replace("point", "Point")

	result = '''<kml xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <Name>Awesome places</Name>
    <Style>
      <IconStyle>
        <scale>1.2</scale>
        <Icon>
          <href>http://maps.google.com/mapfiles/kml/pal4/icon28.png</href>
        </Icon>
      </IconStyle>
    </Style>'''

	for data in places:
		placement = '''<Placemark><name>%s</name>
      		<Point>
        		<coordinates>%f,%f</coordinates>
      		</Point>
    	</Placemark>''' % (data.get("name"), data.get("lng"), data.get("lat"))
		result += placement

  	result += '''</Document></kml>'''

	return result

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
	
def get_google_places(query, near):
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


@app.route('/generate', methods=['POST', 'GET'])
def generate():
	query = request.args.get('query')
	near = request.args.get('near')

	placesFoursquare = get_foursqure(query, near)
	placesGoogle = get_google_places(query, near)
	places = placesFoursquare + placesGoogle
	kml = generate_kml(places)

	os.remove("app/static/x.kml")
	f = open('app/static/x.kml', 'w+')
	f.write(kml)
	f.close()

	return "static/x.kml" # OK kml

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
