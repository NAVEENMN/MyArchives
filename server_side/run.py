import web
import json
#import ntp_handle_user as ntp

urls = (
        '/', 'index'
)

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
                data = json.dumps(web.input())
		print data
		#result = ntp.main(data.operation, data.payload)
        	return data

if __name__ == "__main__":
        app = web.application(urls, globals())
        web.httpserver.runsimple(app.wsgifunc(), ("0.0.0.0", 8888))
