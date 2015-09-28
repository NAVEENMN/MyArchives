from firebase import Firebase
import os, sys

FIREBASE_URL = "https://met-ster.firebaseio.com/"

def drop_this_event_from_host_table(to, event_id):
	print "enter drop"
	fb = Firebase(FIREBASE_URL+to+"/joined/")
	data = fb.get()
	print "data"
	fb_keys = data.keys()
	for key in fb_keys:
		fb_joined = Firebase(FIREBASE_URL+to+"/joined/"+key)
		joined_data = fb_joined.get()
		if joined_data["event_reference"] == event_id:
			fb_joined.delete()

def main():
	to = sys.argv[1]
	event_id = sys.argv[2].replace("--","-->")
	drop_this_event_from_host_table(to, event_id)

if __name__ == "__main__":
	main()
