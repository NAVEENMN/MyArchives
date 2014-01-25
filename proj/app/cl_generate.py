import json
import requests

from app import app

from pykml.factory import KML_ElementMaker as KML
from pykml.factory import ATOM_ElementMaker as ATOM
from pykml.factory import GX_ElementMaker as GX
from lxml import etree

# Given the dictionaries, describing places, generated the kml file
def generate_kml(places):
	doc = KML.kml(
	    KML.Document(
	        KML.Name("Awesome places"),
	        KML.Style(
	            KML.IconStyle(
	                KML.scale(1.2),
	                KML.Icon(
	                    KML.href("http://maps.google.com/mapfiles/kml/pal4/icon28.png")
	                ),
	            )
	        )
	    )
	)

	for data in places:
		pm = KML.Placemark(
   			KML.name(data.get("name")),
   			KML.Point(KML.coordinates(str(data.get("lng")) + "," + str(data.get("lat"))))
  		)
  		doc.Document.append(pm)

	result = etree.tostring(doc, pretty_print=True)
	result.replace("placemark", "Placemark")
	result.replace("point", "Point")

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
	for item in obj.get("response").get("venues"):
		try:
			generated = {"name": item.get("name"), "lat": item.get("location").get("lat"), "lng": item.get("location").get("lng"), "checkins": item.get("stats").get("checkinsCount")}
			res.append(generated)
		except:
			pass

	return res

query = "pizza"
near = "Santa Monica"

places = get_foursqure(query, near)

kml = generate_kml(places)
f = open('app/static/x.kml', 'w')
f.write(kml)
f.close()
