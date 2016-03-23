


#------------------------  MOVIES OPEARTION
def insert_movie(payload):
	return "OK"
def delete_movie(payload):
	return "OK"
def find_movie(payload):
	return "OK"
#------------------------------------------


#------------------------  THEATER OPEARTION
def insert_theater(payload):
        return "OK"
def delete_theater(payload):
        return "OK"
def find_thater(payload):
        return "OK"
#------------------------------------------


# 11 insert movie
# 12 delete movie
# 13 find movie
# 21 insert theater
# 22 delete theater
# 23 find theater
def main(oper, payload):
 	token = str(oper)
	if token[0] == 1: #movies
		if token[1] == 1: # insert
			res = insert_movie(payload)
		if token[1] == 2: # delete
			res = delete_movie(payload)
		if token[1] == 3: # find
			res = find_movie(payload)
	if token[0] == 2: #theater
		if token[1] == 1: # insert
			res = insert_theater(payload)
                if token[1] == 2: # delete
			res = delete_theater(payload)
                if token[1] == 3: # find
			res = find_theater(payload)

if __name__ == "__main__":
	main()
