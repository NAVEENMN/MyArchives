package com.example.metster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Profession_info extends Activity {

	public static class user_info{
		
		static String question;
		static String profession;
		static String works_at;
		static String Location;
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profession_info);
		
		user_info.question = "Where do you work?";
		
		
		
	}
	
	public static void setup_view(View v, int mode){
	
		//TextView fname = (TextView)findViewById(R.id.info_view);
        //fname.setText(question_one);
		
	}
}
