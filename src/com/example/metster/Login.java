package com.example.metster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
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
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class Login extends Activity {
	
	//----------------------------------------->

		
		Geocoder gcd ;
		List<Address> addresses;
		String cityName = null;
		String country = null;
		String addressline = null;
		String state = null;
		String zip = null;
		
	
	
	//----------------------------------------->
	GoogleMap mMap;
	LocationManager locationManager;
	LocationListener locationListener;
	Location location;
	String provider;	
	String usremail;
	String usrfname;
	Location pos = null ;//
	String usrlname;
	String ret = "";
	String accnumber = "";
	String tokennumber = "";
	String profileimage = "";
	private Button find;
	Double latival = 0.0 ;
	Double Longival = 0.0 ;
	Location lon2 = new Location("");
	int flag = 0;
	final ArrayList<String> USERNAME = new ArrayList<String>();	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//--------------------------------> Setup location
		//---------------------------------------
// Acquire a reference to the system Location Manager
	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
   locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {
    	
    	latival = location.getLatitude();
    	Longival = location.getLongitude();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}
  };

// Register the listener with the Location Manager to receive location updates
  		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		//---------------------------------------
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        pos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if( pos == null ) 
        	{
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        	pos = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	provider = locationManager.getBestProvider(criteria, false);
        	location = locationManager.getLastKnownLocation(provider);
        	latival = location.getLatitude();
        	Longival = location.getLongitude();
        	}
        else{
        	latival = pos.getLatitude();
        	Longival = pos.getLongitude();
        	}		
		//--------------------------------------------------------------> Turn off location listener after 5 mins
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            //Do something after 100ms
        	  locationManager.removeUpdates(locationListener);
        	 // doafter();
        	  
          }
        }, 1000 * 60 * 5  );
        //----------------------------------------------------------------
        
		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		List<Address> addresses;
		String cityName = null;
		String country = null;
		String addressline = null;
		String state = null;
		String zip = null;
		try {
            addresses = gcd.getFromLocation(latival, Longival, 1);
            if (addresses.size() > 0)
            cityName = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryCode();
            state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();
            addressline = addresses.get(0).getThoroughfare();
           
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.w("city",cityName);
		//----------------------------------------------
		//--------------------------------> Read user ID
		//-------------------------------------
				
		        try {
		            FileInputStream inputStream = openFileInput("accounts.txt");

		            if ( inputStream != null ) {
		                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		                String receiveString = "";
		                StringBuilder stringBuilder = new StringBuilder();

		                while ( (receiveString = bufferedReader.readLine()) != null ) {
		                    stringBuilder.append(receiveString);
		                }

		                inputStream.close();
		                accnumber = stringBuilder.toString();
		                Log.w("serveraccnumber",accnumber);
		            }
		        }
		        catch (FileNotFoundException e) {
		            Log.e("login activity", "File not found: " + e.toString());
		            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
		            .show();
		        } catch (IOException e) {
		            Log.e("login activity", "Can not read file: " + e.toString());
		            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
		            .show();
		        }	
		      //--------------------------------> Read token
				//-------------------------------------
						
				        try {
				            FileInputStream inputStream = openFileInput("token.txt");

				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                String receiveString = "";
				                StringBuilder stringBuilder = new StringBuilder();

				                while ( (receiveString = bufferedReader.readLine()) != null ) {
				                    stringBuilder.append(receiveString);
				                }

				                inputStream.close();
				                tokennumber = stringBuilder.toString();
				                Log.w("servertoken",tokennumber);
				            }
				        }
				        catch (FileNotFoundException e) {
				            Log.e("login activity", "File not found: " + e.toString());
				            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
				            .show();
				        } catch (IOException e) {
				            Log.e("login activity", "Can not read file: " + e.toString());
				            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
				            .show();
				        }	
				        
				      //--------------------------------> Read image from file
						//-------------------------------------
								
						        try {
						            FileInputStream inputStream = openFileInput("image.txt");

						            if ( inputStream != null ) {
						                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
						                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						                String receiveString = "";
						                StringBuilder stringBuilder = new StringBuilder();

						                while ( (receiveString = bufferedReader.readLine()) != null ) {
						                    stringBuilder.append(receiveString);
						                }

						                inputStream.close();
						                profileimage = stringBuilder.toString();
						            }
						        }
						        catch (FileNotFoundException e) {
						            Log.e("login activity", "File not found: " + e.toString());
						            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
						            .show();
						        } catch (IOException e) {
						            Log.e("login activity", "Can not read file: " + e.toString());
						            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
						            .show();
						        }
		        
				//-------------------------------------> Read data from server
	            String output = null;
	            System.out.print("fetching");
	            try {
	            	//ProgressDialog dialog = new ProgressDialog(Login.this);
	                //dialog.setMessage("Fetching Profile..");
	                //dialog.show();
					output = new RequestTask().execute("http://www.naveenmn.com/Metster/fetchprofile.php",accnumber,tokennumber,zip,Double.toString(latival),Double.toString(Longival),(String)country,accnumber
							, accnumber, accnumber, accnumber, accnumber, accnumber, accnumber).get();
					//if (! output.isEmpty()){
					//dialog.dismiss();
					//}
	            } catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            //System.out.println(output);
	            String[] separated = output.split("-");
	            usrfname = separated[0];
	            usrlname = separated[1];
	            
	            setTitle(usrfname + " " + usrlname);
	            
				if(output.contains("null")){
					Toast.makeText(getApplicationContext(), "Metster is unable to connect to server at this time.", Toast.LENGTH_SHORT).show();
				}
				else{
		        //-----------------------------------------------------------
					String v = "Naveen Mysore";
					String affiliation = "Graduate Student | Computer Science";
					TextView name = (TextView)findViewById(R.id.Name); 
		            name.setText((String)v);
	                TextView affil = (TextView)findViewById(R.id.Affil); 
		            affil.setText((String)affiliation);
		        //---------------------------------------
		       // TextView fname = (TextView)findViewById(R.id.Location); 
	           // fname.setText((String)addressline);
               // TextView lname = (TextView)findViewById(R.id.City); 
	           // lname.setText((String)cityName);
	            if (profileimage!=null){
                    byte[] decodedString = Base64.decode(profileimage, Base64.DEFAULT);
   		             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView imgg = (ImageView)findViewById(R.id.ImageView01);
                        imgg.setImageBitmap(decodedByte);
                      }
	            
	            //-------------------------------------------
	         // Define a listener that responds to location updates   
		        
	            //------------------------------------- meet someone
		        find = (Button) findViewById(R.id.buttonmeet);
		        find.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							Intent intent1 = new Intent( Login.this, ProfilelistActivity.class);
			        		startActivity(intent1);
							       
					}//on click
					
					
		        		}
		        
		        
		        );
		        
		      //------------------------------------- update profile
		        find = (Button) findViewById(R.id.updateprofile);
		        find.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							Intent intent2 = new Intent( Login.this, UpdateProfile.class);
			        		startActivity(intent2);
							       
					}//on click
					
					
		        		}
		        
		        
		        );
		        
		        
		        
		        
				} // else ends here
				
				
	}
	
	@Override
	public void onBackPressed() {
	 
		locationManager.removeUpdates(locationListener);
		Login.this.finish();
		
	}

	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		//getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
