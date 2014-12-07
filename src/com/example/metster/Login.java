package com.example.metster;
import java.io.IOException;
import java.util.Iterator;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
    Location postion_get;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();// Show the Up button in the action bar.
		commondata.user_information.status = "Hello There!!";
		setTitle("Set your status");
		Firebase.setAndroidContext(this);
		//------ side bar
		
		if(commondata.facebook_details.contact.contains("nothing")){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Contact Information needed.");
			alert.setMessage("Hi, your friends need you contact information to add to events.");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  Editable value = input.getText();
			  commondata.facebook_details.contact = value.toString();
			  commondata.facebook_details.contact = commondata.facebook_details.contact.replace('(',' ');
			  commondata.facebook_details.contact = commondata.facebook_details.contact.replace(')',' ');
			  commondata.facebook_details.contact = commondata.facebook_details.contact.replace('-',' ');
			  commondata.facebook_details.contact = commondata.facebook_details.contact.replace(" ","");
			  // update contact on server - we need to verify contact number
			  try {
			    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/register_contact.php",commondata.facebook_details.facebook,commondata.facebook_details.contact,"1","1","1","1","1"
					, "1", "1", "1", "1", "1", "1").get();
			    	 System.out.println("backhand" + server_resp);
					} catch (InterruptedException e) {
									// TODO Auto-generated catch block
						System.out.println("backhander");
						e.printStackTrace();
					} catch (ExecutionException e) {
									// TODO Auto-generated catch block
						System.out.println("backhander");
						e.printStackTrace();
					}
			  // Do something with value!
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
    	}
		//-------
		
		//-----------------------
        /*
         * check if event exists
         */
        
        StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
		strBuilder.append("event-" + commondata.facebook_details.facebook);
	    String st = strBuilder.toString();
	    final Firebase pt = new Firebase(st);
	    System.out.println(pt.child("event-" + commondata.facebook_details.facebook));
	    pt.addListenerForSingleValueEvent(new ValueEventListener() {
	        @Override
	        public void onDataChange(DataSnapshot snapshot) {
	        	
	            if(snapshot.hasChildren()){
	            	/*
	        	     * when someone moves find all location and print it
	        	     */
	        	    pt.addValueEventListener(new ValueEventListener() {

	        			@Override
	        			public void onCancelled(FirebaseError arg0) {
	        				// TODO Auto-generated method stub
	        				
	        			}
	        			@Override
	        			public void onDataChange(DataSnapshot data) {
	        				// TODO Auto-generated method stub
	        				System.out.println("something changed");
	        				
	        				Iterator<DataSnapshot> children = data.getChildren().iterator();
	        				while(children.hasNext()){
	        					DataSnapshot child = children.next();
	                			System.out.println(child.getName() + child.getValue());
	        				}				
	        			}
	        	    	
	        	    });
	            }else{
	            	
	            }
	        }
	        @Override
	        public void onCancelled(FirebaseError firebaseError) {
	        }
	    });
	    
	    
		
		//--------
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
      //--------------------------------------------------------   
	  // Define a listener that responds to location updates
	  //---------------------------------------------------------
		   locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {	
		    	if(location != null){
		    	updatelocation(null);	
		    	commondata.user_information.latitude = location.getLatitude();
	        	commondata.user_information.longitude = location.getLongitude();
		    	}else{
		    		System.out.println("loca null");
		    	}

		    }
			@SuppressWarnings("unused")
			public void onStatusChanged(Location location) {
				if(location != null ){
					updatelocation(null);
				commondata.user_information.latitude = location.getLatitude();
	        	commondata.user_information.longitude = location.getLongitude();
				}else{
					System.out.println("loc null");
				}
				
		    }

			@SuppressWarnings("unused")
			public void onProviderEnabled(Location location) {
				if(location != null){
					updatelocation(null);
				commondata.user_information.latitude = location.getLatitude();
	        	commondata.user_information.longitude = location.getLongitude();
				}else{
					System.out.println("loc null");
				}
		    }
		    public void onProviderDisabled(String provider) {
		    	AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
    			alert.setTitle("Connection Error");
    			alert.setMessage("Please check your network settings");
    			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent intent = new Intent(Login.this, HomescreenActivity.class);
                	startActivity(intent);
                	finish();
    			  }
    			});

    			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int whichButton) {
    				  finish();
    			  }
    			});
    			alert.show();
		    }

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}
		  };
		//------------------------------------------------------------------------
		// getting GPS and network status
	    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Connection Error");
			alert.setMessage("Please check your network settings");
			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(Login.this, HomescreenActivity.class);
            	startActivity(intent);
            	finish();
			  }
			});

			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  finish();
			  }
			});
			alert.show();
        } else {
        	if(isNetworkEnabled){
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        postion_get = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        if(postion_get != null){
		        commondata.user_information.latitude = postion_get.getLatitude();
		        commondata.user_information.longitude = postion_get.getLongitude();
		        System.out.println("loc from network ");
		        }else{
		        	System.out.println("loc from network error");
		        }
		        
        	}else{
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	        	postion_get = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        	if(postion_get != null){
	        	commondata.user_information.latitude = postion_get.getLatitude();
	        	commondata.user_information.longitude = postion_get.getLongitude();
	        	System.out.println("loc from gps ");
	        	}else{
	        		
	        		System.out.println("loc from gps error");
	        	}
        	}
        	
        }
    
  //------------------------------------------------------------------> Fetch the location details
    
  		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
  		List<Address> addresses;
  		
  		try {
              addresses = gcd.getFromLocation(commondata.user_information.latitude, commondata.user_information.longitude, 1);
              if (addresses.size() > 0){
              commondata.user_information.cityname = addresses.get(0).getLocality();
              commondata.user_information.country = addresses.get(0).getCountryCode();
              commondata.user_information.zip = addresses.get(0).getPostalCode();
              profilelistactdata.putString("zip", commondata.user_information.zip);
              commondata.user_information.addressline = addresses.get(0).getThoroughfare();
              }
              else{
            	  System.out.println("address was zero");
              }
             
          } catch (IOException e) {
              e.printStackTrace();
          }

  	//----------------------> Fire base reference creation
		strBuilder = new StringBuilder("https://met-ster.firebaseio.com/");
		strBuilder.append(commondata.user_information.zip);
		strBuilder.append("/");
	    strBuilder.append(commondata.facebook_details.facebook);
	    fbdata.fbref = strBuilder.toString();
	    fbdata.firebaseobj = new Firebase(fbdata.fbref);
	    fbdata.firebaseobj.child("Status").setValue("Hello There!!");
	    fbdata.firebaseobj.child("Latitude").setValue(commondata.user_information.latitude);
	    fbdata.firebaseobj.child("Longitude").setValue(commondata.user_information.longitude);
	    
	    //---- find base place to add to add location to comman data are
	    //---
   
       					        
