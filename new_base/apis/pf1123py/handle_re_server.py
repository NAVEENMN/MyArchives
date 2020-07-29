import web
import ntp_handle_user as ntp

urls = (
        '/', 'index'
)

class index:
        def GET(self):
                i = 0
        def POST(self):
                data = web.input()
		result = ntp.main(data.operation, data.payload)
                return result

if __name__ == "__main__":
        app = web.application(urls, globals())
        web.httpserver.runsimple(app.wsgifunc(), ("0.0.0.0", 8888))
