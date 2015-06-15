package com.nmysore.metster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nmysore.metster.util.SystemUiHider;

public class LoadHome extends Activity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "613142100758";
    static final String TAG = "GCMDemo";
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	Bundle data = new Bundle();	
	//--- GCM
	TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    Location mCurrentLocation;
	//-------
	public static class gpslocation{

		static double latival;
		static double Longival;

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final LocationManager locationManager;
		final LocationListener locationListener;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_home);
		setupActionBar();
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		final View contentView = findViewById(R.id.fullscreen_content);
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.
		int mControlsHeight;
		int mShortAnimTime;
		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		public void onVisibilityChange(boolean visible) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				if (mControlsHeight == 0) {
					//mControlsHeight = controlsView.getHeight();
				}
				if (mShortAnimTime == 0) {
					mShortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
				}
			}
			if (visible && AUTO_HIDE) {
				// Schedule a hide().
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
		}
		});

		SharedPreferences prefs = getApplicationContext().getSharedPreferences("user_settings_prefrence", MODE_PRIVATE);
		
		
		
		
		// this is prefrence section
		if (prefs.toString()!= null) {
		  float price_preference = prefs.getFloat("price_preference", (float) 2.5);//"No name defined" is the default value.
		  float travel_prefernce = prefs.getFloat("travel_preference", (float) 5.0); //0 is the default value.
		  commondata.prefrences.price = price_preference;
		  commondata.prefrences.travel = (double) travel_prefernce;
		  System.out.println("price :"  + price_preference + "travel : " + travel_prefernce);
		}else{
			System.out.println("no settings found - making default settings");
			
			 SharedPreferences.Editor editor = prefs.edit();
			 editor.putFloat("price_preference", (float)2.5);
			 editor.putFloat("travel_preference", (float)5.0);
			 editor.commit();
		}
		
		
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

