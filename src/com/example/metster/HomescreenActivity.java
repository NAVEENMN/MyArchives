package com.example.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.firebase.client.Firebase;

/*
 * 	This class opens up when the App starts
 * 	It first checks for the accounts.txt if it exists then check for network if OK then 
 *  go to LoadHome.class
 *  if not it will display the UI to either signup or login and the methods will take are of it. 
 */

public class HomescreenActivity extends Activity {

	String val;
	int userid;
	SharedPreferences sp;
	private ViewFlipper Homeimage;
	private float initialX;
	Dialog dialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);
		Homeimage = (ViewFlipper) findViewById(R.id.view_flipper);
		// -- Homeimage animtion

		Homeimage.setInAnimation(this, R.anim.in_right);
		Homeimage.setOutAnimation(this, R.anim.out_left);
		Homeimage.setAutoStart(true);
		// ---------------------

		ActionBar actionBar = getActionBar();
		actionBar.hide();
		String APP_ID = getString(R.string.facebook_app_id);
		commondata.facebook_details.fb = new Facebook(APP_ID);
		Firebase.setAndroidContext(getApplicationContext());
		commondata.event_information.eventID = null;
		sp = getPreferences(MODE_PRIVATE);// save share preferences to private
		commondata.prefrence.sp = sp;// keep a copy to logout
		String access_token = sp.getString("access_token", null);// look for
																	// string
																	// access_token
																	// if you
																	// dont find
																	// it set it
																	// to null
		long expires = sp.getLong("access_expires", 0);
		// -------------------------------

		boolean stat = haveNetworkConnection();
		if (stat) {// network check ok

			if (access_token != null && expires != 0) {
				commondata.facebook_details.fb.setAccessToken(access_token);
				commondata.facebook_details.fb.setAccessExpires(expires);
				commondata.facebook_details.fb
						.setSession(commondata.facebook_details.fb.getSession());
				//
				new RetrieveFeedTask().execute();
			}

		} else {// network error
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Connection Error");
			alert.setMessage("Please check your network settings");

			alert.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Intent intent = new Intent(HomescreenActivity.this,
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
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initialX = touchevent.getX();
			break;
		case MotionEvent.ACTION_UP:
			float finalX = touchevent.getX();
			if (initialX > finalX) {
				if (Homeimage.getDisplayedChild() == 1)
					break;

				
				Homeimage.setInAnimation(HomescreenActivity.this, R.anim.in_right);
				Homeimage.setOutAnimation(this, R.anim.out_left);
				 

				Homeimage.showNext();
			} else {
				if (Homeimage.getDisplayedChild() == 0)
					break;

				
				Homeimage.setInAnimation(this, R.anim.in_left);
				Homeimage.setOutAnimation(this, R.anim.out_right);
				 

				Homeimage.showPrevious();
			}
			break;
		}
		return false;
	}

	class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

		private Exception exception;
		private String response;
		private ProgressDialog progress = new ProgressDialog(HomescreenActivity.this);
		
		protected void onPreExecute(){
			this.progress.setTitle("Loading your profile");
			this.progress.setMessage("please wait...");
			this.progress.show();	
			this.progress.setCancelable(false);
			this.progress.setCanceledOnTouchOutside(false);
		}

		protected void onPostExecute(Void result) {
			// create account
			super.onPostExecute(result);
			System.out.println("on post");
			try {
				if (commondata.facebook_details.facebook != null) {
					response = new RequestTask().execute(
							"http://54.183.113.236/metster/setup_account.php",
							commondata.facebook_details.facebook,
							commondata.facebook_details.name,
							commondata.facebook_details.email, "1", "1", "1",
							"1", "1", "1", "1", "1", "1", "1", "1").get();
					System.out.println(response.toString());
					String[] dat = response.toString().split("-->");
					commondata.facebook_details.contact = dat[1];// contact
					if (dat[2].isEmpty() || dat[2] == null) {
						commondata.event_information.eventID = null;
					} else {
						commondata.event_information.eventID = dat[2];// eventid
					}
					System.out.println("contact"
							+ commondata.facebook_details.contact);
					System.out.println("eventid"
							+ commondata.event_information.eventID);

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.progress.dismiss();
			Intent serviceIntent = new Intent(HomescreenActivity.this,
					LoadHome.class);
			HomescreenActivity.this.startService(serviceIntent);
			startActivity(serviceIntent);
		
			finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				getProfileInformation();
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
		commondata.facebook_details.fb.authorizeCallback(requestCode,
				resultCode, data);
	}

	public String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	/*
	 * login to facebook method
	 */
	@SuppressWarnings("deprecation")
	public void login_to_facebook(View v) {
		commondata.facebook_details.fb.authorize(HomescreenActivity.this,
				new String[] { "email", "public_profile","user_friends" },
				new DialogListener() {

					@SuppressLint("CommitPrefEdits")
					@Override
					public void onComplete(Bundle values) {
						Editor editor = sp.edit();
						editor.putString("access_token",
								commondata.facebook_details.fb.getAccessToken());
						editor.putLong("access_expires",
								commondata.facebook_details.fb
										.getAccessExpires());
						editor.commit();
						new RetrieveFeedTask().execute();
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
	}

	/*
	 * This method will fetch the facebook details
	 */

	@SuppressWarnings("deprecation")
	private void getProfileInformation() {

		try {

			JSONObject profile = Util.parseJson(commondata.facebook_details.fb
					.request("me"));
			JSONObject profilefriends = Util.parseJson(commondata.facebook_details.fb
					.request("me/friends"));
			
			Log.e("Profile", "" + profile);
			JSONArray jArray = profilefriends.getJSONArray("data");
			commondata.facebook_details.friends = profilefriends.getJSONArray("data");
            for(int i=0;i<jArray.length();i++){

                    JSONObject json_data = jArray.getJSONObject(i);
                    System.out.println(json_data.get("name").toString());
                    
                    Log.v("THIS IS DATA", i+" : "+jArray.getJSONObject(i));
             }
			final String mUserId = profile.getString("id");
			final String mUserName = profile.getString("name");
			final String mUserEmail = profile.getString("email");

			URL img_value = new URL("https://graph.facebook.com/" + mUserId
					+ "/picture?type=large");
			final Bitmap mIcon1 = BitmapFactory.decodeStream(img_value
					.openConnection().getInputStream());

			commondata.facebook_details.facebook = mUserId;
			commondata.facebook_details.name = mUserName;
			commondata.facebook_details.email = mUserEmail;
			commondata.facebook_details.profile_image = mIcon1;
			System.out.println(Integer
					.toString(commondata.facebook_details.profile_image
							.getHeight()));
			commondata.user_information.profileimage = BitMapToString(mIcon1);
			
			Request.executeMyFriendsRequestAsync(commondata.facebook_details.fb.getSession(),
	                new GraphUserListCallback() {

	                    @Override
	                    public void onCompleted(List<GraphUser> users,
	                            Response response) {
	                        Log.i("Response JSON", response.toString());
	                        /*
	                        names = new String[users.size()];
	                        id = new String[users.size()];
	                        for (int i=0; i<users.size();i++){
	                            names[i] = users.get(i).getName();
	                            id[i]= users.get(i).getId();                                
	                        }  
	                        */
	                    }
	                });
			

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

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setMessage("Do you want to exit?")
				.setTitle("Metster")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								HomescreenActivity.this.finish();
							}
						}).setNegativeButton("No", null).show();
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

}
