var ge;
google.load("earth", "1", {
	"other_params" : "sensor=true_or_false"
});

function init() {
	google.earth.createInstance('map3d', initCB, failureCB);
	document.get
}

function initCB(instance) {
	ge = instance;
	ge.getWindow().setVisibility(true);
}

function failureCB(errorCode) {
}

google.setOnLoadCallback(init);

function getLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(showPosition);
	} else {
		x.innerHTML = "Geolocation is not supported by this browser.";
	}
}

function fullScreen(){
	var elem = document.getElementById("map3d");
	elem.focus();
	if (elem.requestFullscreen) {
	  elem.requestFullscreen();
	} else if (elem.msRequestFullscreen) {
	  elem.msRequestFullscreen();
	} else if (elem.mozRequestFullScreen) {
	  elem.mozRequestFullScreen();
	} else if (elem.webkitRequestFullscreen) {
	  elem.webkitRequestFullscreen();
	}
	return false;
}