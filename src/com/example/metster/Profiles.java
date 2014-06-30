package com.example.metster;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
			reply = new RequestTask().execute("http://www.naveenmn.com/Metster/getprofiledata.php", appkey, visitorid, visitorid,visitorid,visitorid, visitorid, visitorid
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
		 String Image = separated[3];
		 String Gender = separated[4];
		 String Age = separated[5];
		 String Status = separated[6];
		 String Profession = separated[7];
		 String worksat = separated[8];
		 String CurrentCity = separated[9];
		 String hometown = separated[10];
		 String hobbies = separated[11];
		 String music = separated[12];
		 String movies = separated[13];
		 String books = separated[14];
		 String aboutstatus = separated[15];
		
		 TextView fname = (TextView)findViewById(R.id.FirstName); 
	     fname.setText((String)FirstName);
	     TextView lname = (TextView)findViewById(R.id.LastName); 
	     lname.setText((String)LastName);
		
	}
}
