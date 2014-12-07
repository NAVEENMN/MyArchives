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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.metster.HomescreenActivity.RetrieveFeedTask;
import com.example.metster.HomescreenActivity.logininback;
import com.example.metster.util.SystemUiHider;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GCM_handle extends Activity {
	
	Facebook fb;
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
		
		/*
		 * first we need this users facebook id
		 * second fetch the location
		 * gcm will give event id data
		 * update location to fire base
		 */
		
		
	}
	
	//------------------------------------------------------------------
	
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
	  	    	 commondata.facebook_details.contact = response.toString();
	    		}
	  			} catch (InterruptedException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			} catch (ExecutionException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	    	
	    	
	    	//---- handle firebase here
	    	
	    	System.out.println("request was from" + commondata.gcm_req.requester_name);
			System.out.println("request for event" + commondata.gcm_req.event_id);
			System.out.println("vis me name is" + commondata.facebook_details.name );
			System.out.println("vis me name is" + commondata.facebook_details.facebook );
	    	//----
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
	 
	 public String BitMapToString(Bitmap bitmap){
	     ByteArrayOutputStream baos=new  ByteArrayOutputStream();
	     bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
	     byte [] b=baos.toByteArray();
	     String temp=Base64.encodeToString(b, Base64.DEFAULT);
	     return temp;
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
		        
		        URL img_value = new URL("https://graph.facebook.com/"+mUserId+"/picture?type=large");
		        final Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
		        
		        
		        commondata.facebook_details.facebook = mUserId;
		        commondata.facebook_details.name = mUserName;
		        commondata.facebook_details.email = mUserEmail;
		        commondata.facebook_details.profile_image = mIcon1;
		        System.out.println(Integer.toString(commondata.facebook_details.profile_image.getHeight()));
		        commondata.user_information.profileimage = BitMapToString(mIcon1);
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
