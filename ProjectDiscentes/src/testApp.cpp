#include "testApp.h"

int CircleRCircle = 25;//radius
int CircleRSphere = 25;//radius
int CircleRRectangle = 25;//radius
int CircleRCone = 25;//radius
int CircleRLine = 25;//radius
int CircleRCube = 25;//radius
int CircleRTriangle = 25;//radius
int CircleRCylinder = 25;//radius
int CircleRRead = 25;//radius
int CircleRDraw = 25;//radius
int mousex;
int mousey;
int i;
int Firstscreen = 1;
int vectorscreen = 0;
int graphscreen = 0;
int designscreen = 0;//DSO
int drawscreen = 0; // wiki
int entertainment = 0;
int backflag = 0;   // for storing converted value
int linkcounter = 0 ;

//-----------------drawgrap------------------------------------

string value;


//-------------------------------------------------------------
//--------------------------------------------------------------
void testApp::setup()
{

vect.circleflag = 0;
vect.circlex = 0;
vect.circley = 0;
h = 0 ;
k = 0 ;
ofBackground(255,255,255);
myfont.loadFont("sewer.ttf", 48);
myfont1.loadFont("cooperBlack.ttf", 13);
myfont2.loadFont("one.ttf", 13);
myfont3.loadFont("goodfoot.ttf", 22);
vect.setup();
vect.capturepoint = 0;
//-----------------

            cube.loadImage("images/cube.jpg");
            cone.loadImage("images/cone.jpg");
            sphere.loadImage("images/sphere.jpg");
            cylinder.loadImage("images/cylinder.jpg");
            triangle.loadImage("images/triangle.jpg");
            rectangle.loadImage("images/rectangle.png");
            line.loadImage("images/line.png");

//-----------------

    nextLetterTime = ofGetElapsedTimeMillis();
    lineCount      = 0;
    letterCount    = 0;

//-----------------



}

//--------------------------------------------------------------
void testApp::update(){


    vect.update();
    backflag = vect.localbackflag;
    if(backflag)
    {
             Firstscreen = 1;
             vectorscreen = 0;
             graphscreen = 0;
             designscreen = 0;
             drawscreen = 0;
             entertainment = 0;
             backflag = 0;
             vect.localbackflag = 0 ;
    }

}

