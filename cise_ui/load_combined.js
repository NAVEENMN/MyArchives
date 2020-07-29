function load_combined_data(type){

    var myFirebaseRef = new Firebase("https://cise.firebaseio.com/");
    var year; var umales; var ufemales;
    // racial variables
    var causian; var africanamerican; var asian; var hispanic;
    // level variables
    var freshman; var sopohomore; var junior; var senior;
    // applicants
    var unique; var oneplus;
    var graphs = new Array(10);
    var fem;
    //-------- year ugender
    myFirebaseRef.child("Totals/combined/years/list").on("value", function(snapshot) {
                                                            year = snapshot.val();
                                                            });
    myFirebaseRef.child("Totals/combined/males/listmales").on("value", function(snapshot) {
                                                          umales = snapshot.val();
                                                          });
    myFirebaseRef.child("Totals/combined/females/listfemales").on("value", function(snapshot) {
                                                              ufemales = snapshot.val();
                                                              });
    
    //------ RACIAL
    myFirebaseRef.child("Totals/combined/white/listwhite").on("value", function(snapshot) {
                                                     causian = snapshot.val();
                                                     });
    myFirebaseRef.child("Totals/combined/afr/listafr").on("value", function(snapshot) {
                                                             africanamerican = snapshot.val();
                                                             });
    myFirebaseRef.child("Totals/combined/asia/listasia").on("value", function(snapshot) {
                                                     asian = snapshot.val();
                                                     });
    myFirebaseRef.child("Totals/combined/his/listhis").on("value", function(snapshot) {
                                                        hispanic = snapshot.val();
                                                        });
    
    //------- LEVEL
    myFirebaseRef.child("Totals/combined/fresh/freshman").on("value", function(snapshot) {
                                                         freshman = snapshot.val();
                                                         });
    myFirebaseRef.child("Totals/combined/sop/sophomore").on("value", function(snapshot) {
                                                           sopohomore = snapshot.val();
                                                           });
    myFirebaseRef.child("Totals/combined/jun/juniour").on("value", function(snapshot) {
                                                       junior = snapshot.val();
                                                       });
    myFirebaseRef.child("Totals/combined/sen/seniour").on("value", function(snapshot) {
                                                       senior = snapshot.val();
                                                       });
    //------- applicants
    
    myFirebaseRef.child("Totals/combined/oneplus/onep").on("value", function(snapshot) {
                                                          oneplus = snapshot.val();
                                                          });
    myFirebaseRef.child("Totals/combined/uniqueapplicants/unique").on("value", function(snapshot) {
                                                          unique = snapshot.val();
                                                          });
    
    myFirebaseRef.child("Totals/combined/fem/fembased").on("value", function(snapshot) {
                                                                      fem = snapshot.val();
                                                                      });

    var genderOptions = {
        legendTemplate : '<ul>'
        +'<% for (var i=0; i<datasets.length; i++) { %>'
        +'<li>'
        +'<span style=\"background-color:<%=datasets[i].lineColor%>\"></span>'
        +'<% if (datasets[i].label) { %><%= datasets[i].label %><% } %>'
        +'</li>'
        +'<% } %>'
        +'</ul>',
        segmentShowStroke : false,
        animateScale : true
    }
    
    var ugenderchart = {
    labels: year,
    datasets: [
               {
               label: "Males",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: umales
               },
               {
               label: "Females",
            			fillColor: "rgba(73,188,170,0.4)",
            			strokeColor: "rgba(151,187,205,0.8)",
            			data: ufemales
               }
               ]
    }
    
    var uracechart = {
    labels: year,
    datasets: [
               {
               label: "caucasian",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: causian
               },
               {
               label: "asian",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: asian
               },
               {
               label: "hispanic/latino",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: hispanic
               },
               {
               label: "African American",
            			fillColor: "rgba(73,188,170,0.4)",
            			strokeColor: "rgba(151,187,205,0.8)",
            			data: africanamerican
               }
               ]
    }
    
    var ulevelchart = {
    labels: year,
    datasets: [
               {
               label: "freshman",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: freshman
               },
               {
               label: "sophomore",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: sopohomore
               },
               {
               label: "Junior",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: junior
               },
               {
               label: "Senior",
            			fillColor: "rgba(73,188,170,0.4)",
            			strokeColor: "rgba(151,187,205,0.8)",
            			data: senior
               }
               ]
    }
    
    
    var comapp = {
    labels: year,
    datasets: [
               {
               label: "Total Unique Applicants",
               fillColor: "rgba(220,220,220,0.2)",
               strokeColor: "rgba(220,220,220,1)",
               pointColor: "rgba(220,220,220,1)",
               pointStrokeColor: "#fff",
               pointHighlightFill: "#fff",
               pointHighlightStroke: "rgba(220,220,220,1)",
               data: unique
               },
               {
               label: "Applicants applying for one plus sites",
               fillColor: "rgba(151,187,205,0.2)",
               strokeColor: "rgba(151,187,205,1)",
               pointColor: "rgba(151,187,205,1)",
               pointStrokeColor: "#fff",
               pointHighlightFill: "#fff",
               pointHighlightStroke: "rgba(151,187,205,1)",
               data: oneplus
               }
               ]
    };
    
    var fembasedon = {
    labels: year,
    datasets: [
               {
               label: "Females based on total number of applicants",
               fillColor: "rgba(220,220,220,0.2)",
               strokeColor: "rgba(220,220,220,1)",
               pointColor: "rgba(220,220,220,1)",
               pointStrokeColor: "#fff",
               pointHighlightFill: "#fff",
               pointHighlightStroke: "rgba(220,220,220,1)",
               data: fem
               }
               ]
    };
    
    graphs[0] = ugenderchart;
    graphs[1] = genderOptions;
    graphs[2] = uracechart;
    graphs[3] = ulevelchart;
    graphs[4] = comapp;
    graphs[5] = fembasedon;
    
    return graphs;
}

