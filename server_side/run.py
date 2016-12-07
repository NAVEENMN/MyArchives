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
		web.header('Access-Control-Allow-Origin',      '*')
		web.header('Access-Control-Allow-Credentials', 'true')
                data = json.dumps(web.input())
		dat = dict()
		i = 2
		if data == "DD_HD_PERF_NODE_SFS_RD_STREAM":
			i = 4	
		if data == "DD_HD_PERF_NODE_SFS_RD_RAND_STREAM":
			i = 3
		if data == "DD_HD_PERF_NODE_SFS_WR_STREAM":
			i = 8
		if data == "DD_HD_PERF_NODE_SFS_WR_RAND_STREAM":
			i = 9
		if data == "DD_HD_PERF_NODE_SFS_RD_REOPENED_STREAM":
			i = 7
		if data == "DD_HD_PERF_NODE_SFS_WR_REOPENED_STREAM":
			i = 5
		if data == "DD_HD_PERF_NODE_REPL_IN_STREAMS":
			i = 5
		if data == "DD_HD_PERF_NODE_REPL_OUT_STREAMS":
			i = 2
		for x in range(0, i):
			name = "ddr"+str(i)
			dat[name] = "98"
		payload = json.dumps(dat)
		print payload
		#result = ntp.main(data.operation, data.payload)
        	return payload

if __name__ == "__main__":
        app = web.application(urls, globals())
        web.httpserver.runsimple(app.wsgifunc(), ("0.0.0.0", 8888))
