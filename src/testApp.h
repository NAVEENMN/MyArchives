#pragma once

#include "ofMain.h"
#include "Bubble.h"
#include "ofxVectorGraphics.h"
#include "grap.h"


class testApp : public ofBaseApp{

	public:
		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);
		void action(int f);
		Bubble bubble[20]; // 6 used
		grap vect; // vect is the object for graph
        ofTrueTypeFont myfont, myfont1, myfont2, myfont3;
        ofEasyCam cam;
		ofxVectorGraphics output;
		ofPath path;
		ofVec3f X,Y,Z;
		ofMesh mesh;

		//--------

        ofImage cube;
		ofImage cone;
		ofImage sphere;
		ofImage cylinder;
		ofImage triangle;
		ofImage rectangle;
		ofImage line;

		//-----

		    float           nextLetterTime;
            int             lineCount;
            int             letterCount;
            vector <string> seussLines;
            int t;
            int h;
            string num;
            int dat[20];
            int numb[20];
            int k ;



		//-----


		//----------------------




		//----------------------


};
