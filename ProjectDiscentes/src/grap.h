#pragma once

#include "ofMain.h"
#include "ofxVectorGraphics.h"
#include "Bubble.h"


class grap
{
    public:

    ofxVectorGraphics output;
    ofVec3f X,Y,Z;
    ofEasyCam cam;
    ofMesh mesh;
    ofTrueTypeFont myfonts;
    ofTrueTypeFont myfonts1;
    int i, mx, my, mouseX, mouseY, mousex, mousey;
    int circlex;
    int circley;
    int circlez;
    float circler;
    int circleflag;
    int spherex;
    int spherey;
    int spherez;
    int spherer;
    int sphereflag;
    int cubex;
    int cubey;
    int cubez;
    int cubel;
    int cubeflag;
    int conex;
    int coney;
    int conez;
    int coner;
    int coneh;
    int coneflag;
    int trianglex1;
    int triangley1;
    int trianglez1;
    int trianglex2;
    int triangley2;
    int trianglez2;
    int trianglex3;
    int triangley3;
    int trianglez3;
    int triangleflag;
    int rectanglex;
    int rectangley;
    int rectanglez;
    int rectanglew;
    int rectangleh;
    int rectangleflag;
    Bubble bubblevect[10];// three used
    int CircleRdot;//radius
    int CircleRenterpoint;//radius
    int CircleRscale;//radius
    int CircleRadd;//radius
    int CircleRsub;//radius
    int CircleRclear;//radius
    int CircleRundo;//radius
    int CircleRcross;// radius
    int CircleRBack;// back
    int doton;
    int zcd; // z co ordinate
    int ycd; //y co ordinate
    int xcd; // x co ordinate
    int xst;
    int yst;
    int zst;
    int xend;
    int yend;
    int zend;
    int takepoint;
    int capturepoint;
    int x[100];// 10 vector and its x position
    int y[100];// 10 vector and its y position
    int z[100];// 10 vector and its z position
    ofVec3f points[100];
    ofVec3f resultant;
    ofVec3f inter;
    int leng; // number of points selected
    int dat;
    string num;
    int scaleflag;
    int datarry[50];// average out
    int newdat;// for averaging
    int j;
    int n;
    int releaseflag;
    int localbackflag;
    int kflag;

    //



    //forback vector refrence
    //
    //-------------------------------------------
    // Scling

        bool		bSendSerialMessage;			// a flag for sending serial
		char		bytesRead[3];				// data from serial, we will be trying to read 3
		char		bytesReadString[4];			// a string needs a null terminator, so we need 3 + 1 bytes
		int			nBytesRead;					// how much did we read?
		int			nTimesRead;					// how many times did we read?
		float		readTime;					// when did we last read?
		ofSerial	serial;



    //-------------------------------------------

     //----------------------------------------------------------
        void setup()
        {


            kflag = 0;

            ofSetVerticalSync(true);
            mesh.load("lofi-bunny2.ply");
            myfonts.loadFont("sewer.ttf", 23);
            myfonts1.loadFont("cooperBlack.ttf", 13);
            resultant.set(0,0,0);
            localbackflag = 0;


            //---------  sacling

    dat = 1;
    ofSetVerticalSync(true);

	bSendSerialMessage = false;
	ofBackground(255);
	ofSetLogLevel(OF_LOG_VERBOSE);

	serial.listDevices();
	vector <ofSerialDeviceInfo> deviceList = serial.getDeviceList();

	// this should be set to whatever com port your serial device is connected to.
	// (ie, COM4 on a pc, /dev/tty.... on linux, /dev/tty... on a mac)
	// arduino users check in arduino app....
	serial.setup(0, 9600); //open the first device
	//serial.setup("COM4"); // windows example
	//serial.setup("/dev/tty.usbserial-A4001JEC",9600); // mac osx example
	//serial.setup("/dev/ttyUSB0", 9600); //linux example
    serial.setup("/dev/ttyACM0", 9600);
	//nTimesRead = 0;
	//nBytesRead = 0;
	readTime = 0;
	memset(bytesReadString, 0, 4);



//---------




        }


        //----------------update



