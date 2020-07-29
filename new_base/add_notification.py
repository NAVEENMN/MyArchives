from firebase import Firebase
import os, sys
# this is only a reference file
FIREBASE_URL = "https://met-ster.firebaseio.com/"

def add_notification(to):
	message = "			We are still small, please tell your friends about this app so hanging out in group using Metster would be more fun!!			"
	fb = Firebase(FIREBASE_URL+to+"/notifications/message")
	fb.put(message)

def main():
	to = sys.argv[1]
	add_notification(to)
	
if __name__ == "__main__":
	main()
