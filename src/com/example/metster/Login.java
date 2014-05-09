package com.example.metster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Login extends Activity {
	private static final long MIN_TIME_BW_UPDATES = 0;
	private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
	//----------------------------------------->
	String usremail;
	String usrfname;
	String usrlname;
	InputStream usrimg;
	String ret = "";
	private Button find;
	Double Mylatitude;
	Double MyLongitude;
	Double loclat;
	Double loclon;
	Location lon2 = new Location("");
	int flag = 0;
	final ArrayList<String> USERNAME = new ArrayList<String>();
	//----------------------------------------->
	
	//------------------------------------------>
	

	//----------------------------------> Findsomeone   		
/*	public void findsomeone(View view) {
		Firebase baseRef = new Firebase("https://metster.firebaseIO.com/totalusers/User"+ret);
        Firebase LAT = baseRef.child("Latitude");
        Firebase LONG = baseRef.child("Longitute");
        //------------------------------------------------>
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
        //------------------------------------------------>
        LAT.setValue(Double.toString(longitude));
        LONG.setValue(Double.toString(latitude));
	}*/
	//---------------------------------------------------------------
	//----------------------------------------->

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Show the Up button in the action bar.
		setupActionBar();
		//--------------------------------> Read user ID
		//------------------------------------->
		
				
		        try {
		            FileInputStream inputStream = openFileInput("myfile.txt");

		            if ( inputStream != null ) {
		                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		                String receiveString = "";
		                StringBuilder stringBuilder = new StringBuilder();

		                while ( (receiveString = bufferedReader.readLine()) != null ) {
		                    stringBuilder.append(receiveString);
		                }

		                inputStream.close();
		                ret = stringBuilder.toString();
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
		        
				
		        
		        
		        //------------------------------> Read data from firebase
		        
		      //-------------------------------------------
	            Firebase dataRef = new Firebase("https://metster.firebaseIO.com/totalusers/User"+ret);
	            Firebase email = dataRef.child("Email");
	            Firebase fname = dataRef.child("First Name");
	            Firebase lname = dataRef.child("Last Name");
	            Firebase prfpic = dataRef.child("Image");
	            //--------------------------------------------------
	            email.addValueEventListener(new ValueEventListener() {
	                @Override
	                public void onDataChange(DataSnapshot snapshot) {
	                    usremail =  (String) snapshot.getValue();
	                    Log.w("Email",usremail);
	                }

	                @Override
	    			public void onCancelled(FirebaseError arg0) {
	    				// TODO Auto-generated method stub
	    				
	    			}
	            });
	            //-------------------------------------------
	          //--------------------------------------------------
	            fname.addValueEventListener(new ValueEventListener() {
	                @Override
	                public void onDataChange(DataSnapshot snapshot) {
	                    usrfname =  (String) snapshot.getValue();
	                    Log.w("First Name",usrfname);
	                    TextView t = (TextView)findViewById(R.id.txtFirstName); 
	    	            t.setText((String)usrfname);
	                }

	                @Override
	    			public void onCancelled(FirebaseError arg0) {
	    				// TODO Auto-generated method stub
	    				
	    			}
	            });
	            //-------------------------------------------
	          //--------------------------------------------------
	            lname.addValueEventListener(new ValueEventListener() {
	                @Override
	                public void onDataChange(DataSnapshot snapshot) {
	                    usrlname =  (String) snapshot.getValue();
	                    Log.w("Last Name",usrlname);
	                    TextView t = (TextView)findViewById(R.id.txtLastName); 
	    	            t.setText((String)usrlname);
	                }

	                @Override
	    			public void onCancelled(FirebaseError arg0) {
	    				// TODO Auto-generated method stub
	    				
	    			}
	            });
	            //-------------------------------------------
	          //--------------------------------------------------
	            prfpic.addValueEventListener(new ValueEventListener() {
	                @Override
	                public void onDataChange(DataSnapshot snapshot) {
	                	String usrimg = (String) snapshot.getValue();
	                    if (usrimg!=null){
	                    byte[] decodedString = Base64.decode(usrimg, Base64.DEFAULT);
	   		             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	                        ImageView t = (ImageView)findViewById(R.id.ImageView01);
	                        t.setImageBitmap(decodedByte);
	                      }
	                    
	                }

	                @Override
	    			public void onCancelled(FirebaseError arg0) {
	    				// TODO Auto-generated method stub
	    				
	    			}
	            });
	            //-------------------------------------------
	         // Define a listener that responds to location updates   
		        
		        find = (Button) findViewById(R.id.buttonmeet);
		        find.setOnClickListener(new View.OnClickListener() {
					
						public void onClick(View v) {
						Firebase baseRef = new Firebase("https://metster.firebaseIO.com/totalusers/User"+ret);
				        Firebase LAT = baseRef.child("Latitude");
				        Firebase LONG = baseRef.child("Longitute");
				        LocationManager locationManager;
				        String provider;
				        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				        Criteria criteria = new Criteria();
				        provider = locationManager.getBestProvider(criteria, false);
				        Location location = locationManager.getLastKnownLocation(provider);
						Log.v("latibaby:", Double.toString(location.getLatitude()));
						Log.v("longibaby:", Double.toString(location.getLongitude()));
						Toast.makeText(getApplicationContext(), Double.toString(location.getLatitude()), Toast.LENGTH_SHORT).show();
						LAT.setValue(Double.toString(location.getLatitude()));
						LONG.setValue(Double.toString(location.getLongitude()));
						//------------------------------ 
				       
					}//on click
					
					
				});
		      
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
