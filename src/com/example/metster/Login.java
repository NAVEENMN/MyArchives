package com.example.metster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metster.Rend.group;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
		
	public static class event_info{
		static String event_name;
		static String food_type;
		static String is_exist;
		static String is_food_chosen;
	}
	public static class Userslist{
		static String server_response;
		static String numberofusers;
		static int user_count;
	};
	public static class fb_event_ref{
		static String fbref;
		static Firebase firebaseobj;
	}
	private static class contact_info{
		static String contact_name;
		static String contact_number;
	}
	//----------------------------------------->
	//
	private static final String TAG = Rend.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private String access_to_button;
    private String event_tracer_is_setup;
    private String initiate;
	AlertDialog levelDialog = null;
	private static final int CONTACT_PICKER_RESULT = 1001;
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
    ValueEventListener listn;
    ChildEventListener child_listner;
    Boolean listnerflag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();// Show the Up button in the action bar.
		commondata.user_information.status = "Hello There!!";
		setTitle("New Event");
		Firebase.setAndroidContext(this);
	    listnerflag = true;
	    event_info.food_type = "american";
		/*
		 * if contact info is not present ask from user
		 * verify contact
		 * meet up point
		 * voting
		 * refresh
		 * location updates
		 */
		if(commondata.facebook_details.contact.isEmpty()){
			req_contact();
    	}
		
		if(commondata.facebook_details.fb.isSessionValid()){
			System.out.println("yes valid dude");
		}else{
			System.out.println("no not valid");
		}
		//-------
		
		Button b1 = (Button) findViewById(R.id.Rend);
		b1.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	            // TODO Auto-generated method stub
	         System.out.println("long pressed");
	            return true;
	        }
	    });
		//-----------------------
        /*
         * check if event exists
         */
        
		create_firebase_refrence();
	        
	   if(commondata.event_information.eventID != null){//this will be set from res of mysql
					/*
	        	     * when someone moves find all location and print it
	        	     */
		   
		   
		fb_event_ref.firebaseobj.addChildEventListener(child_listner = new ChildEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildRemoved(DataSnapshot child) {
				// TODO Auto-generated method stub
				 Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					final int height = size.y;
				System.out.println("changed" + child.getName());
				String rawname = child.getName();
				String[] name = rawname.split("--");// name[0] will have id and name[1] will have name
				
				if(commondata.event_information.eventID.contains(name[0])){//host has left
					Toast toast= Toast.makeText(getApplicationContext(), 
							   "host has left the event", Toast.LENGTH_SHORT);  
							toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
							TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
							//v.setBackgroundColor(Color.TRANSPARENT);
							v.setTextColor(Color.rgb(175, 250, 176));
							toast.show();
				}else{
					Toast toast= Toast.makeText(getApplicationContext(), 
							 name[1] + " has left the event", Toast.LENGTH_SHORT);  
							toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
							TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
							//v.setBackgroundColor(Color.TRANSPARENT);
							v.setTextColor(Color.rgb(175, 250, 176));
							toast.show();
				}
				
			}
			   
		   });
		   
		   
		   /*
		    * value listner
		    */
		   
		   fb_event_ref.firebaseobj.addValueEventListener(listn = new ValueEventListener() {

			// ....

			// ....
	        			@Override
	        			public void onCancelled(FirebaseError arg0) {
	        				// TODO Auto-generated method stub
	        				
	        			}
	        			@Override
	        			public void onDataChange(DataSnapshot data) {
	        				set_up_map_view();//map is updated for previous data
	        				if(data.hasChildren()){//members are present
	        				// TODO Auto-generated method stub
	        				System.out.println("something changed");
	        				commondata.places_found.latitudes.clear();
	        				commondata.places_found.longitudes.clear();
	        				commondata.places_found.names.clear();
	        				Iterator<DataSnapshot> children = data.getChildren().iterator();
	        				commondata.event_information.host = null;
	        				while(children.hasNext()){// handle cases here like if no latitude etc
	        					DataSnapshot child = children.next();
	        					String rawname = child.getName();
	        					String[] rawdata = rawname.split("--");//rawdata[1] will have name -- raw[0] will have id
	        					if(commondata.event_information.eventID.contains(rawdata[0])){//then he is the host
	        						commondata.event_information.host = rawdata[1];
	        						setTitle("(host)"+commondata.event_information.host);
	        					}
	        					try{
	                			String[] temp1 =child.getValue().toString().split(" ");
	                			String part0 = temp1[0];
	                			String part1 = temp1[1];
	                			String[] comp1 = part0.split("=");
	                			String[] comp2 = part1.split("=");
	                			String longitude = comp1[1].replace(",", "");
	                			String latitude = comp2[1].replace("}", "");
	                			commondata.places_found.latitudes.add(Double.parseDouble(latitude));
	                			commondata.places_found.longitudes.add(Double.parseDouble(longitude));
	                			commondata.places_found.names.add(rawdata[1]);
	        					}catch(Exception e){
	        						System.out.println("pair error");
	        					}
	        				}
	        				//after the loop if host is still null then host has left
	        				
	        				System.out.println("hostname:" + commondata.event_information.host);
	        				System.out.println("latitudes " + commondata.places_found.latitudes.toString());
	        				System.out.println("longitudes " + commondata.places_found.longitudes.toString());
	        				if(commondata.places_found.latitudes.size() != 0 && commondata.places_found.longitudes.size() != 0){
	        				if(commondata.places_found.latitudes.size() == commondata.places_found.longitudes.size()){
	        					set_up_map_view();
	        				}
	        				}
	        			}else{//no members -- clear mysql reset eventid and refresh
	        				
	        				commondata.event_information.eventID = null;
	        				  /*
	        				   * reset the event
	        				   */
	        				  try {
	        				    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/resetevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
	        				  Intent intent = new Intent(Login.this, Login.class);
	        		         	startActivity(intent);
	        		         	finish();
	        			}
	        		}
	        	    	
	        	    });
	            
	            	}else{//event is not there
	            		/*
	            		locationManager.removeUpdates(locationListener);
	        			fb_event_ref.firebaseobj.removeEventListener(listn);
	        			
	        			StringBuilder strBuildertmp = new StringBuilder("https://met-ster-event.firebaseio.com/");
	        			strBuildertmp.append(commondata.event_information.eventID+"/"+commondata.facebook_details.facebook);
	        		    String tempref = strBuildertmp.toString();
	        		    Firebase tempfb = new Firebase(tempref);
	        			tempfb.removeValue();
	        			commondata.event_information.eventID = null;
	        			 try {
	        		    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/resetevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
	        			Intent intent = new Intent(Login.this, Login.class);
	                 	startActivity(intent);
	                 	finish();
	                 	*/
	            		
	            	}
	        
	       
	    
	    
		
		//--------
	   
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
      //--------------------------------------------------------   
	  // Define a listener that responds to location updates
	  //---------------------------------------------------------
		   locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {	
		    	if(location != null){
		    	
		    	commondata.user_information.latitude = location.getLatitude();
	        	commondata.user_information.longitude = location.getLongitude();
	        	if(commondata.event_information.eventID != null){
	    			System.out.println("gets called" + commondata.event_information.eventID);
	    		create_firebase_refrence();
	    		fb_event_ref.firebaseobj.child(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name).child("Latitude").setValue(commondata.user_information.latitude);
	    	    fb_event_ref.firebaseobj.child(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name).child("Longitude").setValue(commondata.user_information.longitude);
	    		}
		    	}else{
		    		System.out.println("loca null");
		    	}

		    }
			@SuppressWarnings("unused")
			public void onStatusChanged(Location location) {
				if(location != null ){
				
				commondata.user_information.latitude = location.getLatitude();
	        	commondata.user_information.longitude = location.getLongitude();
				}else{
					System.out.println("loc null");
				}
				
		    }

			@SuppressWarnings("unused")
			public void onProviderEnabled(Location location) {
				if(location != null){
				
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
	
	public void set_up_map_view(){
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        try{
        for(int i = 0; i< commondata.places_found.latitudes.size(); i++){
        	mMap.addMarker(new MarkerOptions()
            .position(new LatLng(commondata.places_found.latitudes.get(i), commondata.places_found.longitudes.get(i))) // visitor
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
            .title(commondata.places_found.names.get(i)));
        }
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours
        
        mMap.getUiSettings().setZoomControlsEnabled(false);
        }catch(Exception e){
        	System.out.println("something fishy");
        	//ret_data();
        }
	}
	
	 

     public void stopRepeatingTask()
     {
    	 _handler.removeCallbacks(getData);
     }
	
	public void updateprofile(View view){
		
    }
	
	
	public void on_image_click(View view){
		if(listnerflag){
			listnerflag = false;
			LinearLayout ll = (LinearLayout) findViewById(R.id.Profiledata);
			View v = (View) findViewById(R.id.Dividerone);
			ll.setBackgroundColor(Color.parseColor("#FE642E"));
			v.setBackgroundColor(Color.parseColor("#FE642E"));
			locationManager.removeUpdates(locationListener);
		
		}else{
			listnerflag = true;
			LinearLayout ll = (LinearLayout) findViewById(R.id.Profiledata);
			ll.setBackgroundColor(Color.parseColor("#2E9AFE"));
			View v = (View) findViewById(R.id.Dividerone);
			v.setBackgroundColor(Color.parseColor("#2E9AFE"));
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}
		System.out.println("status: " + listnerflag.toString());
	}
	
	@Override
	public void onBackPressed() {
	 
		new AlertDialog.Builder(this)
		.setView(getLayoutInflater().inflate(R.layout.custom_exit, null))
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	stopRepeatingTask();
        		locationManager.removeUpdates(locationListener);
        		try{
        		fb_event_ref.firebaseobj.removeEventListener(listn);
        		}catch(Exception e){
        			System.out.println("no fb ref");
        		}
        		
        		Login.this.finish();
            }
        })
        .setNegativeButton("No", null)
        .show();	
	}

	//-----------------
	//-----------------
	
	/*
	 * This method will add a new member in firebase
	 */
	
	public void add_a_member_to_fb(final String member_email){
		
		Thread thread = new Thread()
		{
		      @Override
		      public void run() {
		    	  Looper.prepare();
					create_firebase_refrence();
					Toast.makeText(getApplicationContext(), group.curr_person + " has been added", Toast.LENGTH_SHORT).show();
					try {
				    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/exe_gcm_send.php",commondata.facebook_details.facebook,member_email,"this is message","1","1","1","1"
						, "1", "1", "1", "1", "1", "1").get();
				    	 System.out.println("contact-"+member_email);
				    	 System.out.println(server_resp);
						} catch (InterruptedException e) {
										// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
										// TODO Auto-generated catch block
							e.printStackTrace();
						}
		      }
		  };

		thread.start();			
	}
	/*
	 * this method prompts user to create a event
	 */
	public void create_event_notfication(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setView(getLayoutInflater().inflate(R.layout.custom_dialog, null));
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  /*
		   * add the host to firebase
		   */
		    commondata.event_information.eventID = "event-"+commondata.facebook_details.facebook;
		    create_firebase_refrence();
		    fb_event_ref.firebaseobj.child(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name).setValue(commondata.facebook_details.name);
		    fb_event_ref.firebaseobj.child(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name).child("Latitude").setValue(commondata.user_information.latitude);
		    fb_event_ref.firebaseobj.child(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name).child("Longitude").setValue(commondata.user_information.longitude);
		  pick_food_type();
		  /*
		   * store the event id on mysql
		   */
		  try {
		    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/updateevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
		  
		  }
		
		
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  event_info.event_name = "Metster-event";
			  setTitle("Create an Event");
			  commondata.event_information.eventID = null;
			  /*
			   * reset the event
			   */
			  try {
			    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/resetevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
		  }
		});

		alert.show();
	}
	
	private void req_contact(){
		
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
		  
		  /*
		   * verify the contact
		   */
		 Boolean status = verify_contact(value.toString());
		 Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			final int height = size.y;
		 if(status){
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
		  Toast toast= Toast.makeText(getApplicationContext(), 
					 "contact has been updated", Toast.LENGTH_SHORT);  
					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
					//v.setBackgroundColor(Color.TRANSPARENT);
					v.setTextColor(Color.rgb(175, 250, 176));
					toast.show();
		  // Do something with value!
		  }else{
			  
			  Toast toast= Toast.makeText(getApplicationContext(), 
						 "some error in your contact number", Toast.LENGTH_SHORT);  
						toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
						TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
						//v.setBackgroundColor(Color.TRANSPARENT);
						v.setTextColor(Color.rgb(175, 250, 176));
						toast.show();
						req_contact();
			}
		}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
	}
	private Boolean verify_contact(String contact){
		if(contact.length() == 10){
			return true;
		}else{
			return false;
		}
	}
	
	private void confirm_add_this_person(){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		final int height = size.y;
		
		ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.ic_home);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Confirm");
		alert.setMessage("Do you want to add " + contact_info.contact_name);
		alert.setIcon(R.drawable.ic_action_add_person);
		// Set an EditText view to get user input 

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		add_this_person();
		  // add this person
			Toast toast= Toast.makeText(getApplicationContext(), 
					contact_info.contact_name + " has been added", Toast.LENGTH_SHORT);  
					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
					//v.setBackgroundColor(Color.TRANSPARENT);
					v.setTextColor(Color.rgb(175, 250, 176));
					toast.show();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  //just cancel it
		  }
		});

		alert.show();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		super.onActivityResult(requestCode, resultCode, data);
		 
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onactivityres: " + data.toString());
            uriContact = data.getData();
 
            retrieveContactName();
            retrieveContactNumber();
            confirm_add_this_person();
        }
		
	}
	
	/*
	 * (non-Javadoc)
	 * Contact functions
	 */
	 
    private void retrieveContactNumber() {
 
        String contactNumber = null;
 
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
 
        if (cursorID.moveToFirst()) {
 
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
 
        cursorID.close();
 
        Log.d(TAG, "Contact ID: " + contactID);
 
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
 
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
 
                new String[]{contactID},
                null);
 
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
 
        cursorPhone.close();
        contactNumber = contactNumber.replace('(',' ');
        contactNumber = contactNumber.replace(')',' ');
        contactNumber = contactNumber.replace('-',' ');
        contactNumber = contactNumber.replace(" ","");
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        contact_info.contact_number = contactNumber;
    }
 
    private void retrieveContactName() {
 
        String contactName = null;
 
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
 
        if (cursor.moveToFirst()) {
 
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
 
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
 
        cursor.close();
 
        Log.d(TAG, "Contact Name: " + contactName);
        contact_info.contact_name = contactName;
    }	
    
    
	public void AccessButton(View view) {
		
		if(commondata.event_information.eventID == null){//no event exist
			create_event_notfication();
		}else{//event exists
			ContactPicker();
		}
		
		
		// custom dialog
		/*
					final Dialog dialog = new Dialog(getBaseContext());
					dialog.setContentView(R.layout.custom_dialog);
					dialog.setTitle("Title...");
		 
					// set the custom dialog components - text, image and button
					TextView text = (TextView) dialog.findViewById(R.id.text);
					text.setText("Android custom dialog example!");
					ImageView image = (ImageView) dialog.findViewById(R.id.image);
					image.setImageResource(R.drawable.ic_launcher);
		 
					Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
					// if button is clicked, close the custom dialog
			
		 
					dialog.show();
				
				
			*/
	/*
		
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setIcon(R.drawable.addfriend);
			alert.setTitle("Notification");
			alert.setMessage("You cannot add more people to this tracer. Would you like to create a new tracer?");


			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  
				delete_event();
				Intent intent2 = new Intent(Login.this, Login.class);
				startActivity(intent2);
				finish();
				
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			  }
			});

			alert.show();
			
			*/
		
	}
	public void delete_event(){
		//if(event_info.is_exist != null){ // verify later when we add file
			StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
			strBuilder.append(commondata.facebook_details.facebook+"--"+commondata.facebook_details.name);
		    fb_event_ref.fbref = strBuilder.toString();
		    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
		    fb_event_ref.firebaseobj.removeValue();
		    
		    try {
		    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/resetevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
		  
		  
	
		//}
	}
	private void ContactPicker(){
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
	            Contacts.CONTENT_URI);
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	    //---
	    
	    AlertDialog.Builder alert = new AlertDialog.Builder(this);

	    alert.setView(getLayoutInflater().inflate(R.layout.custom_contact, null));


		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  
			ContactPicker();
		  }
		});

		alert.setNegativeButton("No I am done", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // 
			  initiate = "yes";
		  }
		});

		alert.show();
		//---------
	    
	    //---
	}
	
	private void add_this_person(){
		
		
			group.curr_person = contact_info.contact_number;
			Log.w("requesting",group.curr_person);
			add_a_member_to_fb(group.curr_person);
			set_up_map_view();
	}
	
	public void pick_food_type(){
		
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items = {" Chinese "," Coffee "," American ","Sea Food"," Pizza "," Asian ", " Japanese "," Mexican "," Italian ", " Indian", "Ice Cream"};
		            
		                // Creating and Building the Dialog 
		                AlertDialog.Builder builder = new AlertDialog.Builder(this);
		                builder.setTitle("Select food type");
		                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int item) {
		                   
		                    
		                    switch(item)
		                    {
		                        case 0:
		                                event_info.food_type = "chinese";
		                                event_info.is_food_chosen = "yes";
		                                 break;
		                        case 1:
		                                // Your code when 2nd  option seletced
		                        	event_info.food_type = "coffee";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 2:
		                               // Your code when 3rd option seletced
		                        	event_info.food_type = "american";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 3:
		                                 // Your code when 4th  option seletced  
		                        	event_info.food_type = "seafood";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 4:
	                                // Your code when first option seletced
		                        	event_info.food_type = "pizza";
		                        	event_info.is_food_chosen = "yes";
	                                 break;
		                        case 5:
		                        	event_info.food_type = "asian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 6:
		                        	event_info.food_type = "japanese";
		                        	event_info.is_food_chosen = "yes";
	                               // Your code when 3rd option seletced
	                                break;
		                        case 7:
		                        	event_info.food_type = "mexican";
		                        	event_info.is_food_chosen = "yes";
	                                 // Your code when 4th  option seletced            
	                                break;
		                        case 8:
		                        	event_info.food_type = "italian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when first option seletced
	                                 break;
		                        case 9:
		                        	event_info.food_type = "indian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 10:
		                        	event_info.food_type = "icecream";
		                        	event_info.is_food_chosen = "yes";
	                               // Your code when 3rd option seletced
	                                break;   
	                             default:
	                            	 event_info.food_type = "american";
	                            	 event_info.is_food_chosen = "no";
	                            	 break;
		                        
		                    }
		                    levelDialog.dismiss();
		                    setTitle(event_info.event_name+ "  " + event_info.food_type);
		                    Intent intent = new Intent(Login.this, Login.class);
		                	startActivity(intent);
		                	finish();
		                    }
		                });
		                levelDialog = builder.create();
		                levelDialog.show();
	}
	
	public void get_places_mean_loc(Double mean_latitude, Double mean_longitude){
		Firebase myFirebaseflag = new Firebase("https://met-ster-control.firebaseio.com/");
		myFirebaseflag.child("dataready").setValue("no");
		String req_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mean_latitude+","+mean_longitude+"&radius=5000&types=food&keyword="+event_info.food_type+"&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE";
		 try {
			ArrayList<Restaurant> run_places = new GetRestaurantAsyncTask().execute(req_url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//set_up_map_for_places();

		myFirebaseflag.child("dataready").addValueEventListener(new ValueEventListener() {

			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
			    System.out.println("get_places"+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
			    //set_up_map_for_places();
			    list_rest();
			  }

			  @Override public void onCancelled(FirebaseError error) { }

			});
		//Log.w("plcea",commondata.places_found.places.get(0));
		
	}
	
	public void list_rest(){
		//--------
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Best Matching places");

		ListView modeList = new ListView(this);
		ArrayList<String> stringArray = new ArrayList<String>();
		for(int i =0 ; i< commondata.places_found.places.size();i++)
		stringArray.add(commondata.places_found.places.get(i));
		//String[] stringArray = new String[] { "Bright Mode", "Normal Mode" };
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		dialog.show();

	}
	
	public void create_firebase_refrence(){
		StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
		strBuilder.append(commondata.event_information.eventID);
	    fb_event_ref.fbref = strBuilder.toString();
	    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
}

	public void find_places(View v){
		/*
		Double temp_lat = 0.0;
		Double temp_long = 0.0;
		for(int i = 0;i< latitudes.size();i++){
			temp_lat += latitudes.get(i);
		}
		for (int i = 0; i < longitudes.size(); i++){
			temp_long += longitudes.get(i);
		}
		temp_lat = temp_lat / latitudes.size();
		temp_long = temp_long / latitudes.size();
		latitudes.add(temp_lat);
		longitudes.add(temp_long);
		group_list.add("mean");// mean is the last point equivalent for group_list content
		Log.w("meanlat",Double.toString(temp_lat));
		Log.w("meanlon",Double.toString(temp_long));
		
		create_firebase_refrence();
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).setValue("user");
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).child("latitudes").setValue(temp_lat);
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).child("longitudes").setValue(temp_long);
		
		set_up_map_view();
		get_places_mean_loc(temp_lat, temp_long);
		*/
	}
	//-----------------
	//-----------------
	//-----------------
	
	private void drop_event(){
		if(commondata.event_information.eventID != null){
			try{
		locationManager.removeUpdates(locationListener);
			}catch(Exception e){
				
			}
		fb_event_ref.firebaseobj.removeEventListener(listn);
		fb_event_ref.firebaseobj.removeEventListener(child_listner);
		//fb_event_ref.firebaseobj.removeEventListener(listener);
		
		StringBuilder strBuildertmp = new StringBuilder("https://met-ster-event.firebaseio.com/");
		strBuildertmp.append(commondata.event_information.eventID+"/"+commondata.facebook_details.facebook+"--"+commondata.facebook_details.name);
	    String tempref = strBuildertmp.toString();
	    Firebase tempfb = new Firebase(tempref);
		tempfb.removeValue();
		commondata.event_information.eventID = null;
		 try {
	    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/resetevent.php",commondata.facebook_details.facebook,"event-"+commondata.facebook_details.facebook,"1","1","1","1","1"
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
		Intent intent = new Intent(Login.this, Login.class);
     	startActivity(intent);
     	finish();
		}
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

			return true;

		case R.id.refresh_icon:

			return true;
			
		case R.id.delete_icon:
			drop_event();
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