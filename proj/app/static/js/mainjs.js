/* This code is so ugly that my eyes hurt :(
 * Proudly govnokoding since 2010
 */

var questionNum = 0;
var correct = 0;
var totalQuestions = 10;

function pausecomp(millis)
 {
  var date = new Date();
  var curDate = null;
  do { curDate = new Date(); }
  while(curDate-date < millis);
}

function onLinkedInLoad() {
  IN.Event.on(IN, "auth", function() {onLinkedInLogin();});
}

function onLinkedInLogin() {
  // Get all the basic info about connections
  IN.API.Connections("me")
    .fields("firstName", "lastName", "positions", "headline", "picture-url", "location:(name)", "current-share")
    .result(function(result, metadata) {
      setConnections(result.values, metadata);
    });
}

function setConnections(conn) {
  connections = conn;

  if (connections.length < 10) {
    $("#nextText").text("");
    $("#inText").text("Sorry, you should have at least 10 connections to take the test. Connect with more people and come back!");
    return;
  }

  $("#inWidget").remove();
  $("#takeTestButton").show();
  $("#selector").show();

  $("#bt10").addClass("active");

  $("#bt10").click(function() {
    totalQuestions = 10;
    $("#bt10").addClass("active");
    $("#bt20").removeClass("active");
  });

  $("#bt20").click(function() {
    totalQuestions = 20;
    $("#bt10").removeClass("active");
    $("#bt20").addClass("active");
  });
}

function startTheTest() {
  $("#takeTestButton").hide();
  $("#two-columns").hide();
  $("#quizfield").show();

  generateQuestion();
}

/*
 * QUESTION TYPES END
 */

function generateQContent() {
  if (totalQuestions == 10) {
    if (questionNum <= 3) {
      return generateWhoIsQ();
    }
    if (questionNum <= 5) {
      return generateWhereWorksQ();
    }
    if (questionNum <= 7) {
      return generateAreaQ();
    }
    if (questionNum <= 8) {
      return generateHeadlineQ();
    }
    var ret = generateShareQ();
    if (ret >= 1)
      return ret;
    return generateWhereWorksQ();
  } else {
    if (questionNum <= 5) {
      return generateWhoIsQ();
    }
    if (questionNum <= 9) {
      return generateWhereWorksQ();
    }
    if (questionNum <= 12) {
      return generateAreaQ();
    }
    if (questionNum <= 15) {
      return generateHeadlineQ();
    }
    var ret = generateShareQ();
    if (ret >= 1)
      return ret;
    return generateWhereWorksQ();
  }
}

function switcher(i, bools){
  $("#option" + i).prop("disabled",bools);
}

function switchAll(bools) {
  for(var i=1; i<= 4;i++){
    switcher(i, bools);
  }
}

function setupHandler2(i, correctAnswer) {
  if (correctAnswer === i) {
    $("#option" + i).removeClass("optionCorrect");
  } else {
    $("#option" + i).removeClass("optionIncorrect");

    // Highlight correct answer as well
    $("#option" + correctAnswer).removeClass("optionCorrect");
    $("#option" + correctAnswer).addClass("optionDefault");
  }
  $("#option" + i).addClass("optionDefault");

  switchAll(false);

  generateQuestion();
}

function setupHandler1(i, correctAnswer) {
  $("#option" + i).removeClass("optionSelected");
  if (correctAnswer === i) {
    correct++; // Track correct answers count
    $("#option" + i).addClass("optionCorrect");
  } else {
    $("#option" + i).addClass("optionIncorrect");

    // Highlight correct answer as well
    $("#option" + correctAnswer).removeClass("optionDefault");
    $("#option" + correctAnswer).addClass("optionCorrect");
  }

  setTimeout(function() { setupHandler2(i, correctAnswer); }, 1800);
}

function setupHandler(i, correctAnswer) {
  $("#option" + i).click(function() {

    switchAll(true);

    $("#option" + i).removeClass("optionDefault");
    $("#option" + i).addClass("optionSelected");

    setTimeout(function() { setupHandler1(i, correctAnswer); }, 1200);
  });
}

function generateQuestion() {
  shuffle(connections);

  questionNum += 1;
  if (questionNum > totalQuestions) {
    $("#quizfield").hide();
    $("#endcolumns").show();
    $("#corc").text(correct);
    return;
  }

  $("#conPict").hide();
  
  // Set question contents
  var correctAnswer = generateQContent();

  if (correctAnswer == -1) {
    alert("Failed to generate a question");
  }

  $("#question").hide();
  $("#question").show();
  $("#qtrack").text("Question " + questionNum + "/" + totalQuestions);

  // Add handlers to button clicks.

  $("#option1").unbind();
  $("#option2").unbind();
  $("#option3").unbind();
  $("#option4").unbind();

  for (var idx = 1; idx <= 4;idx++) {
    setupHandler(idx, correctAnswer);
  }
}
