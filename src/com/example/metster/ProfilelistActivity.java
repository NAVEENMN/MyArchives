package com.example.metster;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProfilelistActivity extends Activity {
	
	public static class Map{
		
		static Double latival;
		static Double Longival;
		
	};
	
	public static class account{
		static String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
		static String accnumber;
		static String tokennumber;
	};
	
	public static class addrs{
		
		static String zip;
	};
	
	public static class visitorinfo{
		
		static int profileid;
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
		static String usrageandgender;
		static Double latitude;
		static Double longitude;
		
	};
	
	
	
	Button b;
    Bitmap decodedByte ;
    String visitorid = null;
    Drawable y[];
    int prfcounter = 1 ;
    String output = null;
    String[] navigate_account_numbers = null;
    String server_response = null;
    int number_of_profiles = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilelist);
		setupActionBar();// Show the Up button in the action bar.
	    setTitle("Hello!! I am new to Metster"); // user status here
		
	    Bundle listdata = getIntent().getExtras();
	    if(listdata != null){
	    	Map.latival = listdata.getDouble("latitude");
	    	Map.Longival = listdata.getDouble("longitude");
	    	account.accnumber = listdata.getString("accountnumber");
	    	addrs.zip = listdata.getString("zip");
	    	navigate_account_numbers = listdata.getString("accountnumberlist").split("#%-->");
	    	number_of_profiles = navigate_account_numbers.length;
            number_of_profiles --;
	    	visitorinfo.profileid = Integer.parseInt(navigate_account_numbers[1]);
	    	
	    }
	    		
	    
	    try {
			current_visitor(visitorinfo.profileid );
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		
		//---------------------------------- Button next
				Button find = (Button) findViewById(R.id.visitorbuttonnext);
		        find.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							if( prfcounter >= number_of_profiles ) prfcounter = 0 ;
							prfcounter++;
							visitorinfo.profileid = Integer.parseInt(navigate_account_numbers[prfcounter]);
							try {
								current_visitor(visitorinfo.profileid );
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					       
						}//on click
					
					
		        		});
		//----------------------------------
		      //---------------------------------- Button view profile
				Button findviewprofile = (Button) findViewById(R.id.visitorbuttonviewprofile);
				findviewprofile.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) { // yet to be implemented
							
							Intent intent = new Intent(ProfilelistActivity.this, Profiles.class);
							intent.putExtra("VisitorId",visitorinfo.profileid);
							intent.putExtra("VisitorImage",visitorinfo.Image);
			        		startActivity(intent);
							
					       }//on click
					
					
		        		});
		//----------------------------------
		        
		      //---------------------------------- Button previous
				Button findprev = (Button) findViewById(R.id.visitorbuttonprev);
				findprev.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							if( prfcounter <= 1 ) prfcounter = number_of_profiles + 1  ;
							prfcounter--;
							visitorinfo.profileid = Integer.parseInt(navigate_account_numbers[prfcounter]);
							try {
								current_visitor(visitorinfo.profileid );
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					       }//on click
					
					
		        		});
		//----------------------------------
				
				        
		
	}
	
	public void current_visitor(int account_number) throws InterruptedException, ExecutionException{
		
		 try {
		    	server_response = new RequestTask().execute("http://54.183.113.236/metster/getprofiledata.php", account.appkey, Integer.toString(account_number), "1","1","1", "1", "1"
						, "1", "1", "1", "1", "1", "1" ).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			 final String[] separated = server_response.split("#%-->");
			 visitorinfo.FirstName = separated[0];
			 visitorinfo.LastName = separated[1];
			 visitorinfo.Image = separated[2];
			 visitorinfo.Gender = separated[3];
			 visitorinfo.Age = separated[4];
			 visitorinfo.Status = separated[5];
			 visitorinfo.Profession = separated[6];
			 visitorinfo.worksat = separated[7];
			 visitorinfo.CurrentCity = separated[8];
			 visitorinfo.hometown = separated[9];
			 visitorinfo.hobbies = separated[10];
			 visitorinfo.music = separated[11];
			 visitorinfo.movies = separated[12];
			 visitorinfo.books = separated[13];
			 visitorinfo.AboutMe = separated[14];
			 visitorinfo.Passion = separated[15];
			 visitorinfo.latitude = Double.parseDouble(separated[16]);
			 visitorinfo.longitude = Double.parseDouble(separated[17]);
			 visitorinfo.usrageandgender = visitorinfo.Gender + " | " + visitorinfo.Age ;
			 
			 displayprofile(visitorinfo.Image, visitorinfo.FirstName, visitorinfo.LastName, visitorinfo.latitude, visitorinfo.longitude, visitorinfo.Gender,
					        visitorinfo.Age, visitorinfo.Profession, visitorinfo.worksat, visitorinfo.CurrentCity);
		
	}
	
	public void displayprofile(String image, String visfname, String vislname ,double lativale , double Longivale, String gender, String age, String profession,
			                   String worksat, String currentcity)
	{
		//-----------------------> display contents
		TextView fname = (TextView)findViewById(R.id.FirstName); 
        fname.setText(visfname);
        TextView lname = (TextView)findViewById(R.id.LastName); 
        lname.setText(vislname);
        TextView prof = (TextView)findViewById(R.id.Profession); 
        prof.setText(profession);
        TextView workat = (TextView)findViewById(R.id.Worksat); 
        workat.setText(worksat);
        TextView currcity = (TextView)findViewById(R.id.CurrentCity); 
        currcity.setText(currentcity);
        TextView ag = (TextView)findViewById(R.id.AgeandGender); 
        String agegen = gender + " | " + age ;
        ag.setText(agegen);
		//-----------------------> put vis on map
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();

        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
        
        GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lativale, Longivale)) // visitor
                .title("Hi")).showInfoWindow();
        
        
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
		    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imgg = (ImageView)findViewById(R.id.ImagevisitorView01);
        imgg.setImageBitmap(decodedByte);
		
	}

	//---------------------
	@Override
	public void onBackPressed() {

		ProfilelistActivity.this.finish();
		
	}
	//---------------------
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profilelist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
