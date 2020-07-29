package com.nmysore.metster;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context _context;
 
    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        
 
        }
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch(position){
        case 0:
           
            return new LayoutOne();
            
        case 1:
        	return new LayoutTwo();
        default:
        	break;
        }
        return f;
    }
    @Override
    public int getCount() {
        return 2;
    }
 
}