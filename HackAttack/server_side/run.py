import web
import json
import sqlite3
import re
#import ntp_handle_user as ntp

conn = sqlite3.connect('/home/sysadmin/Documents/hackattack/server_side/decrypted_dd_hd.12071210.db')
c = conn.cursor()

urls = (
        '/', 'index'
)

def handle_query(query):
	i = 0
	points = dict()
	out = dict()
	finaldata = dict()
	payload = dict()
	finaldata["items_count"] = 0
	for row in c.execute(query):
		 points[i] = str(row)
		 i = i + 1
	finaldata["iterm_count"] = i
	if "hd_perf_cpus" in query:
		for x in range(0, len(points)):
			line = str(points[x])
			terms = line.split(",")
			for y in range(0, len(terms)):
				temp = terms[y].replace(" ", "")
				temp = re.sub('\W+','', temp )
				terms[y] = int(temp)
			x_point = terms[0]
			y_point = terms[1]
			label = dict()
			label["xpoint"] = x_point
			label["ypoint"] = y_point
			out[str(x)] = json.dumps(label)
		finaldata["points"] = json.dumps(out)
	else:
		finaldata["points"] = json.dumps(points)
	payload["qdata"] = json.dumps(finaldata) #final
	payload = json.dumps(payload)
	return payload
		

class index:
        def GET(self):
		print "get"
                i = 0
		dat = dict()
		dat["test"] = "test"
		payload = json.dumps(dat)
		return payload
        def POST(self):
		print "post"
		web.header('Access-Control-Allow-Origin',      '*')
		web.header('Access-Control-Allow-Credentials', 'true')
                data = web.input()
		data = data["qdata"]
		query = str(data)
		print "query: ", query
		payload = handle_query(query)
		#result = ntp.main(data.operation, data.payload)
        	return payload

if __name__ == "__main__":
        app = web.application(urls, globals())
        web.httpserver.runsimple(app.wsgifunc(), ("10.25.195.125", 8888))