//--------------------------------------------------------------
void testApp::draw(){

ofEnableAlphaBlending();
ofEnableSmoothing();
ofHideCursor();

if(Firstscreen == 1 ) {

//ofCircle( 356,355,200 );

myfont.drawString("Math Anywhere", 800,110);
myfont3.drawString("Do Real time geometrical Math", 900,133+30);
myfont2.drawString("Developed at Aayana 2013", 1062,704);

ofSetColor(0,0,255);
ofCircle(355,156,28);
ofSetColor(ofColor::black);

/*
myfont1.drawString("Pick the object.", 323,326);
myfont1.drawString("Circle", 355,156);
myfont1.drawString("Sphere", 355,555);
myfont1.drawString("Rectangle", 556,355);
myfont1.drawString("Cone", 156,355);
myfont1.drawString("Line", 474,193);
myfont1.drawString("cube", 194,193);
myfont1.drawString("Trinagle",494,496);
myfont1.drawString("Cylinder",219,496);
*/

//------------------------------------------ mousepointerS-------


//------------------------------------------ mousepointerE-------
//-------------------------------------------BubbleDrawS-------
    CircleRCircle = bubble[1].draw(355,156,CircleRCircle);
    CircleRSphere =  bubble[2].draw(355,555,CircleRSphere);
    CircleRRectangle = bubble[3].draw(556,355,CircleRRectangle);
    CircleRCone = bubble[4].draw(156,355,CircleRCone);
    CircleRLine = bubble[5].draw(518,238,CircleRLine);
    CircleRCube = bubble[6].draw(194,238,CircleRCube);
    CircleRTriangle = bubble[7].draw(517,473,CircleRTriangle);
    CircleRCylinder = bubble[8].draw(194,473,CircleRCylinder);
    CircleRRead = bubble[9].draw(282,284,CircleRRead);
    CircleRDraw = bubble[10].draw(437,284,CircleRDraw);

    myfont1.drawString("Capture", 240,330);
    myfont1.drawString("Plot", 414,330);
//--------------------------------------------BubbleDrawE--------

    if(linkcounter == 1)
    {
        myfont1.drawString("Data Entered is X co ordinate", 800,587);
    }
    if(linkcounter == 2)
    {
        myfont1.drawString("Data Entered is Y co ordinate", 800,587);
    }
    if(linkcounter == 3)
    {
        myfont1.drawString("Data Entered is z co ordinate", 800,587);
    }
    if(linkcounter == 4)
    {
        myfont1.drawString("Data Entered for value", 800,587);
    }
    if(linkcounter == 5)
    {
        myfont1.drawString("Data Entered for value", 800,587);
    }

    if(bubble[1].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for Circle", 800,131+80);
        myfont1.drawString("1) X Co ordinate; 2) Y Co ordinate;", 800,160+80);
        myfont1.drawString("3) Z Co ordinate; 4) Radius", 800,190+80);
    }

    if(bubble[2].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for Sphere", 800,131+80);
        myfont1.drawString("1) X Co ordinate; 2) Y Co ordinate;", 800,160+80);
        myfont1.drawString("3) Z Co ordinate; 4) Radius", 800,190+80);
    }

    if(bubble[3].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for rectangle", 780,131+80);
        myfont1.drawString("1) X Co ordinate; 2) Y Co ordinate;", 780,160+80);
        myfont1.drawString("3) Z Co ordinate; 4) Lenght; 5) Width", 780,190+80);
    }

    if(bubble[6].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for cuboid", 800,131+80);
        myfont1.drawString("1) X Co ordinate; 2) Y Co ordinate;", 800,160+80);
        myfont1.drawString("3) Z Co ordinate; 4) Lenght; 5) Width", 800,190+80);
    }

    if(bubble[4].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for cone", 800,131+80);
        myfont1.drawString("1) X Co ordinate; 2) Y Co ordinate;", 800,160+80);
        myfont1.drawString("3) Z Co ordinate; 4) Radius; 5) Height", 800,190+80);
    }

    if(bubble[7].flag == 1)
    {
        myfont1.drawString("Enter the data in the following order for Triangle", 800,131+80);
        myfont1.drawString("1) Three X Co ordinates; 2) Three Y Co ordinates;", 800,160+80);
        myfont1.drawString("3) Three Z Co ordinates", 800,190+80);
    }

//ofDrawBitmapString("value:"+ofToString(data), 700,700);


//------------------------------------
ofSetColor(255);
cube.draw(158, 209);
sphere.draw(320, 527);
cone.draw(127, 320);
triangle.draw(490,435);
rectangle.draw(517,327);
line.draw(479,210);
cylinder.draw(158, 445);

    ofSetColor(ofColor::black);
    mousex = ofGetMouseX();
    mousey = ofGetMouseY();
    ofDrawBitmapString("x:"+ofToString(mousex), mousex,mousey);
    ofDrawBitmapString("y:"+ofToString(mousey), mousex,mousey+9);
    ofNoFill();
    ofCircle(mousex,mousey,3);

}

if (vectorscreen == 1)
{
     vect.draw();
}

if (graphscreen == 1)
{
    //------------------------------------------ mousepointerS-------
    mousex = ofGetMouseX();
    mousey = ofGetMouseY();
    ofDrawBitmapString("x:"+ofToString(mousex), mousex,mousey);
    ofDrawBitmapString("y:"+ofToString(mousey), mousex,mousey+9);
    ofNoFill();
    ofSetColor(255, 0, 0);
    ofCircle(mousex,mousey,3);
    ofSetColor(0, 0, 0);
    myfont1.drawString(".Capture", 150,61);
    myfont1.drawString(".Plot", 93,104);
    myfont1.drawString(".Back", 72,168);
//------------------------------------------ mousepointerE-------
    myfont1.drawString(value, 200,200);

   // CircleRCapture = bubble[7].draw(150,61,CircleRCapture);
    //CircleRPlot  =  bubble[8].draw(93,104,CircleRPlot);
    //CircleRBack = bubble[9].draw(72,168,CircleRBack);
}

}

//--------------------------------------------------------------
void testApp::keyPressed(int key){
}

