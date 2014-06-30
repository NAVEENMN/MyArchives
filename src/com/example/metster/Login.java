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
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Login extends Activity {
	
	//----------------------------------------->
		String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
		Geocoder gcd ;
		GoogleMap mMap;
		LocationManager locationManager;
		LocationListener locationListener;
		Location location;
		Location pos = null ;
		String provider;	
		String usremail;
		
		List<Address> addresses;
		String cityName = null;
		String country = null;
		String addressline = null;
		String state = null;
		String zip = null;
	//----------------------------------------->
		String usrfname;
		String usrlname;
		String usrgender;
		String usrage;
		String usrageandgender;
		String usrprofession;
		String usrworksat;
		String usrcurrentcity;
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
		int len = 0;
    //---------------------------------------->

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();
		setTitle("Metster");
		//--------------------------------> Setup location
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
      //------------------------------------------------------------------------
// Register the listener with the Location Manager to receive location updates
  		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
//--------------------------------------------------------------------------------------------------------

//------------------------------------------------------------------> Fetch the location details
		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		List<Address> addresses;
		String cityName = null;
		String country = null;
		String addressline = null;
		String state = null;
		String zip = null;
		String myloc = null;
		try {
            addresses = gcd.getFromLocation(latival, Longival, 1);
            if (addresses.size() > 0)
            cityName = addresses.get(0).getLocality();
            country = addresses.get(0).getCountryCode();
            state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();
            addressline = addresses.get(0).getThoroughfare();
            myloc = addressline+", "+cityName;
           
        } catch (IOException e) {
            e.printStackTrace();
        }
//-------------------------------------------------------------------------------------------------------

//--------------------------------> Read user ID
				
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
	            usrgender = separated[2];
	            usrage = separated[3];
	            usrprofession = separated[4];
	            usrworksat = separated[5];
	            usrcurrentcity = separated[6];
	            usrageandgender = usrgender + " | "+usrage;
	            
	            
				if(output.contains("null")){
					Toast.makeText(getApplicationContext(), "Metster is unable to connect to server at this time.", Toast.LENGTH_SHORT).show();
				}
				else{
					
//------------------------------
					//-----------------------------------> post here
					
					String numb = null;
			        try {
			        	numb = new RequestTask().execute("http://www.naveenmn.com/Metster/numberofusers.php",accnumber,appkey,zip,Double.toString(latival),Double.toString(Longival), accnumber,accnumber,
								accnumber, accnumber, accnumber, accnumber, accnumber, accnumber).get();
			            
			        	} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	//----------------------------------
			if(numb.isEmpty()) 
			{
				Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
			}
			else {
			final String[] accountnumbers = numb.split("#%-->");
		     len = accountnumbers.length;
			}
			//------
            Time now = new Time();
            String ampm= "am";
            int offset = 15;
            now.setToNow(); 
            int hour = now.hour;
            int minute = now.minute;
            minute = minute + offset;
            if(minute >= 60){
            	hour++;
            	minute = minute - 60;
            	
            }
            if(hour > 12){ 
        		hour = hour - 12;
        		ampm = "pm"; // still need to fix
        	}
            //------
//-----------------------------------------------------------> Setup the GUI with data acquired
				//----------- Section 1
				TextView fname = (TextView)findViewById(R.id.FirstName); 
		        fname.setText((String)usrfname);
		        TextView lname = (TextView)findViewById(R.id.LastName); 
		        lname.setText((String)usrlname);
		        TextView prof = (TextView)findViewById(R.id.Profession); 
		        prof.setText((String)usrprofession);
		        TextView wat = (TextView)findViewById(R.id.Worksat); 
		        wat.setText((String)usrworksat);
		        TextView ag = (TextView)findViewById(R.id.AgeandGender); 
		        ag.setText((String)usrageandgender);
		        TextView cc = (TextView)findViewById(R.id.CurrentCity); 
		        cc.setText((String)usrcurrentcity);
		        //----------- Section 2
		        TextView loca = (TextView)findViewById(R.id.YourLocation); 
	            loca.setText((String)myloc);
	            TextView num = (TextView)findViewById(R.id.NumberofUsers); 
	            num.setText(Integer.toString(len));
	            TextView tim = (TextView)findViewById(R.id.Invisiblein);
	            String time = hour+":"+minute+" "+ampm;
	            tim.setText(time);
	            //----------- Section Profile Image
	            
	            if (profileimage!=null){
                    byte[] decodedString = Base64.decode(profileimage, Base64.DEFAULT);
   		             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
   		             	ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
   		                imageButton.setImageBitmap(decodedByte);
                      }
	            //----------- Section Maps
	            GoogleMap map = ((MapFragment) getFragmentManager()
	                    .findFragmentById(R.id.visitormap)).getMap();

	            LatLng currlocation = new LatLng(latival, Longival);// yours

	            map.setMyLocationEnabled(true);
	            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
	            //-------------- Section Footer
	            String footertext = "Metster \u00a9 2014, Apha Ver1.1";
	            TextView foot = (TextView)findViewById(R.id.footer);
	            foot.setText(footertext);
//--------------------------------------------------------------------------------------------  
		        
//-----------------------------------------------------------------------> Button Actions	            
	            //------------------------------------- meet someone
		        find = (Button) findViewById(R.id.buttonmeet);
		        find.setOnClickListener(new View.OnClickListener() {	
						public void onClick(View v) {
							
							Intent intent1 = new Intent( Login.this, ProfilelistActivity.class);
			        		startActivity(intent1);
							       
					}//on click
					
		        		}
		        );
		        
//------------------------------------------------------------------------------------------------		        
		        
} // else ends here
				
				
	}
	//------------------------------------- update profile
	public void updateprofile(View view){
    	Intent intent2 = new Intent( Login.this, UpdateProfile.class);
		startActivity(intent2);
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
