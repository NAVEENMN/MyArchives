import sys
import db_functions as df
import numpy as np
from error_id import ERRORS
from numpy import matrix
from numpy import array

def main():
        operation = sys.argv[1]
        payload = sys.argv[2]
        if operation == "add":
        	status, minfo = df.frame_data("ADB", payload)
		if status == 1:
                	status = df.insert_to_db("ADB", minfo)
			if status == 1:
				print "inserted"
			else :
				print ERRORS[status]
		else :
			print ERRORS[status]
		#df.show_db("accounts")
if __name__ == "__main__":
        main()







