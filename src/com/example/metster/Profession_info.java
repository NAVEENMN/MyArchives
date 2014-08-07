package com.example.metster;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Profession_info extends Activity {

	public static class user_info{
		
		static String question;
		static String answer;
		static String profession;
		static String works_at;
		static String Location;
		
	};
	
	String questions[] ={
			"What is your profession?",
			"Where do you work?",
			"What is your current city?",
			"Update your image.", 
	};
	int counter = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profession_info);
		Bundle listdata = getIntent().getExtras();
		user_info.profession = listdata.getString("userprofession");
		user_info.works_at = listdata.getString("userworksat");
		user_info.Location = listdata.getString("usercurrentcity");
		user_info.question = questions[0];
		user_info.answer = user_info.profession;
		setup_view(null);
		
		
	}
	
	public void previous_question(View v){
		
		if(counter <= 0) counter = 0;
		else counter --;
		user_info.question = questions[counter];
		
		switch(counter){
		case 0:
			String ans = user_info.profession;
			user_info.answer = ans;
			break;
		case 1:
			String loc = user_info.works_at;
			user_info.answer = loc;
			break;
		case 2:
			String lo = user_info.Location;
			user_info.answer = lo;
			break;
		case 3:
			String im = "image";
			user_info.answer = im;
			break;
		
		}
		
		setup_view(null);
		
		
	}
	public void next_question(View v){
		counter ++;
		if(counter > ( questions.length - 1)) counter = 0;
		user_info.question = questions[counter];
		switch(counter){
		case 0:
			String ans = user_info.profession;
			user_info.answer = ans;
			break;
		case 1:
			String loc = user_info.works_at;
			user_info.answer = loc;
			break;
		case 2:
			String lo = user_info.Location;
			user_info.answer = lo;
			break;
		case 3:
			String im = "image";
			user_info.answer = im;
			break;
		
		}
		setup_view(null);
		
	}
	public void upload_information(View v){
	
	}
	
	public void setup_view(View v){
	
		TextView quest = (TextView)findViewById(R.id.question);
        quest.setText(user_info.question);
        EditText ans = (EditText) findViewById(R.id.answerbyuser) ;
        if(user_info.answer != "image"){
        ans.setHint(user_info.answer);}
        else{
        	ans.setFocusable(false);
        	ViewGroup linearLayout = (ViewGroup) findViewById(R.id.answer);
			Button bt = new Button(this);
			bt.setText("A Button");
			bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
			                                    LayoutParams.WRAP_CONTENT));
			linearLayout.addView(bt);
        }
		
	}
}
