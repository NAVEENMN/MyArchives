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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);
	
		String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
    	String reply = null;
    	String visitorid = getIntent().getStringExtra("VisitorId");
    	Log.w("visitorid", visitorid);
        try {
			reply = new RequestTask().execute("http://54.183.113.236/metster/getprofiledata.php", appkey, visitorid, visitorid,visitorid,visitorid, visitorid, visitorid
					, visitorid, visitorid, visitorid, visitorid, visitorid, visitorid ).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//-------------------------------------------

		 final String[] separated = reply.split("#%-->");
		 String FirstName = separated[0];
		 String LastName = separated[1];
		 String Image = separated[2];
		 String Gender = separated[3];
		 String Age = separated[4];
		 String Status = separated[5];
		 String Profession = separated[6];
		 String worksat = separated[7];
		 String CurrentCity = separated[8];
		 String hometown = separated[9];
		 String hobbies = separated[10];
		 String music = separated[11];
		 String movies = separated[12];
		 String books = separated[13];
		 String AboutMe = separated[14];
		 String Passion = separated[15];
		 
		 String usrageandgender = Gender + " | " + Age ;
		 
		 Log.w("aboutme",AboutMe);
		 Log.w("passion",Passion);
		
		 setTitle(Status);
		 //------------------------------------------------------
		 TextView fname = (TextView)findViewById(R.id.FirstName); 
	     fname.setText((String)FirstName);
	     TextView lname = (TextView)findViewById(R.id.LastName); 
	     lname.setText((String)LastName);
	     TextView prof = (TextView)findViewById(R.id.Profession); 
	     prof.setText((String)Profession);
	     TextView wat = (TextView)findViewById(R.id.Worksat); 
	     wat.setText((String)worksat);
	     TextView ag = (TextView)findViewById(R.id.AgeandGender); 
	     ag.setText((String)usrageandgender);
	     TextView cc = (TextView)findViewById(R.id.CurrentCity); 
	     cc.setText((String)CurrentCity);
	     TextView hob = (TextView)findViewById(R.id.Hobbies); 
	     hob.setText((String)hobbies);
	     TextView mus = (TextView)findViewById(R.id.Music); 
	     mus.setText((String)music);
	     TextView mov = (TextView)findViewById(R.id.Movies); 
	     mov.setText((String)movies);
	     TextView book = (TextView)findViewById(R.id.Books); 
	     book.setText((String)books);
	     TextView abo = (TextView)findViewById(R.id.AboutMe); 
	     abo.setText((String)AboutMe);
	     TextView pas = (TextView)findViewById(R.id.MyPassion); 
	     pas.setText((String)Passion);
	     
	     if (Image!=null){
             byte[] decodedString = Base64.decode(Image, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageView image =(ImageView)findViewById(R.id.ProfileImage);
	                image.setImageBitmap(decodedByte);
               }
	     
	     //-------------------------------------------------------
		
	}
}
