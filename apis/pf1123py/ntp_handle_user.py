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
                minfo = df.get_data(payload)
                status = df.check_add_params(minfo) # check if we have correct json
                if ERRORS[status] == "M_OK":
                        minfo = df.get_data(payload)
                        df.insert_to_db(minfo)
                else:
                        print ERRORS[status]
if __name__ == "__main__":
        main()
