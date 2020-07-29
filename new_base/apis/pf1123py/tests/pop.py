import time

def main():
	count = 0 
	while count < 5:
    		print "This prints once a minute."
    		time.sleep(20)  # Delay for 1 minute (60 seconds)
		count += 1

if __name__ == "__main__":
	main()