//----------------------------------------------------------  Setup GPS reader here!!
		super.onCreate(savedInstanceState);
		// Acquire a reference to the system Location Manager
			
		Firebase.setAndroidContext(this);
	
	        Context context = getApplicationContext();
	     
	        // Check device for Play Services APK.
	        if (checkPlayServices()) {
	        
	            gcm = GoogleCloudMessaging.getInstance(this);
	            
	            regid = getRegistrationId(context);
	            Log.w("regisid",regid);
	            
	            if (regid.equals("")) {
	        		Thread thread = new Thread() {
	    			    @Override
	    			    public void run() {
	    			    	 registerInBackground();
	    			    	}
	    			};

	    			thread.start();
	               
	            }else{
	            	
	            	try {
	        			
	        			Thread thread = new Thread() {
	        			    @Override
	        			    public void run() {
	        			    	
	        			    	JSONObject json_to_server = new JSONObject(); 
	        			    	try {
	        			    		json_to_server.put("id", commondata.facebook_details.facebook);
	        			    		json_to_server.put("username", commondata.facebook_details.name); 
	        			    		json_to_server.put("email", commondata.facebook_details.email);
	        			    		json_to_server.put("gcm",regid);
	        					} catch (JSONException e) {
	        						// TODO Auto-generated catch block
	        						e.printStackTrace();
	        					} 
	        			    	
	        			    	String response = 	postData("http://52.8.173.36/metster/setup_account.php", "update", json_to_server.toString() );
	        			    
	        					}
	        			};

	        			thread.start();
	        		
	        			} catch (Exception e) {
	        							// TODO Auto-generated catch block
	        				System.out.println("backhander");
	        				e.printStackTrace();
	        			} 
	        	}
	            		
	            	
	            
	        } 

			//--------------------------------------------------------   
		  // Define a listener that responds to location updates
		  //---------------------------------------------------------
	        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			   locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {	
			    	if(location !=null){
			    	commondata.user_information.latitude = location.getLatitude();
		        	commondata.user_information.longitude = location.getLongitude();
			    	}else{
			    		System.out.println("loc is null");
			    	}
			    	

			    }
				@SuppressWarnings("unused")
				public void onStatusChanged(Location location) {
					if(location != null){
					commondata.user_information.latitude = location.getLatitude();
		        	commondata.user_information.longitude = location.getLongitude();
					}else{
						System.out.println("loca is null");
					}
			    }

				@SuppressWarnings("unused")
				public void onProviderEnabled(Location location) {
					if(location != null){
					commondata.user_information.latitude = location.getLatitude();
		        	commondata.user_information.longitude = location.getLongitude();
					}else{
						System.out.println("locat is null");
					}
			    }
			    public void onProviderDisabled(String provider) {
			    	AlertDialog.Builder alert = new AlertDialog.Builder(LoadHome.this);
	    			alert.setTitle("Connection Error");
	    			alert.setMessage("Please check your network settings");
	    			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Intent intent = new Intent(LoadHome.this, HomescreenActivity.class);
	                	startActivity(intent);
	                	finish();
	    			  }
	    			});

	    			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
	    			  public void onClick(DialogInterface dialog, int whichButton) {
	    				  System.exit(0);
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
		    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
	        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        if (!isGPSEnabled && !isNetworkEnabled) {
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    			alert.setTitle("Connection Error");
    			alert.setMessage("Please check your network settings");
    			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent intent = new Intent(LoadHome.this, HomescreenActivity.class);
                	startActivity(intent);
                	finish();
    			  }
    			});

    			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int whichButton) {
    				  System.exit(0);
    				  finish();
    			  }
    			});
    			alert.show();
	        } else {
	        	if(isNetworkEnabled){
	        		
	        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	        		mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			        
			      if(mCurrentLocation!=null){
			        commondata.user_information.latitude = mCurrentLocation.getLatitude();
			        commondata.user_information.longitude = mCurrentLocation.getLongitude();
			      }else{
			    	  System.out.println("pos network is null");
			      }
	        	}else{
	        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	        		mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        	if(mCurrentLocation!=null){
		        	commondata.user_information.latitude = mCurrentLocation.getLatitude();
		        	commondata.user_information.longitude = mCurrentLocation.getLongitude();
		        	}else{
		        		System.out.println("pos gps is null");
		        	}
	        	}
	        	
	        }
	        
	        if(mCurrentLocation == null){ // we were not able to fetch location
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    			alert.setTitle("Connection Error");
    			alert.setMessage("Unable to fetch your location. Please turn your location service on.");
    			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent intent = new Intent(LoadHome.this, HomescreenActivity.class);
                	startActivity(intent);
                	finish();
    			  }
    			});

    			alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int whichButton) {
    				  System.exit(0);
    				  finish();
    			  }
    			});
    			alert.show();
	        }else{

		       //---------------------------------------------------------------------
	        final Handler handler = new Handler();
		    handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        	  locationManager.removeUpdates(locationListener);
		    }
		    }, 1000 * 60 * 1);//15mins
				
		        
		        Runnable mRunnable;
		        Handler mHandler = new Handler();
		        mRunnable = new Runnable() {
		            @Override
		            public void run() {
		            	locationManager.removeUpdates(locationListener);
		            	try{
		                Intent serviceIntent = new Intent(LoadHome.this, Login.class);
		                LoadHome.this.startService(serviceIntent);
		                startActivity(serviceIntent);
		                finish();
		            	}catch(Exception e){
		            		System.out.println("Error on load");		            	}
		            }
		        };
		        mHandler.postDelayed(mRunnable, 1000 * 2 );
	        }
		//-----------------------------------------------------------------------------------

		 
		        
	}//on create
	
	
	private String getRegistrationId(Context context) {
		try{
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
		}catch (Exception e){
			return "";
		}
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(LoadHome.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("error!!", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	    	protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	            	Log.w("inside","rinback");
	                if (gcm.equals("")) {
	                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
	                }
	                regid = gcm.register(SENDER_ID);
	                //gcm.unregister();
	                msg = "Device registered, registration ID=" + regid;
	                Log.i("GCM",  msg);
	                
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        
			protected void onPostExecute(String msg) {
	            //mDisplay.append(msg + "\n");
				sendRegistrationIdToBackend(regid);
				storeRegistrationId(getApplicationContext(), regid);
	            Log.w("registered",msg);
	        }

	    }.execute(null, null, null);
	   
	}
	
	
	private String postData(String url, String param1, String param2) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    HttpResponse response = null;
	    String responseString = null;
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("param1", param1));
	        nameValuePairs.add(new BasicNameValuePair("param2", param2));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        response = httpclient.execute(httppost);
	        response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
	        
	        System.out.println(responseString);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return responseString;
	}
	
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(String reg) {
		System.out.println("registering in backend");
	    // Your implementation here.
		final String regd = reg;
		
		try {
			
			Thread thread = new Thread() {
			    @Override
			    public void run() {
			    	System.out.println("updating gcm");
			    	
			    	JSONObject json_to_server = new JSONObject(); 
			    	try {
			    		json_to_server.put("id", commondata.facebook_details.facebook);
			    		json_to_server.put("username", commondata.facebook_details.name); 
			    		json_to_server.put("email", commondata.facebook_details.email);
			    		json_to_server.put("gcm",regd);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			    	
			    	String response = 	postData("http://52.8.173.36/metster/setup_account.php", "update", json_to_server.toString() );
					}
			};

			thread.start();
			
			} catch (Exception e) {
							// TODO Auto-generated catch block
				System.out.println("backhander");
				e.printStackTrace();
			} 
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
}