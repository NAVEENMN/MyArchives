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
				        
				        //LAT.setValue());
				        //LONG.setValue(Double.toString(LONG));
				        Mylatitude = -79.099237;
				        MyLongitude = 36.07337782;
				        
				        //------------------------------------------------>
				        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				        // Define the criteria how to select the locatioin provider -> use
				        // default
				        
				        Criteria criteria = new Criteria();
				        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				        String provider = locationManager.getBestProvider(criteria, true);
						if(location != null ){
						double latitude = location.getLatitude();
						Mylatitude = latitude;
						double longitude = location.getLongitude();
						MyLongitude = longitude;
				        //------------------------------------------------>
				        LAT.setValue(Double.toString(latitude));
				        LONG.setValue(Double.toString(longitude));
						}
						else Log.w("nope","nope");
						
						
						//------------------------------
						
						//-------------------------------------->finding common
				        int i =0;
				        final ArrayList<String> lat = new ArrayList<String>();
				        final ArrayList<String> lon = new ArrayList<String>();
				        final Location lo2 = new Location("");
				        for( i =1; i<= 4; i++)
				        {
				        	String rett = Integer.toString(i);
				        	final Firebase dataReff = new Firebase("https://metster.firebaseIO.com/totalusers/User"+rett);
				        	Firebase Latitude = dataReff.child("Latitude");
				            Firebase Longitude = dataReff.child("Longitute");
				            
				            //---------------------------------------------------------
				            Latitude.addValueEventListener(new ValueEventListener() {
				                
				                public void onDataChange(DataSnapshot snapshot) {
				                    String lati =  (String) snapshot.getValue();
				                    lat.add(lati);
				                    lo2.setLatitude(Double.parseDouble(lati));
	//			                    Toast.makeText(getApplicationContext(), lati, Toast.LENGTH_SHORT).show();
				                }

				                
				    			public void onCancelled(FirebaseError arg0) {
				    				// TODO Auto-generated method stub
				    				
				    			}
				            });
				            //-----------------------------------------------------------
				            Longitude.addValueEventListener(new ValueEventListener() {
				                
				                public void onDataChange(DataSnapshot snapshot) {
				                    String longi =  (String) snapshot.getValue();
				                    lon.add(longi);
				                    lo2.setLongitude(Double.parseDouble(longi));
				                    //Log.w("longitude:",longi);
				                    //loclon = Double.valueOf(usr).doubleValue();
				                    //lon2.setLongitude(loclon);
				                    //Log.w("Lon#",usr);
				                    //Log.w("loclon",Double.toString(loclon));
				                    //Location lon1 = new Location("");
						            //lon1.setLatitude(Mylatitude);
						            //lon1.setLongitude(MyLongitude);
						            //Double distance = (double) lon1.distanceTo(lon2);
						    
				                }
				              
				                
				    			public void onCancelled(FirebaseError arg0) {
				    				// TODO Auto-generated method stub
				    				
				    			}
				            });   
				            
				            Location lon1 = new Location("");
					          lon1.setLatitude(Mylatitude);
					          lon1.setLongitude(MyLongitude);
					        float dis = lon1.distanceTo(lon2);
					        Toast.makeText(getApplicationContext(), "Dis: " + dis,
					        		Toast.LENGTH_SHORT).show();
			
				                
				            //-----------------------------------------------------------
				     
				        }// for loop  
				       
					}//on click
					
					
				});
		      
	}

	
	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
