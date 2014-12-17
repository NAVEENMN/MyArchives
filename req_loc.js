var http = require('http');
var querystring = require('querystring');

function processPost(request, response, callback) {
    var queryData = "";
    if(typeof callback !== 'function') return null;

    if(request.method == 'POST') {
        request.on('data', function(data) {
            queryData += data;
            if(queryData.length > 1e6) {
                queryData = "";
                response.writeHead(413, {'Content-Type': 'text/plain'}).end();
                request.connection.destroy();
            }
        });

        request.on('end', function() {
            request.post = querystring.parse(queryData);
            callback();
        });

    } else {
        response.writeHead(405, {'Content-Type': 'text/plain'});
        response.end();
    }
}

http.createServer(function(request, response) {
    if(request.method == 'POST') {
        processPost(request, response, function() {
            console.log(request.post);
            // Use request.post here
	    var par = request.post.param2
	    var exec = require('child_process').exec, child;
		child = exec('python get_loc.py ' + par,
  		function (error, stdout, stderr) {
    			console.log('stdout: ' + stdout);
    			console.log('stderr: ' + stderr);
    		if (error !== null) {
      			console.log('exec error: ' + error);
   		 }
});	
	    console.log(par)

            response.writeHead(200, "OK", {'Content-Type': 'text/plain'});
            response.end('okok');
        });
    } else {
        response.writeHead(200, "OK", {'Content-Type': 'text/plain'});
        response.end();
    }

}).listen(8000);
