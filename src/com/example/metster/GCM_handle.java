package com.example.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metster.Rend.fb_event_ref;
import com.example.metster.util.SystemUiHider;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.firebase.client.Firebase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GCM_handle extends Activity {
	
	Facebook fb;
	Location postion_get;
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		setContentView(R.layout.activity_gcm_handle);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

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

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		//----------
		
		// Acquire a reference to the system Location Manager
				LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		      //--------------------------------------------------------   
			  // Define a listener that responds to location updates
			  //---------------------------------------------------------
				   LocationListener locationListener = new LocationListener() {
				    public void onLocationChanged(Location location) {	
				    	if(location != null){	
				    	commondata.user_information.latitude = location.getLatitude();
			        	commondata.user_information.longitude = location.getLongitude();
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
						//Intent intent = new Intent(Login.this, HomescreenActivity.class);
		            	//startActivity(intent);
		            	//finish();
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
		
		
		
		//----------
		String APP_ID = getString(R.string.facebook_app_id);
		setTitle("Invitation");
		
		fb = new Facebook(APP_ID);
		if(fb.isSessionValid()){
        	// if valid login
    		//Intent serviceIntent = new Intent(LoadHome.this, Login.class);
            //LoadHome.this.startService(serviceIntent);
            //startActivity(serviceIntent);
            //finish();
    		
    		System.out.println("session is valid");
    		
        }else{
        	System.out.println("no");
        	
        	new logingcminback().execute();
        	
        	}
		System.out.println("brequest was from" + commondata.gcm_req.requester_name);
		System.out.println("brequest for event" + commondata.gcm_req.event_id);
		System.out.println("bvis me name is" + commondata.facebook_details.name );
		System.out.println("bvis me name is" + commondata.facebook_details.facebook );
		/*
		 * first we need this users facebook id
		 * second fetch the location
		 * gcm will give event id data
		 * update location to fire base
		 */
		
		
	}
	
	
	public void handledata(View v){
		//---- handle firebase here
    	System.out.println("request was from" + commondata.gcm_req.requester_name);
		System.out.println("request for event" + commondata.gcm_req.event_id);
		System.out.println("vis me name is" + commondata.facebook_details.name );
		System.out.println("vis me name is" + commondata.facebook_details.facebook );
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		    	if(commondata.gcm_req.event_id != null){
		    		create_firebase_event_refrence(commondata.gcm_req.event_id);//firebase update
		    		try {
		    	    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/updateevent.php",commondata.facebook_details.facebook,commondata.gcm_req.event_id,"1","1","1","1","1"
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
		    		finish();
		    		} else{//event id null
		    			 Display display = getWindowManager().getDefaultDisplay();
		    				Point size = new Point();
		    				display.getSize(size);
		    				int width = size.x;
		    				final int height = size.y;
		    			Toast toast= Toast.makeText(getApplicationContext(), 
		   					 "Oops something went wrong, request your friend to add.", Toast.LENGTH_SHORT);  
		   					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
		   					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		   					//v.setBackgroundColor(Color.TRANSPARENT);
		   					v.setTextColor(Color.rgb(175, 250, 176));
		   					toast.show();
		    		}
		    }
		};

		thread.start();
		
	}
	//------------------------------------------------------------------
	/*
	 * this method creates the firebase refrence
	 */
	public void create_firebase_event_refrence(String eventid){
				StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
				strBuilder.append(commondata.gcm_req.event_id + "/");//eventid
				strBuilder.append(commondata.facebook_details.facebook);//for that event add me
			    fb_event_ref.fbref = strBuilder.toString();
			    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
			    fb_event_ref.firebaseobj.child("Latitude").setValue(commondata.user_information.latitude);
			    fb_event_ref.firebaseobj.child("Longitude").setValue(commondata.user_information.longitude);
	}
	
class logingcminback extends AsyncTask<Void, Void, Void>{
		
		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
        	fb.authorize(GCM_handle.this, new String[] {"email", "public_profile"}, new DialogListener(){

            	
					@Override
					public void onComplete(Bundle values) {
						new RetrievegcmFeedTask().execute();
					}

					@Override
					public void onFacebookError(FacebookError e) {
						// TODO Auto-generated method stub
						System.out.println("no bab");
					}

					@Override
					public void onError(DialogError e) {
						// TODO Auto-generated method stub
						System.out.println("y bab");
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						System.out.println("yer bab");
					}
        			
        		});
			
			return null;
		}
		
	}
	
	
	class RetrievegcmFeedTask extends AsyncTask<Void, Void, Void> {

	    private Exception exception;
	    private String response;

	    protected void onPostExecute(Void result) {
	        //create account
	    	super.onPostExecute(result);
	    	System.out.println("on post");
	    	try {
	    		if(commondata.facebook_details.facebook != null){
	  	    	 response = new RequestTask().execute("http://54.183.113.236/metster/setup_account.php",commondata.facebook_details.facebook,commondata.facebook_details.name,commondata.facebook_details.email,"1","1","1","1","1"
	  			, "1", "1", "1", "1", "1", "1").get();
	  	    	 System.out.println(response.toString());
	    		}
	  			} catch (InterruptedException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			} catch (ExecutionException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	    	
	    }

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
	        	getgcmProfileInformation();
	        } catch (Exception e) {
	            this.exception = e;
	        }
			return null;
		}
	}
	
	 @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         // TODO Auto-generated method stub
         super.onActivityResult(requestCode, resultCode, data);
         fb.authorizeCallback(requestCode, resultCode, data);
     }
	 
	 
	 /*
	  * This method will fetch the facebook details
	  */
	 
	 private void getgcmProfileInformation() {

		    try {

		        JSONObject profile = Util.parseJson(fb.request("me"));
		        Log.e("Profile", "" + profile);

		        final String mUserId = profile.getString("id");
		        final String mUserName = profile.getString("name");
		        final String mUserEmail = profile.getString("email");
		        
		        
		        commondata.facebook_details.facebook = mUserId;
		        commondata.facebook_details.name = mUserName;
		        commondata.facebook_details.email = mUserEmail;
		        
		        System.out.println(Integer.toString(commondata.facebook_details.profile_image.getHeight()));
		        
		       /*
		        runOnUiThread(new Runnable() {

		            public void run() {
		            	System.out.println(mIcon1.toString());
		                Log.e("FaceBook_Profile",""+mUserName+"\n"+"\n"+mUserEmail+"\n"+mUserEmail);

		                Toast.makeText(getApplicationContext(),
		                        "Name: " + mUserName + "\nEmail: " + mUserEmail,
		                        Toast.LENGTH_LONG).show();



		            }

		        });
		        */

		    } catch (FacebookError e) {

		        e.printStackTrace();
		    } catch (MalformedURLException e) {

		        e.printStackTrace();
		    } catch (JSONException e) {

		        e.printStackTrace();
		    } catch (IOException e) {

		        e.printStackTrace();
		    }

		}
	
	
	//-------------------------------------------------------------------

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
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
}