//-----------------------------------------------------------------------> Button Actions	            
		        rend_button_obj = (Button) findViewById(R.id.Rend);     
		        rend_button_obj.setOnClickListener(new View.OnClickListener() {
			        	public void onClick(View v) {
											
								new Thread(new Runnable() { 
						            public void run(){
						            	SystemClock.sleep(2000);
						            	
											stopRepeatingTask();
											locationManager.removeUpdates(locationListener);
											Intent intentrend = new Intent( Login.this, Rend.class);			
							        		startActivity(intentrend);
											
						            }
						    }).start();						
						}//on click
			        });	        	
//-----------------------------------------------------------------------> Button Actions
		        SetupUIdata();
				
	}//on create
	//------------------------------------- update profile
	
	public void SetupUIdata(){
		
//-----------------------------------------------------------> Setup the GUI with data acquired 
		//----------- Section 1
		TextView fname = (TextView)findViewById(R.id.FirstName);
		String[] f = commondata.facebook_details.name.split(" ");// some name may not split will come back later
        fname.setText(f[0]);
        TextView lname = (TextView)findViewById(R.id.LastName); 
        lname.setText(f[1]);
        TextView prof = (TextView)findViewById(R.id.Profession); 
        prof.setText((String)commondata.user_information.addressline);
        TextView cc = (TextView)findViewById(R.id.CurrentCity); 
        cc.setText((String)commondata.user_information.cityname);
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
        map.getUiSettings().setZoomControlsEnabled(false);
        
//-------------------------------------------------------------------------------------------- 
		
	}
	
	public void updatelocation(View view)
	{
		
		Log.w("called","update_loc");
		_handler.postDelayed(getData, 3000);
		
		if (!isGPSEnabled && !isNetworkEnabled) {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Connection Error");
			alert.setMessage("Please check your network settings");
			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(Login.this, HomescreenActivity.class);
            	startActivity(intent);
            	finish();
			  }
			});

			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  finish();
			  }
			});
			alert.show();
        } else {
        	if(isGPSEnabled){
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        postion_get = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        if(postion_get != null){
		        commondata.user_information.latitude = postion_get.getLatitude();
		        commondata.user_information.longitude = postion_get.getLongitude();
		        }else{
		        	System.out.println("loc error");
		        }
        	}else{
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	        	postion_get = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        	if(postion_get != null){
	        	commondata.user_information.latitude = postion_get.getLatitude();
	        	commondata.user_information.longitude = postion_get.getLongitude();
	        	}else{
	        		System.out.println("loc error");
	        	}
        	}
        	
        }
		
		/*
		 * 	based on the new location update in firebase
		 */
    	fbdata.firebaseobj.child("Latitude").setValue(Double.toString(commondata.user_information.latitude));
    	fbdata.firebaseobj.child("Longitude").setValue(Double.toString(commondata.user_information.longitude));
        
        /*
         *  set up map UI
         */
    	System.out.println("seee");
        GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(commondata.user_information.latitude, commondata.user_information.longitude)) // visitor
                ).showInfoWindow();
	
	}
	
	 

     public void stopRepeatingTask()
     {
    	 _handler.removeCallbacks(getData);
     }
	
	public void updateprofile(View view){
		
    }
	
	
	public void on_image_click(View view){
		
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
			return true;	
			
		}
		return super.onOptionsItemSelected(item);
	}

}