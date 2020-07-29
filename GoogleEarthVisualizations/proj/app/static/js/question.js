/* This code is so ugly that my eyes hurt :(
 * Proudly govnokoding since 2010
 */

var connections; // Global variable that has data about all users connections
var previousAnswer;

function shuffle(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
}

function generateWhereWorksQ() {
  ans = Math.floor((Math.random()*4)+1);
  n = connections.length;
  
  var arr = []
  var names = []
  var work = []

  while(arr.length < 4){
    var randomnumber=Math.floor(Math.random()*n);
    var found=false;
    for(var i=0;i<arr.length;i++){
      if(arr[i]==randomnumber){found=true;break;}
    }
    if(!found) {

      if (connections[randomnumber] == undefined) {
        continue;
      }

      names[arr.length]=connections[randomnumber].firstName + " " + connections[randomnumber].lastName;

      if (connections[randomnumber].positions == undefined || connections[randomnumber].positions._total <= 0) {
        continue;
      }

      if (arr.length == ans - 1 && connections[randomnumber].positions.values[0].company.name == previousAnswer) {
        continue;
      }

      var dup = false;
      for(var i=0;i<arr.length;i++){
        if(work[i] == connections[randomnumber].positions.values[0].company.name) {dup=true;break;}
      }
      if (dup) {
        continue;
      }

      work[arr.length]=connections[randomnumber].positions.values[0].company.name;
      arr[arr.length]=randomnumber;
    }
  }

  $("#question").html("Which of your connections below works at <span style=\"color: #00a5ff\">" + work[ans - 1] + "</span>?");
  $("#option1").text(names[0]);
  $("#option2").text(names[1]);
  $("#option3").text(names[2]);
  $("#option4").text(names[3]);

  previousAnswer = work[ans-1];

  return ans; // Return index of correct q
}

function generateWhoIsQ() {
  ans = Math.floor((Math.random()*4)+1);
  n = connections.length;

  var arr = []
  var names = []

  while(arr.length < 4){
    var randomnumber=Math.floor(Math.random()*n);
    var found=false;
    for(var i=0;i<arr.length;i++){
      if(arr[i]==randomnumber){found=true;break;}
    }
    if(!found) {

      if (connections[randomnumber] == undefined) {
        continue;
      }

      names[arr.length]=connections[randomnumber].firstName + " " + connections[randomnumber].lastName;

      if (connections[randomnumber].pictureUrl == undefined) {
        continue;
      }

      if (arr.length == ans - 1 && names[arr.length] == previousAnswer) {
        continue;
      }

      arr[arr.length]=randomnumber;
    }
  }

  $("#question").text("Which of your connections is on the picture below?");
  $("#option1").text(names[0]);
  $("#option2").text(names[1]);
  $("#option3").text(names[2]);
  $("#option4").text(names[3]);

  previousAnswer = names[ans-1];

  $("#conPict").show();
  $("#conPict").attr("src",connections[arr[ans-1]].pictureUrl);

  return ans; // Return index of correct q
}

function generateAreaQ() {
  ans = Math.floor((Math.random()*4)+1);
  n = connections.length;

  var arr = []
  var names = []
  var area = []

  while(arr.length < 4){
    var randomnumber=Math.floor(Math.random()*n);
    var found=false;
    for(var i=0;i<arr.length;i++){
      if(arr[i]==randomnumber){found=true;break;}
    }
    if(!found) {

      if (connections[randomnumber] == undefined) {
        continue;
      }

      names[arr.length]=connections[randomnumber].firstName + " " + connections[randomnumber].lastName;

      if (connections[randomnumber].location == undefined) {
        continue;
      }

      if (arr.length == ans - 1 && connections[randomnumber].location.name == previousAnswer) {
        continue;
      }

      var dup = false;
      for(var i=0;i<arr.length;i++){
        if(area[i] == connections[randomnumber].location.name) {dup=true;break;}
      }
      if (dup) {
        continue;
      }

      area[arr.length]=connections[randomnumber].location.name;
      arr[arr.length]=randomnumber;
    }
  }

  $("#question").html("Which of your connections below lives in <span style=\"color: #00a5ff\">" + area[ans - 1] + "</span>?");
  $("#option1").text(names[0]);
  $("#option2").text(names[1]);
  $("#option3").text(names[2]);
  $("#option4").text(names[3]);

  previousAnswer = area[ans - 1];

  return ans; // Return index of correct q
}

function generateHeadlineQ() {
  ans = Math.floor((Math.random()*4)+1);
  n = connections.length;

  var arr = []
  var names = []
  var hd = []

  while(arr.length < 4){
    var randomnumber=Math.floor(Math.random()*n);
    var found=false;
    for(var i=0;i<arr.length;i++){
      if(arr[i]==randomnumber){found=true;break;}
    }
    if(!found) {

      if (connections[randomnumber] == undefined) {
        continue;
      }

      names[arr.length]=connections[randomnumber].firstName + " " + connections[randomnumber].lastName;

      if (connections[randomnumber].headline == undefined || connections[randomnumber].headline.length == 0) {
        continue;
      }

      if (arr.length == ans - 1 && connections[randomnumber].headline == previousAnswer) {
        continue;
      }

      var dup = false;
      for(var i=0;i<arr.length;i++){
        if(hd[i] == connections[randomnumber].headline) {dup=true;break;}
      }
      if (dup) {
        continue;
      }

      hd[arr.length]=connections[randomnumber].headline;
      arr[arr.length]=randomnumber;
    }
  }

  $("#question").html("Which of your connections is (a/an) <span style=\"color: #00a5ff\">" + hd[ans - 1] + "</span>?");
  $("#option1").text(names[0]);
  $("#option2").text(names[1]);
  $("#option3").text(names[2]);
  $("#option4").text(names[3]);

  previousAnswer = hd[ans - 1];

  return ans; // Return index of correct q
}

function generateShareQ() {
  ans = Math.floor((Math.random()*4)+1);
  n = connections.length;

  var idx = 1;
  var names = [];

  while (idx != 0) {
    if (idx == previousAnswer) {
      idx = (idx + 1) % n;
      continue;
    }
    if (!(connections[idx] == undefined || connections[idx].currentShare == undefined ||
          connections[idx].currentShare.content == undefined || connections[idx].currentShare.content.title == undefined)) {
      break;
    }
    idx = (idx + 1) % n;
  }

  if (idx == 0)
    return -1;

  $("#question").html("Which of your connections shared <span style=\"color: #00a5ff\">" + connections[idx].currentShare.content.title + "</span>?");
  
  $("#option" + ans).text(connections[idx].firstName + " " + connections[idx].lastName);
  
  var bad = (idx + 1) % n;
  for (var i = 1; i <= 4; i++) {
    if (i == ans) {
      continue;
    }
    $("#option" + i).text(connections[bad].firstName + " " + connections[bad].lastName);
    bad = (bad + 1) % n;
  }

  previousAnswer = idx;

  return ans; // Return index of correct q
}