        void update(){

        bSendSerialMessage = true;

	if (bSendSerialMessage){

		// (1) write the letter "a" to serial:
		serial.writeByte('a');

		// (2) read
		// now we try to read 3 bytes
		// since we might not get them all the time 3 - but sometimes 0, 6, or something else,
		// we will try to read three bytes, as much as we can
		// otherwise, we may have a "lag" if we don't read fast enough
		// or just read three every time. now, we will be sure to
		// read as much as we can in groups of three...

		//nTimesRead = 0;
		//nBytesRead = 0;
		int nRead  = 0;  // a temp variable to keep count per read

		unsigned char bytesReturned[3];

		memset(bytesReadString, 0, 4);
		memset(bytesReturned, 0, 3);

		while( (nRead = serial.readBytes( bytesReturned, 3)) > 0)
		{
			nTimesRead++;
			nBytesRead = nRead;
		};

		memcpy(bytesReadString, bytesReturned, 3);

		bSendSerialMessage = false;
		readTime = ofGetElapsedTimef();
	}
}





        //-----------------------







            void draw()
                {
                        ofBackground(255,255,255);
                        //ofSetColor(ofColor::black);
                        //myfonts.drawString("Vector Playground", 33,33);
                        output.setLineWidth(1);
                        output.setColor(0x000000);
                        output.setLineWidth(2);
                        ofFill();
                        ofSetLineWidth(3);
                        ofSetColor(0, 0 , 255);
                        ofNoFill();
                        ofEnableSmoothing();
                        CircleRdot = bubblevect[1].draw(200,86,CircleRdot);
                        CircleRenterpoint = bubblevect[2].draw(281,43,CircleRenterpoint);
                        CircleRscale = bubblevect[3].draw(140,150,CircleRscale);
                        CircleRadd = bubblevect[4].draw(113,224,CircleRadd);
                        CircleRsub = bubblevect[5].draw(96,292,CircleRsub);
                        CircleRclear = bubblevect[6].draw(87,369,CircleRclear);
                        CircleRundo = bubblevect[7].draw(79,446,CircleRundo);
                        CircleRcross = bubblevect[8].draw(97,522,CircleRcross);
                        CircleRBack = bubblevect[9].draw(133,590,CircleRBack);

                        X.set(380,0,0);
                        Y.set(0,380,0);
                        Z.set(0,0,380);


                        //----------scaing



                        if (nBytesRead > 0 && ((ofGetElapsedTimef() - readTime) < 0.5f))
                        {
                        ofSetColor(0);
                        }
                        else
                        {
                        ofSetColor(220);
                        }
	string msg;
	msg += "read: " + ofToString(bytesReadString) + "\n";
	//msg += "(at time " + ofToString(readTime, 3) + ")";
	//dat = bytesReadString;
	myfonts.drawString(msg, 1176, 711);

	////////////////////////////////////////////////////   Scaling area
//-----------------------------------------------

ofSetColor(0,0,0);

myfonts1.drawString("Draw Vector.", 90,47);
myfonts1.drawString("Reference.", 56,92);
myfonts1.drawString("Walk.", 51,157);
myfonts1.drawString("Add.", 30,224);
myfonts1.drawString("Sub.", 12,290);
myfonts1.drawString("Clear.", 3,375);
myfonts1.drawString("Undo.", 9,448);
myfonts1.drawString("Cross.", 11,524);
myfonts1.drawString("Back.", 29,603);

//myfonts1.drawString("Resultant:"+ofToString( resultant ), 60,524);

//-----------------------------------------------


//-----------------------------average out
for(i=0; i< 50; i++)
{

            for (j=0; j <= 150; j++)  //------------------------------
{



    if(  bytesReadString == num) dat = j ;
    num = ofToString( j );
    //if( *bytesReadString > '100' ) dat = 1 ;
    //if( *bytesReadString == '0' ) dat = 10 ;
}//-------------------------------------------------- converting string to number in dat int


    datarry[i] = dat;
}



for(i=0; i< 50; i++)
{
    newdat = newdat + datarry[i];
}


newdat = newdat / 50;

newdat = 150 - newdat;

newdat = newdat / 10 ;

//newdat = ( -9 / 150 ) * newdat + 10;                                           //  y = mx+c;  1 = 150 m + c; 10 = 0 m + c;

myfonts.drawString( ofToString(newdat), 1287,699);





//--------------------------------------------------- average


//-------------------------------
/*if( *bytesReadString >= '0' and *bytesReadString <= '10' ) dat = 10 ;
if( *bytesReadString > '10' and *bytesReadString <= '20' ) dat = 9 ;
if( *bytesReadString > '20' and *bytesReadString <= '30' ) dat = 8 ;
if( *bytesReadString > '30' and *bytesReadString <= '40' ) dat = 7 ;
if( *bytesReadString > '40' and *bytesReadString <= '50' ) dat = 6 ;
if( *bytesReadString > '50' and *bytesReadString <= '60' ) dat = 5 ;
if( *bytesReadString > '60' and *bytesReadString <= '70' ) dat = 4 ;
if( *bytesReadString > '70' and *bytesReadString <= '80' ) dat = 3 ;
if( *bytesReadString > '80' and *bytesReadString <= '90' ) dat = 2 ;
if( *bytesReadString > '90' and *bytesReadString <= '100' ) dat = 1 ;
*/
	///////////////////////////////////////////////////   Scaling area






                        //-----------------


                        cam.begin();



                                            if(scaleflag) ofScale(newdat,newdat,newdat);
                                            output.setColor(0xEEEEEE);
                                            //////////////////////////////////////////////////////////////// GRID start
                                            //////////////////////////////////////// grid x y plane
                                            for(int y = 0; y < 38; y++)
                                            {
                                                ofLine(0, y * 10, 0 , 380, y * 10, 0);
                                            }

                                            for(int x = 0; x < 38; x++){
                                                ofLine(x * 10, 0, 0, x * 10, 380, 0 );
                                            }
                                            /////////////////////////////////////// grid x y plane

                                            /////////////////////////////////////// grid x z plane
                                            for(int y = 0; y < 38; y++)
                                            {
                                                ofLine(0, 0 , y * 10, 380, 0, y * 10);
                                            }

                                            for(int x = 0; x < 38; x++)
                                            {
                                                ofLine(x * 10, 0, 0, x * 10, 0, 380 );
                                            }
                                            ////////////////////////////////////// grid x z plane
                                            /////////////////////////////////////// grid z y plane
                                            for(int y = 0; y < 38; y++)
                                            {
                                                ofLine(0, 0 , y * 10, 0, 380, y * 10);
                                            }

                                            for(int x = 0; x < 38; x++)
                                            {
                                                ofLine(0, x * 10, 0, 0, x * 10, 380 );
                                            }
                                            ////////////////////////////////////// grid z y plane
                                            ////////////////////////////////////////////////////////////////////////////// GRID END

                                            ofSetLineWidth(3);
                                            ofSetColor(0, 0 , 255);

                                            ofLine(0, 0, 0, X.x, X.y, X.z);
                                            ofLine(0, 0, 0, Y.x, Y.y, Y.z);
                                            ofLine(0, 0, 0, Z.x, Z.y, Z.z);
                                            ofDrawBitmapString("X axis", X.x, X.y, X.z);
                                            ofDrawBitmapString("Y axis", Y.x, Y.y, Y.z);
                                            ofDrawBitmapString("Z axis", Z.x, Z.y, Z.z);






                                            //-------------------------------------   Scaling
                                            //points[1][0][0] = xst * 20;
                                            //points[0][1][0] = yst * 20;
                                            //points[0][0][1] = zst * 20;
                                            ;
                                            //--------------------------------------   Scaling

                                            ofSetColor(0,0,0);

                                            //for( i = 0; i <= leng; i++)
                                            //{
                                              //  vectordata( i, 1, 1, 1);
                                           // }






                                            for(i=2; i<= leng; i++)
                                            {

                                                ofSetColor(255,0,200);
                                                ofLine( x[i-1] , y[i-1] , z[i-1], x[i], y[i], z[i]);
                                                ofSetColor(0,0,255);
                                                ofLine(x[i] - x[i-1],y[i] - y[i-1], z[i] - z[i-1], 0, 0, 0 );
                                                //ofTriangle(x[i]+1, y[i]+1 , z[i]+1,((x[i])-1), ((y[i])-1), ((z[i])-1), ((x[i])+1), ((y[i])+1), ((z[i])+1));

                                                //------------storing vectors


                                                points[i/2].set( x[i] - x[i-1],y[i] - y[i-1], z[i] - z[i-1]);


                                                //------------storing vectors
                                                ofSetColor(255,0,0);

                                                ofLine(resultant.x,resultant.y,resultant.z, 0, 0, 0 );

                                                ofSetColor(ofColor::black);

                                                ofDrawBitmapString( ofToString(x[i]/20)+"i+"+ofToString(y[i]/20)+"j+"+ofToString(z[i]/20)+"k", x[i],y[i],z[i]);
                                                ofDrawBitmapString( ofToString((x[i] - x[i-1])/20)+"i+"+ofToString((y[i] - y[i-1])/20)+"j+"+ofToString((z[i] - z[i-1])/20)+"k", x[i] - x[i-1],y[i] - y[i-1], z[i] - z[i-1]);
                                                ofDrawBitmapString( ofToString(x[i-1]/20)+"i+"+ofToString(y[i-1]/20)+"j+"+ofToString(z[i-1]/20)+"k", x[i-1],y[i-1],z[i-1]);
                                                ofDrawBitmapString( "V"+ofToString(i/2), (x[i]+x[i-1])/2,(y[i]+y[i-1])/2,(z[i]+z[i-1])/2);
                                                ofDrawBitmapString( "--->", (x[i]+x[i-1])/2,((y[i]+y[i-1])/2)+6,(z[i]+z[i-1])/2);
                                                ofDrawBitmapString( "V"+ofToString(i/2), (x[i]-x[i-1])/2,(y[i]-y[i-1])/2,(z[i]-z[i-1])/2);
                                                ofDrawBitmapString( "--->", (x[i]-x[i-1])/2,((y[i]-y[i-1])/2)+6,(z[i]-z[i-1])/2);
                                                ofDrawBitmapString( ofToString(resultant.x/20)+"i+"+ofToString(resultant.y/20)+"j+"+ofToString(resultant.z/20)+"k", resultant.x,resultant.y,resultant.z);
                                                i++;// This is done to draw seperate vectors.

                                            }


                                            ofSetColor(0,0,255);

                                            //----------

                                            if(circleflag == 1)
                                            {

                                                ofSetColor(255,0,255);
                                                ofCircle(circlex * 20,circley * 20,circlez * 20,circler * 20);
                                                ofDrawBitmapString("x:"+ofToString(circlex), 800,800);
                                                ofDrawBitmapString("y:"+ofToString(circley), 800,820);
                                                ofDrawBitmapString("z:"+ofToString(circlez), 800,840);
                                                ofDrawBitmapString("rad:"+ofToString(circler), 800,860);

                                            }
                                            if(sphereflag == 1)
                                            {

                                                ofSetColor(0,255,255);
                                                ofSphere(spherex * 20,spherey * 20,spherez * 20,spherer * 20);

                                            }

                                            if(cubeflag == 1)
                                            {
                                                ofSetColor(0,255,0);
                                                ofBox(cubex * 20,cubey * 20,cubez * 20,cubel * 20);
                                            }

                                             if(rectangleflag == 1)
                                            {

                                                ofSetColor(255,255,0);
                                                ofRect(rectanglex * 20,rectangley * 20,rectanglez * 20,rectanglew * 20,rectangleh * 20);

                                            }

                                            if(coneflag == 1)
                                            {

                                                ofSetColor(255,255,0);
                                                ofCone(conex * 20,coney * 20,conez * 20,coner * 20,coneh * 20);

                                            }

                                            if(triangleflag == 1)
                                            {

                                                ofSetColor(255,255,0);
                                                ofTriangle(trianglex1 * 20, triangley1 * 20, trianglez1 * 20, trianglex2 * 20, triangley2 * 20, trianglez2 * 20, trianglex3 * 20, triangley3 * 20, trianglez3 * 20);

                                            }

                                            //-----------
                                            /*

                                            trial code for drawing a lines

                                            for(i=0; i<= size; i++)
                                            {

                                                ofLine( x[i] , y[i] , z[i], x[i+1], y[i+1], z[i+1])

                                            }


                                            */


for( i = 0; i <= 380; i = i+20 ) // for axis numbers
{
    ofFill();
    ofSetColor(0, 0 ,255);
    ofSphere( i, 0, 0, 2);
    ofSetColor(0, 0 ,0);
    ofDrawBitmapString(ofToString(i/20), i, 2, 0);
    if( i > 0)
    {   ofSetColor(0, 0 ,255);
        ofSphere( 0, i, 0, 2);
        ofSetColor(0, 0 ,0);
        ofDrawBitmapString(ofToString(i/20), 2, i, 0);
        ofSetColor(0, 0 ,255);
        ofSphere( 0, 0, i, 2);
        ofSetColor(0, 0 ,0);
        ofDrawBitmapString(ofToString(i/20), 0, 2, i);
    }

}


                                            ofSetColor(ofColor::gray);


                                                glPointSize(2);
                                                ofSetColor(ofColor::black);
                                                if(doton)
                                                {
                                                   mesh.drawVertices();
                                                }

                                                mx = ofGetMouseX();
                                                my = ofGetMouseY();
                                                mouseX = mx;
                                                mouseY = my;

                            cam.end();

    int n = mesh.getNumVertices();
	float nearestDistance = 0;
	ofVec2f nearestVertex;
	int nearestIndex;
	ofVec2f mouse(mouseX, mouseY);


if(takepoint == 1)

{
//------------------------------------------------------------------
	for(int i = 0; i < n; i++)
	{
		ofVec3f cur = cam.worldToScreen(mesh.getVertex(i));
		float distance = cur.distance(mouse);
		if(i == 0 || distance < nearestDistance)
		{
			nearestDistance = distance;
			nearestVertex = cur;
			nearestIndex = i;
		}
	}
//-------------------------------------------------------------------


	ofSetColor(ofColor::gray);
	ofLine(nearestVertex, mouse);

	ofNoFill();
	ofSetColor(ofColor::yellow);
	ofSetLineWidth(2);
	ofCircle(nearestVertex, 4);
	ofSetLineWidth(1);

	ofVec2f offset(10, -10);
	ofDrawBitmapStringHighlight(ofToString(nearestIndex), mouse + offset);


    xcd = nearestIndex / 400 ;
	ycd = (nearestIndex - (xcd * 400 ))/20;
	zcd = (( nearestIndex - (xcd * 400) ) - (ycd * 20));


	if(capturepoint == 1)
	{
            xst = xcd;
            yst = ycd;
            zst = zcd;

            if(bubblevect[2].flag == 0)
            {
                leng += 1;
            }
            if(bubblevect[2].flag == 0 )
        {
            x[leng] = 20 * xst;
            y[leng] = 20 * yst;
            z[leng] = 20 * zst;
        }

            capturepoint = 0;
	}





    ofSetColor(0,0,0);


    ofDrawBitmapString( "x:"+ofToString(xcd)+" y:"+ofToString(ycd)+" z:"+ofToString(zcd), 20,654);

    ofDrawBitmapString( "xset:"+ofToString(xst)+" yset:"+ofToString(yst)+" zset:"+ofToString(zst), 20,670);

}


    ofSetColor(255, 0, 0);
    mx = ofGetMouseX();
    my = ofGetMouseY();

    ofCircle(ofGetMouseX(),ofGetMouseY(),3);
    ofDrawBitmapString("x:"+ofToString(mx), mx,my);
    ofDrawBitmapString("y:"+ofToString(my), mx,my+9);


    }





