package com.nmysore.metster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nmetster.metster.R;
 
public class LayoutTwo extends Fragment {
 
    public static Fragment newInstance(Context context) {
        LayoutTwo f = new LayoutTwo();
        System.out.println("layout two runnning...");
       //new Chatlayer().;
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        
        
        System.out.println("layout two runnning...");
        Intent intent = new Intent(getActivity(), Chatlayer.class);
        ((ViewPagerStyle1Activity) getActivity()).startActivity(intent);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_chatlayer,container, false);
        
        return root;
    }
    
}