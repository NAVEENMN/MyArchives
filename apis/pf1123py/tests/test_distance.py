from mapbox import Distance
from geojson import Point
import os
from mapbox import Geocoder
geocoder = Geocoder()

service = Distance()

#portland = Point((37.4419959, -122.1614552))
#corvallis = Point((37.4619959, -122.1614552))

print geocoder.session.params['access_token']

portland = {'type': 'Feature', 'properties': {'name': 'Portland, OR'}, 'geometry': {'type': 'Point','coordinates': [-122.7282, 45.5801]}}
corvallis = portland

print portland
res = service.distances([portland, corvallis], 'driving')
print res.status_code
print res.headers['Content-Type']
print res.json()
