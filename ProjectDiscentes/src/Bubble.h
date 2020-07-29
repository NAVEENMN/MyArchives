#pragma once

#include "ofMain.h"

class Bubble
{
    public:

    int mousex;
    int mousey;
    int length;
    int CircleR;
    int CircleX;
    int CircleY;
    int ul;
    int ll;
    int flag;
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    int draw( int CircleX, int CircleY,  int CircleR)
    {
            ofSetColor(255,0,255);
            ofNoFill();
            ofSetLineWidth(2);
            length = ofDist(CircleX, CircleY, ofGetMouseX(), ofGetMouseY());
            if (length <= CircleR)
            {
                 flag = 1;
                 CircleR = bub( CircleR);

            }
            else
            {
                flag = 0;
                CircleR = 25;

            }
            ofCircle(CircleX, CircleY, CircleR);

            return(CircleR);
    }
    //-----------------------------------------------------------------------------------------------
    int bub(int CircleR)
    {
        if(CircleR >= 40) { ul = 1; ll = 0;}
        if(CircleR <= 30)  {ul = 0; ll = 1;}

        if(ul == 1 and ll == 0)
        {
                CircleR = CircleR - 1 ;
        }

        if(ul == 0 and ll == 1)
        {
                CircleR = CircleR + 1 ;
        }


        return(CircleR);
    }
};
