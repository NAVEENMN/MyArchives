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
import android.location.LocationManager;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Login extends Activity {
	//----------------------------------------->
	String usremail;
	String usrfname;
	String usrlname;
	String ret = "";
	String accnumber = "";
	String tokennumber = "";
	String profileimage = "";
	private Button find;
	Double Mylatitude;
	Double MyLongitude;
	Double gpsMylatitude;
	Double gpsMyLongitude;
	Double loclat;
	Double loclon;
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
		System.out.println("comes here");
		//---------------------------------------
        LocationManager locationManager;
        String provider;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = locationManager.getLastKnownLocation("gps");
        if (currentLocation != null)
        {
        	gpsMylatitude = currentLocation.getLatitude();
        	gpsMyLongitude = currentLocation.getLongitude();
        }
        System.out.println(gpsMylatitude);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        System.out.println("latvalues:");
        System.out.println(location.getLatitude());
        System.out.println("will it come here?");
		Log.v("latibaby:", Double.toString(location.getLatitude()));
		Log.v("longibaby:", Double.toString(location.getLongitude()));
		Toast.makeText(getApplicationContext(), Double.toString(location.getLatitude())+ ","+Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
		Double latival = location.getLatitude();
		Double Longival = location.getLongitude();
		//------------------------------
		//----------------------- put on maps
	     // Get a handle to the Map Fragment
	        GoogleMap map = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();

	        LatLng currlocation = new LatLng(latival, Longival);

	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
	        
	        //-----------------------------------------
		Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		List<Address> addresses;
		String cityName = null;
		String country = null;
		String addressline = null;
		String state = null;
		String zip = null;
		try {
            addresses = gcd.getFromLocation(latival, Longival, 1);
            if (addresses.size() > 0)
            	System.out.println(addresses.get(0).getPostalCode());
            TextView t1 = (TextView)findViewById(R.id.txtCurrentLocation);
            TextView t2 = (TextView)findViewById(R.id.txtCurrentCounty);
            TextView t3 = (TextView)findViewById(R.id.txtCurrentCountry);
            cityName = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryCode();
            state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();
            addressline = addresses.get(0).getThoroughfare();
            t1.setText((String)addressline);
            t2.setText((String)cityName);
            t3.setText((String)state+" "+(String)country);
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
					output = new RequestTask().execute("http://www.naveenmn.com/Metster/fetchprofile.php",accnumber,tokennumber,zip,Double.toString(latival),Double.toString(Longival),(String)country,accnumber).get();
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
	            
				if(output.contains("null")){
					Toast.makeText(getApplicationContext(), "Metster is unable to connect to server at this time.", Toast.LENGTH_SHORT).show();
				}
				else{
		        //-----------------------------------------------------------
		        //---------------------------------------
		        TextView fname = (TextView)findViewById(R.id.txtFirstName); 
	            fname.setText((String)usrfname);
                TextView lname = (TextView)findViewById(R.id.txtLastName); 
	            lname.setText((String)usrlname);
	            if (profileimage!=null){
                    byte[] decodedString = Base64.decode(profileimage, Base64.DEFAULT);
   		             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView imgg = (ImageView)findViewById(R.id.ImageView01);
                        imgg.setImageBitmap(decodedByte);
                      }
	            
	            //-------------------------------------------
	         // Define a listener that responds to location updates   
		        
		        find = (Button) findViewById(R.id.buttonmeet);
		        find.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							Intent intent1 = new Intent( Login.this, ProfilelistActivity.class);
			        		startActivity(intent1);
							       
					}//on click
					
					
		        		}
		        
		        
		        );
				} // else ends here
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
