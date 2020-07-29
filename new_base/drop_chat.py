from firebase import Firebase
import os, sys

FIREBASE_URL = "https://metster-chat.firebaseio.com/"

def drop_this_event_from_host_table(to, event_id):
	print "enter drop"
	fb = Firebase(FIREBASE_URL+event_id)
	fb.delete()

def main():
	to = sys.argv[1]
	event_id = sys.argv[2].replace("--","-->")
	drop_this_event_from_host_table(to, event_id)

if __name__ == "__main__":
	main()