//--------------------------------------------------------------
void testApp::keyReleased(int key){
}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){

}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button)
{


    if( Firstscreen == 1 )
    {

            for( i = 1; i <= 10; i++  ) // condition it coz we may be in vector screen
                {
                    if( bubble[i].flag == 1)
                {
                    action(i);
                }
                }
    }



if (vectorscreen == 1)

{
    vect.capturepoint = 1;

    vect.gridclick();
}

if (graphscreen == 1)
{

    //for( i = 7; i <= 9; i++  ) // condition it coz we may be in vector screen
      //          {
                //    if( bubble[i].flag == 1)
        //        {
          //          action(i);
            //    }
              //  }

}

}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button)
{

    vect.releaseflag = 1;

}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){

}

//--------------------------------------------------------------
void testApp::gotMessage(ofMessage msg){

}

//--------------------------------------------------------------
void testApp::dragEvent(ofDragInfo dragInfo){

}
//--------------------------------------------------------------
void testApp::action(int f)
{
    switch(f)
    {
        case 1:
        {
            vect.circleflag = 1;
            vect.circlex = numb[0];
            vect.circley = numb[1];
            vect.circlez = numb[2];
            vect.circler = numb[3];
           // cout << dat[0];
            break;

        }

        case 2: //
        {
            vect.sphereflag = 1;
            vect.spherex = numb[0];
            vect.spherey = numb[1];
            vect.spherez = numb[2];
            vect.spherer = numb[3];
            break;
        }

        case 3: // DSO
        {
            vect.rectangleflag = 1;
            vect.rectanglex = numb[0];
            vect.rectangley = numb[1];
            vect.rectanglez = numb[2];
            vect.rectanglew = numb[3];
            vect.rectangleh = numb[4];
            break;
        }

        case 4: // draw --> wiki
        {

            vect.coneflag = 1;
            vect.conex = numb[0];
            vect.coney = numb[1];
            vect.conez = numb[2];
            vect.coner = numb[3];
            vect.coneh = numb[4];
            break;
        }

        case 5: // entertainment
        {
            ofDrawBitmapString("stat:5", 600,600);
            break;
        }

        case 6:
        {
            vect.cubeflag = 1;
            vect.cubex = numb[0];
            vect.cubey = numb[1];
            vect.cubez = numb[2];
            vect.cubel = numb[3];
            break;
        }

        case 7: // 7 8 9 are from draw frame be careful
        {


            vect.triangleflag = 1;
            vect.trianglex1 = numb[0];
            vect.triangley1 = numb[1];
            vect.trianglez1 = numb[2];
            vect.trianglex2 = numb[3];
            vect.triangley2 = numb[4];
            vect.trianglez2 = numb[5];
            vect.trianglex3 = numb[6];
            vect.triangley3 = numb[7];
            vect.trianglez3 = numb[8];

            break;

        }
         case 8:
        {
            ofDrawBitmapString("stat:8", 600,600);
            break;
        }
         case 9:
        {
            linkcounter++;
            system("python run.py");

             // this is our buffer to stroe the text data
    ofBuffer buffer = ofBufferFromFile("finaldata.txt");

    if(buffer.size()) {

        // we now keep grabbing the next line
        // until we reach the end of the file
        while(buffer.isLastLine() == false) {

            // move on to the next line
            string line = buffer.getNextLine();

            // copy the line to draw later
            // make sure its not a empty line
            if(line.empty() == false) {
                seussLines.push_back(line);
            }

            // print out the line
            //cout << line << endl;

            for (t = 0 ; t <= 20; t++ )
            {
                num = ofToString(t) ;
                if ( line == num)
                {
                       dat[h] = t;
                        h++;
                }



            }


        }


          if(dat[1] > 0)
          {
              numb[k] = dat[0]*10 + dat[1]*1 ;
          }
          else
          {
              numb[k] = dat[1]*10 + dat[0]*1 ;
          }
          k++;
          h = 0;
          if( vect.kflag == 1)
          {
              k = 0;
          }
          cout << numb[k] << endl ;

        }

        break;
        }

         case 10:
        {

             Firstscreen = 0;
             vectorscreen = 1;
             graphscreen = 0;
             designscreen = 0;
             drawscreen = 0;
             entertainment = 0;
             f = 0;
            break;
        }


        default:
        {
             Firstscreen = 1;
             vectorscreen = 0;
             graphscreen = 0;
             designscreen = 0;
             drawscreen = 0;
             entertainment = 0;
        }


    }


}
