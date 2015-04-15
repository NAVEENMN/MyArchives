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
    clear_canvas('headline1');
    clear_canvas('headline2');
    clear_canvas('sec1');
    clear_canvas('sec2');
    clear_canvas('sec3');
    clear_canvas('sec4');
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

    var applicants = document.getElementById('headline1').getContext('2d'); applicants.font = "25px Georgia"; applicants.fillText("Total Applicants: " + graphs[6], 10, 30);
    var uniqueapplicants = document.getElementById('headline2').getContext('2d'); uniqueapplicants.font = "25px Georgia"; uniqueapplicants.fillText("Total Unique Applicants: " + graphs[9], 10, 30);
    // pies
    var gender = document.getElementById('sec1').getContext('2d'); var comgenchart = new Chart(gender).Pie(graphs[0], graphs[1]);
    document.getElementById("sec1legend").innerHTML = comgenchart.generateLegend();
    var race= document.getElementById('sec2').getContext("2d"); var comracechart = new Chart(race).Doughnut(graphs[2], graphs[3]);
    document.getElementById("sec2legend").innerHTML = comracechart.generateLegend();
    var level = document.getElementById('sec3').getContext("2d"); var comlevelchart = new Chart(level).Doughnut(graphs[4], graphs[5]);
    document.getElementById("sec3legend").innerHTML = comlevelchart.generateLegend();
    var ugender = document.getElementById('sec4').getContext("2d"); var comugenderchart = new Chart(ugender).Bar(graphs[7]);
    document.getElementById("sec4legend").innerHTML = comugenderchart.generateLegend();
    //var urace = document.getElementById('sec5').getContext("2d"); var comuracechart = new Chart(urace).Bar(graphs[8]);
    //document.getElementById("sec5legend").innerHTML = comuracechart.generateLegend();
    //elem.focus();
    
}

function setup_combined_views(graphs){
    var headline = document.getElementById('headline1').getContext('2d'); headline.font = "25px Georgia"; headline.fillText("Cumilative report from 2010 onwards", 10, 30);
    var comgender = document.getElementById('sec2').getContext('2d'); var comgenchart = new Chart(comgender).Bar(graphs[0], graphs[1]);
    document.getElementById("sec2legend").innerHTML = comgenchart.generateLegend();
    var comrace = document.getElementById('sec5').getContext('2d'); var comracechart = new Chart(comrace).Bar(graphs[2]);
    document.getElementById("sec5legend").innerHTML = comracechart.generateLegend();
    var comlevel = document.getElementById('sec4').getContext('2d'); var comlevelchart = new Chart(comlevel).Bar(graphs[3]);
    document.getElementById("sec4legend").innerHTML = comlevelchart.generateLegend();
    var comappicants = document.getElementById('sec1').getContext('2d'); var comapp = new Chart(comappicants).Line(graphs[4]);
    document.getElementById("sec1legend").innerHTML = comapp.generateLegend();
    var comfem = document.getElementById('sec3').getContext('2d'); var comfemapp = new Chart(comfem).Line(graphs[5]);
    document.getElementById("sec3legend").innerHTML = comfemapp.generateLegend();
    
    elem.focus();
}

