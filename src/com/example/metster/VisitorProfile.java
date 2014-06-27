package com.example.metster;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VisitorProfile extends Activity {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitor_profile);
		
		String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
    	String reply = null;
    	String visitorid = getIntent().getStringExtra("VisitorId");
    	Log.w("visitorid", visitorid);
        try {
			reply = new RequestTask().execute("http://www.naveenmn.com/Metster/visitorprofile.php", appkey, visitorid, visitorid,visitorid,visitorid, visitorid, visitorid
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
		 String userprofession = separated[0];
		 String userworksat = separated[1];
		 Log.w("userprofession",userprofession);
		 Log.w("userworksat",userworksat);
		// String userhometown = separated[2];
		 //String userhobbies = separated[3];
		 //String usermusic = separated[4];
		 //String usermovies = separated[5];
		 //String userbooks = separated[6];
		 //String userstatus = separated[7];
		 //String age = separated[8];
		 TextView uprofession = (TextView)findViewById(R.id.userprofession); 
	        uprofession.setText((String)userprofession);
		// TextView fname = (TextView)findViewById(R.id.userprofession); 
         //fname.setText((String)userprofession);
         //fname = (TextView)findViewById(R.id.userworksat); 
         //fname.setText((String)userworksat);
         //fname = (TextView)findViewById(R.id.userhometown); 
         //fname.setText((String)userhometown);
		 
		 
		//-------------------------------------------

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		
		
		//-------------------------------------------	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visitor_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_visitor_profile,
					container, false);
			return rootView;
		}
	}
	
	//---------------------
		@Override
		public void onBackPressed() {
		 
			getIntent().removeExtra("VisitorImage");
			getIntent().removeExtra("VisitorId");
			VisitorProfile.this.finish();
			
		}
		//---------------------
	
	
	//--------------------------------
	//--------------------------------
	
	

}
