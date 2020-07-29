from mapbox import Distance
from geojson import Point
import os
from mapbox import Geocoder
geocoder = Geocoder()
from pprint import pprint

service = Distance()

#portland = Point((37.4419959, -122.1614552))
#corvallis = Point((37.4619959, -122.1614552))

print geocoder.session.params['access_token']
token = '"pk.eyJ1IjoibXlzb3JuMSIsImEiOiJjaW1jcXZkd2UwMDI1dHNra3kyZzZ6YmZ5In0.XXYOEo0n7n0Kxg8ULBumAg"'
cmd = "export MAPBOX_ACCESS_TOKEN="+token
print cmd
os.system(cmd)
portland = {'type': 'Feature', 'properties': {'name': 'Portland, OR'}, 'geometry': {'type': 'Point','coordinates': [-122.7282, 45.5801]}}
corvallis = {'type': 'Feature', 'properties': {'name': 'Portland, OR'}, 'geometry': {'type': 'Point','coordinates': [-122.18111, 37.45234]}}

res = service.distances([portland, corvallis], 'driving')
#pprint(res.json()['durations'])
print res.json()
