package com.example.metster;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class Profiles extends Activity {
	
	
	public static class info{
	
		 static String FirstName;
		 static String LastName;
		 static String Image;
		 static String Gender;
		 static String Age;
		 static String Status;
		 static String Profession;
		 static String worksat;
		 static String CurrentCity;
		 static String hometown;
		 static String hobbies;
		 static String music;
		 static String movies;
		 static String books;
		 static String AboutMe;
		 static String Passion;
		
	};

	String server_reponse = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);
	
		String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
    	String visitorid = getIntent().getStringExtra("VisitorId");
    	Log.w("visitorid", visitorid);
        try {
        	server_reponse = new RequestTask().execute("http://54.183.113.236/metster/getprofiledata.php", appkey, visitorid, visitorid,visitorid,visitorid, visitorid, visitorid
					, visitorid, visitorid, visitorid, visitorid, visitorid, visitorid ).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//-------------------------------------------

		 final String[] separated = server_reponse.split("#%-->");
		 info.FirstName = separated[0];
		 info.LastName = separated[1];
		 info.Image = separated[2];
		 info.Gender = separated[3];
		 info.Age = separated[4];
		 info.Status = separated[5];
		 info.Profession = separated[6];
		 info.worksat = separated[7];
		 info.CurrentCity = separated[8];
		 info.hometown = separated[9];
		 info.hobbies = separated[10];
		 info.music = separated[11];
		 info.movies = separated[12];
		 info.books = separated[13];
		 info.AboutMe = separated[14];
		 info.Passion = separated[15];
		 
		 Log.w("aboutme",info.AboutMe);
		 Log.w("passion",info.Passion);
		
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
	     TextView hob = (TextView)findViewById(R.id.Hobbies); 
	     hob.setText((String)info.hobbies);
	     TextView mus = (TextView)findViewById(R.id.Music); 
	     mus.setText((String)info.music);
	     TextView mov = (TextView)findViewById(R.id.Movies); 
	     mov.setText((String)info.movies);
	     TextView book = (TextView)findViewById(R.id.Books); 
	     book.setText((String)info.books);
	     TextView abo = (TextView)findViewById(R.id.AboutMe); 
	     abo.setText((String)info.AboutMe);
	     TextView pas = (TextView)findViewById(R.id.MyPassion); 
	     pas.setText((String)info.Passion);
	     
	     if (info.Image!=null){
             byte[] decodedString = Base64.decode(info.Image, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageView image =(ImageView)findViewById(R.id.ProfileImage);
	                image.setImageBitmap(decodedByte);
               }
	     
	     //-------------------------------------------------------
		
	}
}
