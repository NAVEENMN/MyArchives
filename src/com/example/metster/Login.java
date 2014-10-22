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
		
	public static class addrs{
	
		static String zip;
		static String cityName;
		static String country;
		static String addressline;
		
	};
	
	public static class User{
		
		static String profileimage;
		static String usrfname;
		static String usrlname;
		static String usrgender;
		static String usrabout;
		static String usrprofession;
		static String usrworksat;
		static String usrcurrentcity;
		static String usrstatus;
		static String linkedin;
		static String facebook;
		
	};
	
	
public static class visitorinfo{
		
		static String profileid;
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
		static Double latitude;
		static Double longitude;
		static String facebookurl;
		static String linkedinurl;
		
	};
	
	public static class account{
		static String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
		static String accnumber;
		static String tokennumber;
	};
	
	public static class Map{
		
		static Double latival;
		static Double Longival;
		
	};
	
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
		User.usrstatus = "Hello There!!";
		setTitle("Set your status");
		//--------------------------------> Read Bundle
		
				Bundle b = getIntent().getExtras();
				if( b != null ){
					account.accnumber = b.getString("accountnumber");
					profilelistactdata.putString("accountnumber", account.accnumber);
					account.tokennumber = b.getString("tokennumber");
					User.profileimage = b.getString("userimage");
					profilelistactdata.putString("userimage",User.profileimage);
				}
							
	//--------------------------------> Setup location
	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {	
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    }
    public void onProviderEnabled(String provider) {
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
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
        Map.latival = location.getLatitude();
        Map.Longival = location.getLongitude();
    }
    else{
    	provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        Map.latival = location.getLatitude();
        Map.Longival = location.getLongitude();
    }
    
  //------------------------------------------------------------------> Fetch the location details
  		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
  		List<Address> addresses;
  		
  		try {
              addresses = gcd.getFromLocation(Map.latival, Map.Longival, 1);
              if (addresses.size() > 0)
              addrs.cityName = addresses.get(0).getLocality();
              addrs.country = addresses.get(0).getCountryCode();
              addrs.zip = addresses.get(0).getPostalCode();
              profilelistactdata.putString("zip", addrs.zip);
              addrs.addressline = addresses.get(0).getThoroughfare();
             
          } catch (IOException e) {
              e.printStackTrace();
          }

  	//----------------------> Fire base reference creation
		StringBuilder strBuilder = new StringBuilder("https://met-ster.firebaseio.com/");
		strBuilder.append(addrs.zip);
		strBuilder.append("/");
	    strBuilder.append(account.accnumber);
	    fbdata.fbref = strBuilder.toString();
	    fbdata.firebaseobj = new Firebase(fbdata.fbref);
	    fbdata.firebaseobj.child("Status").setValue("Hello There!!");
    
    getData = new Runnable()
    {
            @Override
            public void run()
            {
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
  	    	 new RequestTask().execute("http://54.183.113.236/metster/deletelocation.php",account.appkey,account.accnumber,"1","1","1","1","1"
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
    	Userslist.server_response = new RequestTask().execute("http://54.183.113.236/metster/fetchprofile.php",account.accnumber,account.tokennumber,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival),(String)addrs.country,"1"
    			, "1", "1", "1", "1", "1", "1").get();
    	}catch(Exception e){
    		Log.w("fetch","failed");
    	}            
	            updatelocation(null);
	            Log.w("response",Userslist.server_response);
	    
	           
					String[] separated = Userslist.server_response.split("-");
		            User.usrfname = separated[0];
		            User.usrlname = separated[1];
		            User.usrgender = separated[2];
		            User.usrabout = separated[3];
		            User.usrprofession = separated[4];
		            User.usrworksat = separated[5];
		            User.usrcurrentcity = separated[6];
		            User.linkedin = null; // setup in server
		            User.facebook = null;
		           
		            profilelistactdata.putString("userprofession", User.usrprofession);
		            profilelistactdata.putString("userworksat", User.usrworksat);
		            profilelistactdata.putString("usercurrentcity", User.usrcurrentcity);
					
		            SetupUIdata();
	//----------------------------------
			if(Userslist.numberofusers.isEmpty()) 
			{
				Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
			}
			else {
				final String[] accountnumbers = Userslist.numberofusers.split("#%-->");
				Userslist.user_count = accountnumbers.length;
				Userslist.user_count --;
			}
					        
//-----------------------------------------------------------------------> Button Actions	            
	            //------------------------------------- meet someone
			final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		        find = (Button) findViewById(R.id.buttonmeet);
		        rend_button_obj = (Button) findViewById(R.id.Rend);
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
        fname.setText((String)User.usrfname);
        TextView lname = (TextView)findViewById(R.id.LastName); 
        lname.setText((String)User.usrlname);
        lname.startAnimation(animTimeChange);
        TextView prof = (TextView)findViewById(R.id.Profession); 
        prof.setText((String)User.usrprofession);
        TextView wat = (TextView)findViewById(R.id.Worksat); 
        wat.setText((String)User.usrworksat);
        TextView cc = (TextView)findViewById(R.id.CurrentCity); 
        cc.setText((String)User.usrcurrentcity);
        //----------- Section 2
        //TextView loca = (TextView)findViewById(R.id.YourLocation); 
        //loca.setText((String)addrs.addressline);
        //TextView locacity = (TextView)findViewById(R.id.YourLocationcity); 
        //locacity.setText((String)addrs.cityName);
        //----------- Section Profile Image
        if (User.profileimage!=null){
            byte[] decodedString = Base64.decode(User.profileimage, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
	                imageButton.setImageBitmap(decodedByte);
              }
        //----------- Section Maps
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();

        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours

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
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    	profilelistactdata.putDouble("latitude",Map.latival);
    	profilelistactdata.putDouble("longitude", Map.Longival);
    	fbdata.firebaseobj.child("Latitude").setValue(Double.toString(Map.latival));
    	fbdata.firebaseobj.child("Longitude").setValue(Double.toString(Map.Longival));
        try {
        	new RequestTask().execute("http://54.183.113.236/metster/updatedash.php",account.accnumber,account.appkey,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival), User.usrstatus,"1",
 					"1", "1", "1", "1", "1", "1").get();
        	 Userslist.numberofusers = new RequestTask().execute("http://54.183.113.236/metster/numberofusers.php",account.accnumber,account.appkey,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival), "1","1",
					"1", "1", "1", "1", "1", "1").get();
        	 profilelistactdata.putString("accountnumberlist", Userslist.numberofusers);
		     if(Userslist.numberofusers.isEmpty()) 
				{
					//Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
				}
				else {
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
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Map.latival, Map.Longival)) // visitor
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
			  User.usrstatus = value.toString();
			  setTitle(User.usrstatus);
			  fbdata.firebaseobj.child("Status").setValue(User.usrstatus);
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