    void gridclick(void)

{

    for( i = 1; i<= 10; i++ )

    {
        if( bubblevect[i].flag == 1 )
        {
            actioninvector(i);
        }

    }
}



    void actioninvector(int u)
    {

        switch(u)
        {
            case 1:
                {
                    doton = not(doton);
                    break;
                }

            case 2:
                {
                    takepoint = not(takepoint);
                    break;
                }

            case 3:
                {
                    scaleflag = not(scaleflag);
                    break;
                }

            case 4:
                {


                    for( i=2; i<= leng; i++)
                    {
                        resultant = resultant + points[i-1];
                    }


                break;

                }

            case 5:
                {
                    resultant = points[1];
                     for( i=2; i<= leng; i++)
                    {
                        resultant = resultant - points[i];
                    }


                break;
                }

            case 6:
                {
                     leng = 0 ;
                     kflag = 1;
                     resultant.set(0,0,0);
                    break;
                }

            case 7:
                {
                    leng = leng - 2;
                    if(leng <= 0)
                    {
                        leng = 0;
                    }
                    break;
                }

            case 8:
                {

                  resultant = points[2].getCrossed(points[1]);

                  //resultant = points[2].getPerpendicular(points[1]);
                  //resultant = resultant.getNormalized();

                   resultant=resultant.normalize();
                   //resultant *= 50 ;

                break;
                }

            case 9:
                {

                localbackflag = 1;

                break;
                }
            case 10:
                {

                localbackflag = 1;

                break;
                }


        }

    }



};

