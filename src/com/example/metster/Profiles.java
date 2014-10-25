package com.example.metster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Profiles extends Activity {
	
	
	public static class visitor_info{
	
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
    	

		 visitor_info.FirstName = visitordata.getString("visitor_firstname");
		 visitor_info.LastName = visitordata.getString("visitor_lastname");
		 visitor_info.Image = visitordata.getString("visitor_image");
		 visitor_info.Gender = visitordata.getString("visitor_gender");
		 visitor_info.Status = visitordata.getString("visitor_status");
		 visitor_info.Profession = visitordata.getString("visitor_profession");
		 visitor_info.worksat = visitordata.getString("visitor_worksat");
		 visitor_info.CurrentCity = visitordata.getString("visitor_currentcity");
		 visitor_info.AboutMe = visitordata.getString("visitor_aboutme");
		 visitor_info.Facebook = visitordata.getString("visitor_facebook");
		 visitor_info.Linkedin = visitordata.getString("visitor_linkedin");
		 
		 		
		 setTitle(visitor_info.Status);
		 //------------------------------------------------------
		 TextView fname = (TextView)findViewById(R.id.FirstName); 
	     fname.setText((String)visitor_info.FirstName);
	     TextView lname = (TextView)findViewById(R.id.LastName); 
	     lname.setText((String)visitor_info.LastName);
	     TextView prof = (TextView)findViewById(R.id.Profession); 
	     prof.setText((String)visitor_info.Profession);
	     TextView wat = (TextView)findViewById(R.id.Worksat); 
	     wat.setText((String)visitor_info.worksat);
	     TextView cc = (TextView)findViewById(R.id.CurrentCity); 
	     cc.setText((String)visitor_info.CurrentCity); 
	     TextView abo = (TextView)findViewById(R.id.AboutMe); 
	     abo.setText((String)visitor_info.AboutMe);

	     
	     if (visitor_info.Image!=null){
             byte[] decodedString = Base64.decode(visitor_info.Image, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageView image =(ImageView)findViewById(R.id.ProfileImage);
	                image.setImageBitmap(decodedByte);
               }
	     
	     //-------------------------------------------------------
		
	}
	
	public void openfacebook(View view){
		String url = visitor_info.Facebook;
		try{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);}
		catch(Exception e){
			Toast.makeText(this, "User seems to have not linked facebook", Toast.LENGTH_SHORT)
            .show();
		}
	}
	
	public void openlinkedin(View view){
		String url = visitor_info.Linkedin;
		try{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
		}
		catch(Exception e){
			Toast.makeText(this, "User seems to have not linked facebook", Toast.LENGTH_SHORT)
            .show();
		}
	}
}
