package com.example.metster;

import java.util.concurrent.ExecutionException;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.metster.Login.visitorinfo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
	
	public static class viz{
		static String acc;
	}
	
	

    int prfcounter = 1 ;
    String[] navigate_account_numbers = null;
    String server_response = null;
    int number_of_profiles = 0;
    Bundle visitoractdata = new Bundle();
    ProgressDialog progress;
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
	    	visitorinfo.profileid = navigate_account_numbers[1];
	    	
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
							visitorinfo.profileid = navigate_account_numbers[prfcounter];
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
		      
		        
		      //---------------------------------- Button previous
				Button findprev = (Button) findViewById(R.id.visitorbuttonprev);
				findprev.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							if( prfcounter <= 1 ) prfcounter = number_of_profiles + 1  ;
							prfcounter--;
							visitorinfo.profileid = navigate_account_numbers[prfcounter];
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
	
	public void current_visitor(String account_number) throws InterruptedException, ExecutionException{
		viz.acc = account_number;

		 try {
		    	server_response = new RequestTask().execute("http://54.183.113.236/metster/getprofiledata.php", account.appkey, account_number, "1","1","1", "1", "1"
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
			 visitorinfo.Status = separated[4];
			 visitorinfo.Profession = separated[5];
			 visitorinfo.worksat = separated[6];
			 visitorinfo.CurrentCity = separated[7];
			 visitorinfo.AboutMe = separated[8];
			 visitorinfo.latitude = Double.parseDouble(separated[9]);
			 visitorinfo.longitude = Double.parseDouble(separated[10]);
			 visitorinfo.facebookurl = separated[11];
			 visitorinfo.linkedinurl = separated[12];
			 
			 //------
			 
			 visitoractdata.putString("visitor_image",visitorinfo.Image);
			 visitoractdata.putString("visitor_firstname",visitorinfo.FirstName);
			 visitoractdata.putString("visitor_lastname",visitorinfo.LastName);
			 visitoractdata.putString("visitor_gender",visitorinfo.Gender);
			 visitoractdata.putString("visitor_status",visitorinfo.Status);
			 visitoractdata.putString("visitor_profession",visitorinfo.Profession);
			 visitoractdata.putString("visitor_worksat",visitorinfo.worksat);
			 visitoractdata.putString("visitor_currentcity",visitorinfo.CurrentCity);
			 visitoractdata.putString("visitor_aboutme",visitorinfo.AboutMe);
			 visitoractdata.putString("visitor_facebook",visitorinfo.facebookurl);
			 visitoractdata.putString("visitor_linkedin",visitorinfo.linkedinurl);
			 
			 //-------
			 Log.w("nowuser",account_number);
			 try{
			 StringBuilder strBuilder = new StringBuilder("https://met-ster.firebaseio.com/");
			 strBuilder.append(addrs.zip);
		     strBuilder.append("/");
			 strBuilder.append(account_number);
			 String fbref = strBuilder.toString();
			 Firebase dat = new Firebase(fbref);
			 dat.child("Latitude").addValueEventListener(new ValueEventListener() {
			        @Override
			        public void onDataChange(DataSnapshot snapshot) {
			            System.out.println(snapshot.getValue()); 
			            Double d = Double.parseDouble(snapshot.getValue().toString());
			            visitorinfo.latitude = d;
			            try{
			            updatemap(null);
			            }catch(Exception e){
			            	Log.w("map","locationerror");
			            }
			        }
			        @Override public void onCancelled(FirebaseError error) { }
			    });
			 dat.child("Longitude").addValueEventListener(new ValueEventListener() {
			        @Override
			        public void onDataChange(DataSnapshot snapshot) {
			            System.out.println(snapshot.getValue());
			            Double d = Double.parseDouble(snapshot.getValue().toString());
			            visitorinfo.longitude = d;
			            try{
				            updatemap(null);
				            }catch(Exception e){
				            	Log.w("map","locationerror");
				            }
			        }
			        @Override public void onCancelled(FirebaseError error) { }
			    });
			 dat.child("Status").addValueEventListener(new ValueEventListener() {
			        @Override
			        public void onDataChange(DataSnapshot snapshot) {
			            System.out.println(snapshot.getValue());
			            
			            visitorinfo.Status = (String) snapshot.getValue();
			            try{
			            	setTitle(visitorinfo.Status);
				            }catch(Exception e){
				            	Log.w("map","locationerror");
				            }
			        }
			        @Override public void onCancelled(FirebaseError error) { }
			    });
			 }catch(Exception e){
				 Log.w("error","cannot access firebase");
			 }
			 
			 //-------
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
        setTitle(visitorinfo.Status); // user status here
        updatemap(null);
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
		    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		    ImageButton imgg = (ImageButton)findViewById(R.id.ImagevisitorView01);
        imgg.setImageBitmap(decodedByte);
		
	}

	
	public void load_profile(View view){
		
		Intent intent = new Intent(ProfilelistActivity.this, Profiles.class);
		intent.putExtras(visitoractdata);
		startActivity(intent);
		
	}
	
	public void updatemap(View view){
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
                .position(new LatLng(visitorinfo.latitude, visitorinfo.longitude)) // visitor
                .title(visitorinfo.FirstName)).showInfoWindow();
        
        
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
