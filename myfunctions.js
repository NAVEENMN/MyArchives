function select_view(type) {
    switch(type){
        case 'year':
            clear_year_canvas();
            document.getElementById("dash").innerHTML = "";
            //Create an input type dynamically.
            var button = new Array(4);
            var base_year = 2010;
            for(i=0; i< 6; i++){
                button[i] = document.createElement("input");
                button[i].type = 'button';
                button[i].value = base_year.toString();
                base_year += 1;
                button[i].name = 'year';
                button[i].onclick = function(){
                    $.when( overall_display('year') ).done(function() {
                                           overall_display('year');
                    });
                    
                }
                var attach_button = document.getElementById("dash");
                attach_button.appendChild(button[i]);
            }//for
            break;
        case 'combined':
            clear_year_canvas();
            document.getElementById("dash").innerHTML = "";
            $.when( overall_display('combined') ).done(function() {
                                                   overall_display('combined');
                                                   });
            
            break;
        default:
            document.getElementById("dash").innerHTML = "";
            break;
    }//switch
}//select view

function clear_year_canvas(){
    //------ clear canvas
    clear_canvas('sec1');
    clear_canvas('sec2');
    clear_canvas('sec3');
    clear_canvas('ugender');
    clear_canvas('applicants');
    clear_canvas('uniqueapplicants');
    //-------------------
    return true;
}

function clear_canvas(canvas_id){
    var canvas = document.getElementById(canvas_id);
    var context = canvas.getContext('2d');
    context.clearRect(0,0,canvas.width,canvas.height);
    return true;
}

function overall_display(view_type){
    switch(view_type){
            case 'year':
                        $.when( graphs = load_year_data("overall") ).done(function() {
                                setup_year_views(graphs);
                        });
                        break;
            case 'combined':
                        $.when( graphs = load_combined_data("combined") ).done(function() {
                                setup_combined_views(graphs);
                        });
            
                        break;
    }
    return true;
}

function setup_year_views(graphs){
    
    var applicants = document.getElementById('applicants').getContext('2d'); applicants.font = "25px Georgia"; applicants.fillText("Total Applicants: " + graphs[6], 10, 30);
    var uniqueapplicants = document.getElementById('uniqueapplicants').getContext('2d'); uniqueapplicants.font = "25px Georgia"; uniqueapplicants.fillText("Total Unique Applicants: " + graphs[8], 10, 30);
    // pies
    var gender = document.getElementById('sec1').getContext('2d'); new Chart(gender).Pie(graphs[0], graphs[1]);
    var race= document.getElementById('sec2').getContext("2d"); new Chart(race).Doughnut(graphs[2], graphs[3]);
    var level = document.getElementById('sec3').getContext("2d"); new Chart(level).Doughnut(graphs[4], graphs[5]);
    // charts
    //var gdt = document.getElementById('genderdistributiontitle').getContext('2d'); gdt.font = "15px Georgia"; gdt.fillText("Gender Distribution");
    var ugender = document.getElementById('ugender').getContext("2d"); new Chart(ugender).Bar(graphs[7]);
    
    //elem.focus();
    
}

function setup_combined_views(graphs){
    var comgender = document.getElementById('sec1').getContext('2d'); new Chart(comgender).Bar(graphs[0]);
    var comrace = document.getElementById('sec2').getContext('2d'); new Chart(comrace).Bar(graphs[2]);
    var comlevel = document.getElementById('sec3').getContext('2d'); new Chart(comlevel).Bar(graphs[3]);
    elem.focus();
}

