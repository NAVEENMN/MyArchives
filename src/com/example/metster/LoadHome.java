package com.example.metster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.metster.util.SystemUiHider;

public class LoadHome extends Activity {
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	Bundle data = new Bundle();	

	public static class gpslocation{

		static double latival;
		static double Longival;

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.w("befofe","fg");
		setTitle("Meet, Connect and Socialize");
		final LocationManager locationManager;
		final LocationListener locationListener;
		Location location = null;
		Location pos = null ;
		String provider;

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_load_home);
		setupActionBar();

		//final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
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
								//mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							//controlsView
							//		.animate()
							//		.translationY(visible ? 0 : mControlsHeight)
							//		.setDuration(mShortAnimTime);
						} //else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							//controlsView.setVisibility(visible ? View.VISIBLE
							//		: View.GONE);
						//}

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
		//findViewById(R.id.dummy_button).setOnTouchListener(
		//		mDelayHideTouchListener);

//----------------------------------------------------------  Setup GPS reader here!!

		
		//--------------------------------> Setup location
		super.onCreate(savedInstanceState);
		// Acquire a reference to the system Location Manager
			locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		   locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {	
		    	gpslocation.latival = location.getLatitude();
	        	gpslocation.Longival = location.getLongitude();

		    }

			@SuppressWarnings("unused")
			public void onStatusChanged(Location location) {
		    	gpslocation.latival = location.getLatitude();
	        	gpslocation.Longival = location.getLongitude();
		    }

			@SuppressWarnings("unused")
			public void onProviderEnabled(Location location) {
		    	gpslocation.latival = location.getLatitude();
	        	gpslocation.Longival = location.getLongitude();
		    }
		    public void onProviderDisabled(String provider) {}

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
		// Register the listener with the Location Manager to receive location updates
		  		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        Criteria criteria = new Criteria();
		        pos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        if( pos == null ) 
		        	{
		        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        	pos = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        	provider = locationManager.getBestProvider(criteria, false);
		        	location = locationManager.getLastKnownLocation(provider);
		        	gpslocation.latival = location.getLatitude();
		        	gpslocation.Longival = location.getLongitude();
		        	}
		        else{
		        	gpslocation.latival = pos.getLatitude();
		        	gpslocation.Longival = pos.getLongitude();
		        	}

		       //---------------------------------------------------------------------

		        final Handler handler = new Handler();
		        handler.postDelayed(new Runnable() {
		          @Override
		          public void run() {
		        	  locationManager.removeUpdates(locationListener);
		          }
		        }, 1000 * 60 * 15);//15mins

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
		                data.putString("accountnumber", stringBuilder.toString());
		                commondata.user_information.account_number = stringBuilder.toString();
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
				                data.putString("tokennumber", stringBuilder.toString());
				                commondata.user_information.token_number = stringBuilder.toString();
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
						                data.putString("userimage", stringBuilder.toString());
						                commondata.user_information.profileimage = stringBuilder.toString();
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
		      //-----------------------------------------------------------------------



		        Runnable mRunnable;
		        Handler mHandler = new Handler();
		        mRunnable = new Runnable() {
		            @Override
		            public void run() {
		            	locationManager.removeUpdates(locationListener);
		                Intent serviceIntent = new Intent(LoadHome.this, Login.class);
		                serviceIntent.putExtras(data);
		                //LoadHome.this.startService(serviceIntent);
		                startActivity(serviceIntent);
		                finish();
		            }
		        };
		        mHandler.postDelayed(mRunnable, 1000 * 4 );

		//-----------------------------------------------------------------------------------

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
}