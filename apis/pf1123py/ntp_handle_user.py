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
		print status, minfo
                if ERRORS[status] == "M_OK":
                        status, minfo = df.frame_data("ADB", payload)
			if status == 1:
                        	df.insert_to_db(minfo)
			else :
				print "unable to insert"
                else:
                        print ERRORS[status]
			df.m_log(0,"main", "invalid inputs")
		df.show_db("accounts")
if __name__ == "__main__":
        main()







