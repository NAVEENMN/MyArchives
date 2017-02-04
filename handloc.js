#!/usr/bin/env node

var leapjs  = require('leapjs');
var firebase = require("firebase-admin");
var stop_count  = 0;
var pitch_state = 0;

var keypath = require("/Users/naveenmysore/Documents/leap/projects/mathvr-73393-firebase-adminsdk-nxdqd-c1dc843c42.json");
firebase.initializeApp({
  credential: firebase.credential.cert(keypath),
  databaseURL: "https://mathvr-73393.firebaseio.com"
});

function startListeners() {
  firebase.database().ref('/posts').on('child_added', function(postSnapshot) {
    var postReference = postSnapshot.ref;
    var uid = postSnapshot.val().uid;
    var postId = postSnapshot.key;
    // Update the star count.
    // [START post_value_event_listener]
    postReference.child('stars').on('value', function(dataSnapshot) {
      updateStarCount(postReference);
      // [START_EXCLUDE]
      updateStarCount(firebase.database().ref('user-posts/' + uid + '/' + postId));
      // [END_EXCLUDE]
    }, function(error) {
      console.log('Failed to add "value" listener at /posts/' + postId + '/stars node:', error);
    });
    // [END post_value_event_listener]
    // Send email to author when a new star is received.
    // [START child_event_listener_recycler]
    postReference.child('stars').on('child_added', function(dataSnapshot) {
      sendNotificationToUser(uid, postId);
    }, function(error) {
      console.log('Failed to add "child_added" listener at /posts/' + postId + '/stars node:', error);
    });
    // [END child_event_listener_recycler]
  });
  console.log('New star notifier started...');
  console.log('Likes count updater started...');
}



function convertToRange(value, srcRange, dstRange){
  // value is outside source range return
  if (value < srcRange[0] || value > srcRange[1]){
    return NaN; 
  }

  var srcMax = srcRange[1] - srcRange[0],
      dstMax = dstRange[1] - dstRange[0],
      adjValue = value - srcRange[0];

  return (adjValue * dstMax / srcMax) + dstRange[0];

}

var controller = new leapjs.Controller({enableGestures: true});
      controller.loop(function(frame) {
        latestFrame = frame;
	var prevsroll = 0;
	var prevsyaw = 0;
	var prevspitch = 0;
        for (var i in frame.handsMap) {
          var hand = frame.handsMap[i];
	  var roll = Math.round(hand.roll() * 10);
	  var pitch = Math.round(hand.pitch() * 10);
          var yaw = Math.round(hand.yaw() * 10);
          var sroll = convertToRange(roll, [-8, 8], [-90, 90]);
	  var syaw = convertToRange(yaw, [-8, 8], [-90, 90]);
	  var spitch = convertToRange(pitch, [-8, 8], [-90, 90]);
	  sroll = Math.round(sroll / 10) * 10;
	  syaw = Math.round(syaw / 10) * 10;
	  spitch = Math.round(spitch / 10) * 10;
	  if (prevsroll !== sroll && !isNaN(sroll)){
	  	prevsroll = sroll;
		firebase.database().ref('/RightHand/xloc').set(sroll);
	  }
	  if (prevspitch !== spitch && !isNaN(spitch)){
		prevspitch = spitch;
		firebase.database().ref('/RightHand/zloc').set(spitch);
	  }
	  if (prevsyaw !== syaw && !isNaN(syaw)){
		prevsyaw = syaw;
	  	firebase.database().ref('/RightHand/yloc').set(syaw);
	  }
        }
      });
      controller.on('ready', function() {
          console.log("ready");
      });
      controller.on('connect', function() {
          console.log("connect");
      });
      controller.on('disconnect', function() {
          console.log("disconnect");
      });
      controller.on('focus', function() {
          console.log("focus");
      });
      controller.on('blur', function() {
          console.log("blur");
      });
      controller.on('deviceConnected', function() {
          console.log("deviceConnected");
      });
      controller.on('deviceDisconnected', function() {
          console.log("deviceDisconnected");
	  process.exit();  
      });

