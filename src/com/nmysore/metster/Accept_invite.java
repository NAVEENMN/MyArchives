package com.nmysore.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.nmysore.metster.Login.fb_event_ref;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.firebase.client.Firebase;
import com.nmysore.metster.R;

public class Accept_invite extends Activity {
	Facebook fb;
	Location postion_get;
	SharedPreferences sp;
	AlertDialog levelDialog = null;
	RadioGroup travelchoice;
	Boolean invite_status ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accept_invite);
		Firebase.setAndroidContext(this);
		sp = getPreferences(MODE_PRIVATE);// save share preferences to private
		commondata.prefrence.sp = sp;// keep a copy to logout
		String access_token = sp.getString("access_token", null);
		long expires = sp.getLong("access_expires", 0);
		String APP_ID = getString(R.string.facebook_app_id);
		fb = new Facebook(APP_ID);
		invite_status = false;
		setTitle("Invitation");
		// ----------
		/*
		 * fetch all data from the the dialog
		 */
		RatingBar ratingBar = (RatingBar) findViewById(R.id.pricelevel);
		ratingBar.setRating((float) 2.5);
		commondata.prefrences.price = (float) 2.5;
		commondata.prefrences.travel = (Double) 5.0;
		commondata.prefrences.hour = 0;
		commondata.prefrences.minute = 0;
		commondata.prefrences.food = "american";
		

		
		// pull all the data from the shared pref 
		
		TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				commondata.prefrences.hour = hourOfDay;
				commondata.prefrences.minute = minute;
			}
		});

		
		// ----------
		boolean stat = haveNetworkConnection();
		if (stat) {// network check ok
			LocationManager locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			// --------------------------------------------------------
			// Define a listener that responds to location updates
			// ---------------------------------------------------------
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					if (location != null) {
						commondata.user_information.latitude = location
								.getLatitude();
						commondata.user_information.longitude = location
								.getLongitude();
					} else {
						System.out.println("loca null");
					}

				}

				@SuppressWarnings("unused")
				public void onStatusChanged(Location location) {
					if (location != null) {
						commondata.user_information.latitude = location
								.getLatitude();
						commondata.user_information.longitude = location
								.getLongitude();
					} else {
						System.out.println("loc null");
					}

				}

				@SuppressWarnings("unused")
				public void onProviderEnabled(Location location) {
					if (location != null) {
						commondata.user_information.latitude = location
								.getLatitude();
						commondata.user_information.longitude = location
								.getLongitude();
					} else {
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
			// ------------------------------------------------------------------------
			// getting GPS and network status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSEnabled && !isNetworkEnabled) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Connection Error");
				alert.setMessage("Please check your network settings");
				alert.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								 Intent intent = new Intent(Accept_invite.this,
								 HomescreenActivity.class);
								 startActivity(intent);
								 finish();
							}
						});

				alert.setNegativeButton("Exit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
							}
						});
				alert.show();
			} else {

				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 0, 0,
							locationListener);
					postion_get = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (postion_get != null) {
						commondata.user_information.latitude = postion_get
								.getLatitude();
						commondata.user_information.longitude = postion_get
								.getLongitude();
						System.out.println("loc from network ");
					} else {
						System.out.println("loc from network error");
					}

				} else {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 0, 0,
							locationListener);
					postion_get = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (postion_get != null) {
						commondata.user_information.latitude = postion_get
								.getLatitude();
						commondata.user_information.longitude = postion_get
								.getLongitude();
						System.out.println("loc from gps ");
					} else {

						System.out.println("loc from gps error");
					}
				}

			}

			// ----------------------

			if (access_token != null && expires != 0) {// login directl
				/*
				 * facebook login
				 */
				commondata.facebook_details.fb.setAccessToken(access_token);
				commondata.facebook_details.fb.setAccessExpires(expires);
				commondata.facebook_details.fb
						.setSession(commondata.facebook_details.fb.getSession());
				System.out.println("brequest was from"
						+ commondata.gcm_req.requester_name);
				System.out.println("brequest for event"
						+ commondata.gcm_req.event_id);
				System.out.println("bvis me name is"
						+ commondata.facebook_details.name);
				System.out.println("bvis me name is"
						+ commondata.facebook_details.facebook);
				pick_food_type();
				new RetrievegcmFeedTask().execute();

			} else {// manually log him in
				new logingcminback().execute();
			}

		} else {// no active network connection

		}

	}

	/*
	 * This method puts data to firebase and mysql
	 */
	public void handledata() {
		// ---- handle firebase here
		System.out.println("request was from"
				+ commondata.gcm_req.requester_name);
		System.out.println("request for event" + commondata.gcm_req.event_id);
		System.out.println("vis me name is" + commondata.facebook_details.name);
		System.out.println("vis me name is"
				+ commondata.facebook_details.facebook);
		
		
		
		
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				if (commondata.gcm_req.event_id != null) {
					create_firebase_event_refrence(commondata.gcm_req.event_id);// firebase
																				// location

					try {
						String server_resp = 	postData("http://54.183.113.236/metster/updateevent.php", commondata.facebook_details.facebook,
								commondata.gcm_req.event_id,"1" );
						
						System.out.println("backhand" + server_resp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("backhander");
						e.printStackTrace();
					} 

				} else {// event id null
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					final int height = size.y;
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									"Oops something went wrong, request your friend to add.",
									Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
							0, height / 4);
					TextView v = (TextView) toast.getView().findViewById(
							android.R.id.message);
					// v.setBackgroundColor(Color.TRANSPARENT);
					v.setTextColor(Color.rgb(175, 250, 176));
					toast.show();
				}
			}
		};

		thread.start();

	}

	// ------------------------------------------------------------------
	/*
	 * this method creates the firebase refrence
	 */
	public void create_firebase_event_refrence(String eventid) {
		StringBuilder strBuilder = new StringBuilder(
				"https://met-ster-event.firebaseio.com/");
		strBuilder.append(commondata.gcm_req.event_id + "/");// eventid
		strBuilder.append(commondata.facebook_details.facebook + "--"
				+ commondata.facebook_details.name);// for that event add me
		fb_event_ref.fbref = strBuilder.toString();
		fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
		fb_event_ref.firebaseobj.child("Latitude").setValue(
				commondata.user_information.latitude);
		fb_event_ref.firebaseobj.child("Longitude").setValue(
				commondata.user_information.longitude);
	}

	class logingcminback extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			fb.authorize(Accept_invite.this, new String[] { "email",
					"public_profile" }, new DialogListener() {

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
		String[] id;
		protected void onPostExecute(Void result) {
			// create account
			super.onPostExecute(result);
			System.out.println("on post");
			
			
			Thread thread = new Thread() {
			    @Override
			    public void run() {
			    	System.out.println("before");
			    	response = 	postData("http://54.183.113.236/metster/setup_account.php", commondata.facebook_details.facebook,
							commondata.facebook_details.name,commondata.facebook_details.email );
			    	System.out.println("after");
					System.out.println(response.toString());
					id = response.split("-->");
					}
			};

			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.out.println("has" + id[2]);
			if(id[2].isEmpty()){
			handledata();
			invite_status = true;
			}else{
				//----------- display you are already in an event
				AlertDialog.Builder alert = new AlertDialog.Builder(Accept_invite.this);
				alert.setTitle("You are already in an event");
				alert.setMessage("Please login back to Metster to drop that event");
				alert.setCancelable(false);

				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						finish();
					}
				});
				alert.show();
			invite_status = false;	
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				getgcmProfileInformation();
				pick_food_type();
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

			System.out.println(Integer
					.toString(commondata.facebook_details.profile_image
							.getHeight()));

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

	/*
	 * This method will check if network connection exists Returns boolean
	 */
	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	// -------------------------------------------------------------------

	public void fetch_prefrence(View v) {
		/*
		 * add the host to firebase met-ster-event
		 */
		System.out.println("submit");
		fb_event_ref.firebaseobj.child("price").setValue(
				commondata.prefrences.price);
		fb_event_ref.firebaseobj.child("travel").setValue(
				commondata.prefrences.travel);
		fb_event_ref.firebaseobj.child("hour").setValue(
				commondata.prefrences.hour);
		fb_event_ref.firebaseobj.child("minute").setValue(
				commondata.prefrences.minute);
		fb_event_ref.firebaseobj.child("food").setValue(
				commondata.prefrences.food);
		/*
		 * add preferences to firbase
		 */
		ale();
	}

	public void ale() {
		if(invite_status){
		AlertDialog.Builder alert = new AlertDialog.Builder(Accept_invite.this);
		alert.setTitle("You have accepted the invite");
		alert.setMessage("Please login back to Metster to view this event");
		alert.setCancelable(false);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				finish();
			}
		});
		alert.show();
		}else{
			AlertDialog.Builder alert = new AlertDialog.Builder(Accept_invite.this);
			alert.setTitle("Invitation Rejected");
			alert.setMessage("You are already in an event, please login back to drop it.");
			alert.setCancelable(false);

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					finish();
				}
			});
			alert.show();
		}
	}
	
	private String postData(String url, String param1, String param2, String param3) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    HttpResponse response = null;
	    String responseString = null;
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("appkey", param1));
	        nameValuePairs.add(new BasicNameValuePair("param2", param2));
	        nameValuePairs.add(new BasicNameValuePair("param3", param3));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
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

	public void pick_food_type() {

		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items = { " Chinese ", " Coffee ", " American ",
				"Sea Food", " Pizza ", " Asian ", " Japanese ", " Mexican ",
				" Italian ", " Indian", "Ice Cream" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select food type");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							commondata.prefrences.food = "chinese";

							break;
						case 1:
							// Your code when 2nd option seletced
							commondata.prefrences.food = "coffee";

							break;
						case 2:
							// Your code when 3rd option seletced
							commondata.prefrences.food = "american";

							break;
						case 3:
							// Your code when 4th option seletced
							commondata.prefrences.food = "seafood";

							break;
						case 4:
							// Your code when first option seletced
							commondata.prefrences.food = "pizza";

							break;
						case 5:
							commondata.prefrences.food = "asian";

							// Your code when 2nd option seletced
							break;
						case 6:
							commondata.prefrences.food = "japanese";

							// Your code when 3rd option seletced
							break;
						case 7:
							commondata.prefrences.food = "mexican";

							// Your code when 4th option seletced
							break;
						case 8:
							commondata.prefrences.food = "italian";

							// Your code when first option seletced
							break;
						case 9:
							commondata.prefrences.food = "indian";

							// Your code when 2nd option seletced
							break;
						case 10:
							commondata.prefrences.food = "icecream";

							// Your code when 3rd option seletced
							break;
						default:
							commondata.prefrences.food = "american";

							break;

						}
						levelDialog.dismiss();
					}
				});
		levelDialog = builder.create();
		levelDialog.show();
	}
}
