function load_year_data(year){
    var myFirebaseRef = new Firebase("https://cise.firebaseio.com/");
    var universities; var umales; var ufemales; var uunspecified;
    var utotals;
    var uwhites; var uafros; var uasians; var uhisps; var multis;
    // gender variables
    var male; var female; var unspecified;
    // racial variables
    var causian; var africanamerican; var asian; var hispanic; var multi;
    // level variables
    var freshman; var sopohomore; var junior; var senior;
    // applicants
    var totalapplicants; var uniqueapplicants;
    // level variables
    var graphs = new Array(12);
    // Data from Firebase ------------------------------------
    //------ GENDER
    myFirebaseRef.child("Totals/"+year+"/gender/Male").on("value", function(snapshot) {
                                                      male = snapshot.val();
                                                      });
    myFirebaseRef.child("Totals/"+year+"/gender/Female").on("value", function(snapshot) {
                                                        female = snapshot.val();
                                                        });
    myFirebaseRef.child("Totals/"+year+"/gender/unspecified").on("value", function(snapshot) {
                                                             unspecified = snapshot.val();
                                                             });
    //------ RACIAL
    myFirebaseRef.child("Totals/"+year+"/race/White").on("value", function(snapshot) {
                                                     causian = snapshot.val();
                                                     });
    myFirebaseRef.child("Totals/"+year+"/race/Afro American").on("value", function(snapshot) {
                                                             africanamerican = snapshot.val();
                                                             });
    myFirebaseRef.child("Totals/"+year+"/race/Asian").on("value", function(snapshot) {
                                                     asian = snapshot.val();
                                                     });
    myFirebaseRef.child("Totals/"+year+"/race/Hispanic").on("value", function(snapshot) {
                                                        hispanic = snapshot.val();
                                                        });
    myFirebaseRef.child("Totals/"+year+"/race/others").on("value", function(snapshot) {
                                                      multi = snapshot.val();
                                                      });
    //------- LEVEL
    myFirebaseRef.child("Totals/"+year+"/level/freshman").on("value", function(snapshot) {
                                                         freshman = snapshot.val();
                                                         });
    myFirebaseRef.child("Totals/"+year+"/level/sopohomore").on("value", function(snapshot) {
                                                           sopohomore = snapshot.val();
                                                           });
    myFirebaseRef.child("Totals/"+year+"/level/junior").on("value", function(snapshot) {
                                                       junior = snapshot.val();
                                                       });
    myFirebaseRef.child("Totals/"+year+"/level/senior").on("value", function(snapshot) {
                                                       senior = snapshot.val();
                                                       });
    //--------- applicants
    myFirebaseRef.child("Totals/"+year+"/total/Total Unique applicants").on("value", function(snapshot) {
                                                                        uniqueapplicants = snapshot.val();
                                                                        });
    myFirebaseRef.child("Totals/"+year+"/total/total applicants").on("value", function(snapshot) {
                                                                 totalapplicants = snapshot.val();
                                                                 });
    //-------- university ugender
    myFirebaseRef.child("Totals/"+year+"/universities/list").on("value", function(snapshot) {
                                                            universities = snapshot.val();
                                                            });
    myFirebaseRef.child("Totals/"+year+"/MALES/listmales").on("value", function(snapshot) {
                                                          umales = snapshot.val();
                                                          });
    myFirebaseRef.child("Totals/"+year+"/FEMALES/listfemales").on("value", function(snapshot) {
                                                              ufemales = snapshot.val();
                                                              });
    myFirebaseRef.child("Totals/"+year+"/UNSPECIFIED/listunspecified").on("value", function(snapshot) {
                                                                      uunspecified = snapshot.val();
                                                                      });
    
    myFirebaseRef.child("Totals/"+year+"/whites/listwhites").on("value", function(snapshot) {
                                                          uwhites = snapshot.val();
                                                          });
    myFirebaseRef.child("Totals/"+year+"/afros/listafros").on("value", function(snapshot) {
                                                              uafros = snapshot.val();
                                                              });
    myFirebaseRef.child("Totals/"+year+"/asians/listasians").on("value", function(snapshot) {
                                                                      uasians = snapshot.val();
                                                                      });
    myFirebaseRef.child("Totals/"+year+"/hisps/listhisps").on("value", function(snapshot) {
                                                            uhisps = snapshot.val();
                                                            });
    myFirebaseRef.child("Totals/"+year+"/multis/listmultis").on("value", function(snapshot) {
                                                            umultis = snapshot.val();
                                                            });

    
    //---------------------------------------------------------
    // gender pie graph data
    var genderData = [
                      {
                    		value: male,
                    		color:"#F7464A",
                      highlight: "#FF5A5E",
                      label: "Male"
                      },
                      {
                      value: female,
                      color: "#46BFBD",
                      highlight: "#5AD3D1",
                      label: "Female"
                      },
                      {
                    		value : unspecified,
                    		color: "#FDB45C",
                      highlight: "#FFC870",
                      label: "Unspecified"
                      }
                      ];
    var genderOptions = {
        segmentShowStroke : false,
        animateScale : true
    }
    // race doughnut graph data
    var raceData = [
                    {
                    value: causian,
                    color:"#F7464A",
                    highlight: "#FF5A5E",
                    label: "Caucasian"
                    },
                    {
                    value : africanamerican,
                    color : "#46BFBD",
                    highlight: "#5AD3D1",
                    label: "African American"
                    },
                    {
                    value: asian,
                    color:"#FDB45C",
                    highlight: "#FFC870",
                    label: "Asian"
                    },
                    {
                    value : hispanic,
                    color : "#878BB6",
                    highlight: "#8E91B3",
                    label: "Hispanic/Latino"
                    },
                    {
                    value : multi,
                    color : "#2ff560",
                    highlight: "#4cfa78",
                    label: "Multiracial"
                    }
                    
                    ];
    
    var raceOptions = {
        segmentShowStroke : false,
        animateScale : true
    }
    
    var levelData = [
                     {
                     value: freshman,
                     color:"#F7464A",
                     highlight: "#FF5A5E",
                     label: "Freshman"
                     },
                     {
                     value : sopohomore,
                     color : "#46BFBD",
                     highlight: "#5AD3D1",
                     label: "Sophomore"
                     },
                     {
                     value: junior,
                     color:"#FDB45C",
                     highlight: "#FFC870",
                     label: "Junior"
                     },
                     {
                     value : senior,
                     color : "#878BB6",
                     highlight: "#8E91B3",
                     label: "Senior"
                     }
                     
                     ];
    var levelOptions = {
        segmentShowStroke : false,
        animateScale : true
    }
    
    
    var ugenderchart = {
    labels: universities,
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

/*
    var uracechart = {
    labels: universities,
    datasets: [
               {
               label: "Caucasian",
            			fillColor: "#48A497",
            			strokeColor: "#48A4D1",
            			data: uwhites
               },
               label: "Africanamerican",
            			fillColor: "#48A457",
            			strokeColor: "#4854D1",
            			data: uwhites
               },
               label: "Asian",
            			fillColor: "#48A457",
            			strokeColor: "#45A4D1",
            			data: uwhites
               },
               label: "HispanicLatino",
            			fillColor: "#48A597",
            			strokeColor: "#45A4D1",
            			data: uwhites
               },
               {
               label: "multiracial",
            			fillColor: "rgba(73,188,170,0.4)",
            			strokeColor: "rgba(151,187,205,0.8)",
            			data: uwhites
               }
               ]
    }
*/
    graphs[0] = genderData;
    graphs[1] = genderOptions;
    graphs[2] = raceData;
    graphs[3] = raceOptions;
    graphs[4] = levelData;
    graphs[5] = levelOptions;
    graphs[6] = totalapplicants;
    graphs[7] = ugenderchart;
    //graphs[8] = uracechart;
    graphs[9] = uniqueapplicants;
    return graphs;
}
