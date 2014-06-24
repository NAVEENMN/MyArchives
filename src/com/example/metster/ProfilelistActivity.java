package com.example.metster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProfilelistActivity extends Activity {
	
	String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
	String accnumber = null;
	Button b;
    ScrollView scrollview;
    String profileimage;
    String visitorimage;
    Bitmap decodedByte ;
    String visitorid = null;
    Drawable y[];
    int prfcounter =0 ;
    Double latival = 0.0;
    Double Longival = 0.0;
    Location pos = null ;
    LocationListener locationListener;
    LocationManager locationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilelist);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//-----------------------------------
		//--------------------------------< title
	    setTitle("Hello!! I am new to Metster"); // user status here
		
		//--------------------------------> Setup location
				//---------------------------------------
		// Acquire a reference to the system Location Manager
	    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		    locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      //makeUseOfNewLocation(location);
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
		        String provider;
		        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        pos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        if( pos == null ) 
		        	{
		        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        	pos = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        	provider = locationManager.getBestProvider(criteria, false);
		        	Location location = locationManager.getLastKnownLocation(provider);
		        	latival = location.getLatitude();
		        	Longival = location.getLongitude();
		        	}
		        else{
		        	latival = pos.getLatitude();
		        	Longival = pos.getLongitude();
		        	}
				String output = null;
				String zip = null;
				Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
				List<Address> addresses;
				try {
		            addresses = gcd.getFromLocation(latival, Longival, 1);
		            zip = addresses.get(0).getPostalCode();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
				
		        //----------------------------------------
		//-----------------------------------
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
		//-----------------------------------
				try {
					
					output = new RequestTask().execute("http://www.naveenmn.com/Metster/profilelist.php",accnumber,appkey,zip,Double.toString(latival),Double.toString(Longival), accnumber,accnumber,
							accnumber, accnumber, accnumber, accnumber, accnumber, accnumber).get();
				   
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//----------------------------------
				if(output.isEmpty()) 
				{
					Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
				}
				else {
				final String[] separated = output.split("#%-->");
				final int len = separated.length;	
				prfcounter += 3;
				Log.w("Latt", separated[prfcounter+1]);
				Log.w("Latt", separated[prfcounter+2]);
				// dummy #%--> usrid #%--> fname lname #%--> image #%--> lat #%--> long
				if (separated[prfcounter]!=null ){
					
					try {
						displayprofile(separated[prfcounter],separated[prfcounter-1],  Double.parseDouble(separated[prfcounter+1]), Double.parseDouble(separated[prfcounter+2]) );
						visitorid = separated[prfcounter-2];
						visitorimage = separated[prfcounter];
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		         
				}
		//---------------------------------- Button next
				Button find = (Button) findViewById(R.id.visitorbuttonnext);
		        find.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							prfcounter += 5;
							if ( prfcounter >= len ) prfcounter = 3 ;
							if (separated[prfcounter]!=null){
							System.out.println("lenght");
							System.out.println(len);
							System.out.println("prfcounter");
							System.out.println(prfcounter);
							System.out.println("prfid");
							System.out.println(separated[prfcounter-1]);
							try {
								
								displayprofile(separated[prfcounter],separated[prfcounter-1], Double.parseDouble(separated[prfcounter+1]), Double.parseDouble(separated[prfcounter+2]) );
								visitorid = separated[prfcounter-2];
								visitorimage = separated[prfcounter];
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
					       }//on click
					
					
		        		});
		//----------------------------------
		      //---------------------------------- Button view profile
				Button findviewprofile = (Button) findViewById(R.id.visitorbuttonviewprofile);
				findviewprofile.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) { // yet to be implemented
							
							Intent intent = new Intent(ProfilelistActivity.this, VisitorProfile.class);
							intent.putExtra("VisitorId",visitorid);
							intent.putExtra("VisitorImage",visitorimage);
			        		startActivity(intent);
							
					       }//on click
					
					
		        		});
		//----------------------------------
		        
		      //---------------------------------- Button next
				Button findprev = (Button) findViewById(R.id.visitorbuttonprev);
				findprev.setOnClickListener(new View.OnClickListener() {
					
						
						public void onClick(View v) {
							
							prfcounter -= 5;
							if ( prfcounter <= 3 ) prfcounter = 3 ; // you have to fix it to last profile
							if (separated[prfcounter]!=null){
							System.out.println("lenght");
							System.out.println(len);
							System.out.println("prfcounter");
							System.out.println(prfcounter);
							System.out.println("prfid");
							System.out.println(separated[prfcounter-1]);
							try {
								
								displayprofile(separated[prfcounter],separated[prfcounter-1], latival, Longival );
								visitorid = separated[prfcounter-2];
								visitorimage = separated[prfcounter];
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
					       }//on click
					
					
		        		});
		//----------------------------------
				} // else ends here
				        
		
	}
	
	public void displayprofile(String image, String visfname ,double lativale , double Longivale ) throws InterruptedException, ExecutionException
	{
		Log.w("Latt", Double.toString(lativale));
		Log.w("Latt", Double.toString(Longivale));
		//-----------------------> display name
		TextView fname = (TextView)findViewById(R.id.txtvisitorFirstName); 
        fname.setText((String)visfname);
        //TextView lname = (TextView)findViewById(R.id.txtLastName); 
        //lname.setText((String)usrlname);
		//-----------------------> put vis on map
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();

        LatLng currlocation = new LatLng(latival, Longival);// yours

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
        
        GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lativale, Longivale))
                .title("Hi")).showInfoWindow();
        
        
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
		    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView imgg = (ImageView)findViewById(R.id.ImagevisitorView01);
        imgg.setImageBitmap(decodedByte);
		
	}

	//---------------------
	@Override
	public void onBackPressed() {
	 
		locationManager.removeUpdates(locationListener);
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
