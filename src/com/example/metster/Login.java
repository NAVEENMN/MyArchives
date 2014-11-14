package com.example.metster;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Login extends Activity {
	
	public static class fbdata{
		
		static String fbref;
		static Firebase firebaseobj;
		
	};
	
	public static class loading_token{
		static Boolean ok_to_load;
	}
		
	
	public static class Userslist{
		static String server_response;
		static String numberofusers;
		static int user_count;
	};
	//----------------------------------------->
		
	Geocoder gcd ;
	GoogleMap mMap;
	LocationManager locationManager;
	LocationListener locationListener;
	Location location;
	Location position;
	String provider;	
	List<Address> addresses;
	private Button find;
	private Button rend_button_obj;
	private final Handler _handler = new Handler();
	Runnable getData;
    Criteria criteria = new Criteria();
    Bundle profilelistactdata = new Bundle();
    ProgressDialog pd;
   
    //---------------------------------------->
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();// Show the Up button in the action bar.
		commondata.user_information.status = "Hello There!!";
		setTitle("Set your status");
		Firebase.setAndroidContext(this);					
		//--------------------------------> Setup location listener
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {	
			commondata.user_information.latitude = location.getLatitude();
			commondata.user_information.longitude = location.getLongitude();
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			commondata.user_information.latitude = location.getLatitude();
			commondata.user_information.longitude = location.getLongitude();
		}
		public void onProviderEnabled(String provider) {
			commondata.user_information.latitude = location.getLatitude();
			commondata.user_information.longitude = location.getLongitude();
		}
    public void onProviderDisabled(String provider) {}
    };
  	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);// Register the listener with the Location Manager to receive location updates
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if( position == null ){
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        position = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        commondata.user_information.latitude = location.getLatitude();
        commondata.user_information.longitude = location.getLongitude();
    }
    else{
    	provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        commondata.user_information.latitude = location.getLatitude();
        commondata.user_information.longitude = location.getLongitude();
    }
    
  //------------------------------------------------------------------> Fetch the location details
  		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
  		List<Address> addresses;
  		
  		try {
              addresses = gcd.getFromLocation(commondata.user_information.latitude, commondata.user_information.longitude, 1);
              if (addresses.size() > 0)
              commondata.user_information.cityname = addresses.get(0).getLocality();
              commondata.user_information.country = addresses.get(0).getCountryCode();
              commondata.user_information.zip = addresses.get(0).getPostalCode();
              profilelistactdata.putString("zip", commondata.user_information.zip);
              commondata.user_information.addressline = addresses.get(0).getThoroughfare();
             
          } catch (IOException e) {
              e.printStackTrace();
          }

  	//----------------------> Fire base reference creation
		StringBuilder strBuilder = new StringBuilder("https://met-ster.firebaseio.com/");
		strBuilder.append(commondata.user_information.zip);
		strBuilder.append("/");
	    strBuilder.append(commondata.user_information.account_number);
	    fbdata.fbref = strBuilder.toString();
	    fbdata.firebaseobj = new Firebase(fbdata.fbref);
	    fbdata.firebaseobj.child("Status").setValue("Hello There!!");
	    
	    //---- find base place to add to add location to comman data are
	    //---
    
    getData = new Runnable(){
            	@Override
            	public void run(){
                updatelocation(null);
            	}
    			};
    
    final Handler handler_remove_location_updates = new Handler();
    handler_remove_location_updates.postDelayed(new Runnable() {
      @Override
      public void run() {
    	  locationManager.removeUpdates(locationListener);
      }
    }, 1000 * 60 * 3);//3mins
    
    final Handler handler_delete_location = new Handler();
    handler_delete_location.postDelayed(new Runnable() {
      @Override
      public void run() { // Might have to kill this thread on refresh
    	  stopRepeatingTask();
    	  Toast.makeText(getApplicationContext(), "Location will be deleted.", Toast.LENGTH_SHORT).show();
    	  fbdata.firebaseobj.child("Latitude").removeValue();
    	  fbdata.firebaseobj.child("Longitude").removeValue();
    	  fbdata.firebaseobj.child("Status").removeValue();
    	  try {
  	    	 new RequestTask().execute("http://54.183.113.236/metster/deletelocation.php",commondata.keys.appkey,commondata.user_information.account_number,"1","1","1","1","1"
  			, "1", "1", "1", "1", "1", "1").get();
  			} catch (InterruptedException e) {
  							// TODO Auto-generated catch block
  				e.printStackTrace();
  			} catch (ExecutionException e) {
  							// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
      }
    }, 1000 * 60* 3);//3mins
     
	//-------------------------------------> Read data from server
    try{
    	Userslist.server_response = new RequestTask().execute("http://54.183.113.236/metster/fetchprofile.php",commondata.user_information.account_number,commondata.user_information.token_number,commondata.user_information.zip,Double.toString(commondata.user_information.latitude),Double.toString(commondata.user_information.longitude),commondata.user_information.country,"1"
    			, "1", "1", "1", "1", "1", "1").get();
    	}catch(Exception e){
    		Log.w("fetch","failed");
    	}            
	            updatelocation(null);
	            Log.w("response",Userslist.server_response);
	    
	           
					String[] separated = Userslist.server_response.split("-");
		            commondata.user_information.firstname = separated[0];
		            commondata.user_information.lastname = separated[1];
		            commondata.user_information.gender = separated[2];
		            commondata.user_information.about = separated[3];
		            commondata.user_information.profession = separated[4];
		            commondata.user_information.worksat = separated[5];
		            commondata.user_information.currentcity = separated[6];
		            commondata.user_information.linkedin = null; // setup in server
		            commondata.user_information.facebook = null;
		           
		            profilelistactdata.putString("userprofession", commondata.user_information.profession);
		            profilelistactdata.putString("userworksat", commondata.user_information.worksat);
		            profilelistactdata.putString("usercurrentcity", commondata.user_information.currentcity);
					
		            SetupUIdata();
	//----------------------------------
			if(Userslist.numberofusers.isEmpty()) 
			{
				Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
			}else {
				final String[] accountnumbers = Userslist.numberofusers.split("#%-->");
				Userslist.user_count = accountnumbers.length;
				Userslist.user_count --;
			}
					        
//-----------------------------------------------------------------------> Button Actions	            
	            //------------------------------------- meet someone
			
			final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		        //find = (Button) findViewById(R.id.buttonmeet);
		        rend_button_obj = (Button) findViewById(R.id.Rend);
		        /*
		        //----------------------------------------------------------------      
		        find.setOnClickListener(new View.OnClickListener() {
		        	public void onClick(View v) {
							v.startAnimation(animScale);			
							new Thread(new Runnable() { 
					            public void run(){
					            	SystemClock.sleep(2000);
					            	
					            	if(Userslist.user_count > 0){
										stopRepeatingTask();
										locationManager.removeUpdates(locationListener);
										Intent intentprofilelist = new Intent( Login.this, ProfilelistActivity.class);
										intentprofilelist.putExtras(profilelistactdata);
						        		startActivity(intentprofilelist);
										}
										else{
											//Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT).show();
										}
					            }
					    }).start();						
					}//on click
		        });
		        */	        
		      //---------------------------------------------------------------	
		      //------------------------------------- Rend      
		        rend_button_obj.setOnClickListener(new View.OnClickListener() {
			        	public void onClick(View v) {
								v.startAnimation(animScale);			
								new Thread(new Runnable() { 
						            public void run(){
						            	SystemClock.sleep(2000);
						            	
											stopRepeatingTask();
											locationManager.removeUpdates(locationListener);
											Intent intentrend = new Intent( Login.this, Rend.class);
											intentrend.putExtras(profilelistactdata);
							        		startActivity(intentrend);
											
						            }
						    }).start();						
						}//on click
			        });	        
			      //---------------------------------------------------------------	
				
	}//on create
	//------------------------------------- update profile
	
	public void SetupUIdata(){
		
//-----------------------------------------------------------> Setup the GUI with data acquired
		 Animation animTimeChange = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left); 
		//----------- Section 1
		TextView fname = (TextView)findViewById(R.id.FirstName);
		fname.startAnimation(animTimeChange);
        fname.setText((String)commondata.user_information.firstname);
        TextView lname = (TextView)findViewById(R.id.LastName); 
        lname.setText((String)commondata.user_information.lastname);
        lname.startAnimation(animTimeChange);
        TextView prof = (TextView)findViewById(R.id.Profession); 
        prof.setText((String)commondata.user_information.profession);
        TextView wat = (TextView)findViewById(R.id.Worksat); 
        wat.setText((String)commondata.user_information.worksat);
        TextView cc = (TextView)findViewById(R.id.CurrentCity); 
        cc.setText((String)commondata.user_information.currentcity);
        //----------- Section 2
        //TextView loca = (TextView)findViewById(R.id.YourLocation); 
        //loca.setText((String)addrs.addressline);
        //TextView locacity = (TextView)findViewById(R.id.YourLocationcity); 
        //locacity.setText((String)addrs.cityName);
        //----------- Section Profile Image
        if (commondata.user_information.profileimage!=null){
            byte[] decodedString = Base64.decode(commondata.user_information.profileimage, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
	                imageButton.setImageBitmap(decodedByte);
              }
        //----------- Section Maps
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();

        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
        
//-------------------------------------------------------------------------------------------- 
		
	}
	
	public void updatelocation(View view)
	{
		
		Log.w("called","gaina");
		
		_handler.postDelayed(getData, 3000);

		position = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	provider = locationManager.getBestProvider(criteria, false);
    	location = locationManager.getLastKnownLocation(provider);
    	commondata.user_information.latitude = location.getLatitude();
    	commondata.user_information.longitude = location.getLongitude();
    	profilelistactdata.putDouble("latitude",commondata.user_information.latitude);
    	profilelistactdata.putDouble("longitude", commondata.user_information.longitude);
    	fbdata.firebaseobj.child("Latitude").setValue(Double.toString(commondata.user_information.latitude));
    	fbdata.firebaseobj.child("Longitude").setValue(Double.toString(commondata.user_information.longitude));
        try {
        	new RequestTask().execute("http://54.183.113.236/metster/updatedash.php",commondata.user_information.account_number,commondata.keys.appkey,commondata.user_information.zip,Double.toString(commondata.user_information.latitude),Double.toString(commondata.user_information.longitude), commondata.user_information.status,"1",
 					"1", "1", "1", "1", "1", "1").get();
        	 Userslist.numberofusers = new RequestTask().execute("http://54.183.113.236/metster/numberofusers.php",commondata.user_information.account_number,commondata.keys.appkey,commondata.user_information.zip,Double.toString(commondata.user_information.latitude),Double.toString(commondata.user_information.longitude), "1","1",
					"1", "1", "1", "1", "1", "1").get();
        	 profilelistactdata.putString("accountnumberlist", Userslist.numberofusers);
		     if(Userslist.numberofusers.isEmpty()) {
					//Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
			  }else {
					final String[] accountnumb = Userslist.numberofusers.split("#%-->");
					Userslist.user_count = accountnumb.length;
					Userslist.user_count --;
			}
        	//TextView num = (TextView)findViewById(R.id.NumberofUsers); 
            //num.setText(Integer.toString(Userslist.user_count));
            
            
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(commondata.user_information.latitude, commondata.user_information.longitude)) // visitor
                .title(Integer.toString(Userslist.user_count))).showInfoWindow();
	
	}
	
	 

     public void stopRepeatingTask()
     {
    	 _handler.removeCallbacks(getData);
     }
	
	public void updateprofile(View view){
		stopRepeatingTask();
    	Intent intent2 = new Intent( Login.this, Profession_info.class);
    	intent2.putExtras(profilelistactdata);
		startActivity(intent2);
    }
	
	
	public void on_image_click(View view){
		//
		Intent intentprofilelist = new Intent( Login.this, Upload_Image.class);
		startActivity(intentprofilelist);
	}
	
	@Override
	public void onBackPressed() {
	 
		new AlertDialog.Builder(this)
        .setMessage("You will go invisible, Do you wish to exit?")
        .setTitle("Metster")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	stopRepeatingTask();
        		locationManager.removeUpdates(locationListener);
        		Login.this.finish();
            }
        })
        .setNegativeButton("No", null)
        .show();	
	}

	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		//getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.login, menu);
		//return true;
		MenuInflater mif = getMenuInflater();
		mif.inflate(R.menu.main_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Set your Status");
			alert.setMessage("What I am upto!!");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  Editable value = input.getText();
			  commondata.user_information.status = value.toString();
			  setTitle(commondata.user_information.status);
			  fbdata.firebaseobj.child("Status").setValue(commondata.user_information.status);
			  // Do something with value!
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
			return true;
			
		case R.id.person_icon:
			updateprofile(null);
			return true;
		case R.id.refresh_icon:
			locationManager.removeUpdates(locationListener);
            Intent serviceIntent = new Intent(Login.this, Login.class);
            startActivity(serviceIntent);
            finish();
			return true;	
		case R.id.settings_icon:
			locationManager.removeUpdates(locationListener);
            Intent settingsIntent = new Intent(Login.this, Settings.class);
            startActivity(settingsIntent);
            finish();
			return true;	
			
		}
		return super.onOptionsItemSelected(item);
	}

}