package com.example.metster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Profiles extends Activity {
	
	
	public static class info{
	
		 static String FirstName;
		 static String LastName;
		 static String Image;
		 static String Gender;
		 static String Status;
		 static String Profession;
		 static String worksat;
		 static String CurrentCity;
		 static String AboutMe;
		 static String Facebook;
		 static String Linkedin;
		
	};

	String server_reponse = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);
	
		Bundle visitordata = getIntent().getExtras();
    	

		 info.FirstName = visitordata.getString("visitor_firstname");
		 info.LastName = visitordata.getString("visitor_lastname");
		 info.Image = visitordata.getString("visitor_image");
		 info.Gender = visitordata.getString("visitor_gender");
		 info.Status = visitordata.getString("visitor_status");
		 info.Profession = visitordata.getString("visitor_profession");
		 info.worksat = visitordata.getString("visitor_worksat");
		 info.CurrentCity = visitordata.getString("visitor_currentcity");
		 info.AboutMe = visitordata.getString("visitor_aboutme");
		 info.Facebook = visitordata.getString("visitor_facebook");
		 info.Linkedin = visitordata.getString("visitor_linkedin");
		 
		 Log.w("fa",info.Facebook);
		 		
		 setTitle(info.Status);
		 //------------------------------------------------------
		 TextView fname = (TextView)findViewById(R.id.FirstName); 
	     fname.setText((String)info.FirstName);
	     TextView lname = (TextView)findViewById(R.id.LastName); 
	     lname.setText((String)info.LastName);
	     TextView prof = (TextView)findViewById(R.id.Profession); 
	     prof.setText((String)info.Profession);
	     TextView wat = (TextView)findViewById(R.id.Worksat); 
	     wat.setText((String)info.worksat);
	     TextView cc = (TextView)findViewById(R.id.CurrentCity); 
	     cc.setText((String)info.CurrentCity); 
	     TextView abo = (TextView)findViewById(R.id.AboutMe); 
	     abo.setText((String)info.AboutMe);

	     
	     if (info.Image!=null){
             byte[] decodedString = Base64.decode(info.Image, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageView image =(ImageView)findViewById(R.id.ProfileImage);
	                image.setImageBitmap(decodedByte);
               }
	     
	     //-------------------------------------------------------
		
	}
	
	public void openfacebook(View view){
		String url = info.Facebook;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
}
