r_id = dict()
#dbs accounts (1), theater (2), movies(3), food(4), restauraunts(5)
#db requests 1000 - 5000
#example to insert into mongo accounts table 111000
#to insert into firebase 211000
# starts with 1
# <mongo/firebase> <tableid> <3digitoperations>
r_id[1000] = "insert"
r_id[1001] = "delete"
r_id[1002] = "find"
r_id[1003] = "update"
r_id[1004] = "show_all"
#-----------
r_id[9000] = "find_food"
r_id[9001] = "find_movies"
#-----------
