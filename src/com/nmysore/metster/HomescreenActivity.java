package com.nmysore.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
			//login_to_facebook(null);
			
			if (access_token != null && expires != 0) {
				commondata.facebook_details.fb.setAccessToken(access_token);
				commondata.facebook_details.fb.setAccessExpires(expires);
				commondata.facebook_details.fb
						.setSession(commondata.facebook_details.fb.getSession());
				//
				System.out.println("retrieve");
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
							System.exit(0);
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
		ProgressDialog progress = null;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progress = new ProgressDialog(HomescreenActivity.this);
			progress.setTitle("Loading your profile");
			progress.setMessage("please wait...");
			progress.show();	
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			System.out.println("leaves pre exe");
		}
		@Override
		protected void onPostExecute(Void result) {
			// create account
			super.onPostExecute(result);
			System.out.println("on post");
		
			progress.dismiss();
			Intent serviceIntent = new Intent(HomescreenActivity.this,
					LoadHome.class);
			HomescreenActivity.this.startService(serviceIntent);
			startActivity(serviceIntent);
		
			finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			System.out.println("do in background");
			
				getProfileInformation();
			    
				try {
				if (commondata.facebook_details.facebook != null) {
					
					Thread thread = new Thread() {
					    @Override
					    public void run() {
					    	System.out.println("before");
					    	response = 	postData("http://54.183.113.236/metster/setup_account.php", commondata.facebook_details.facebook,
									commondata.facebook_details.name,commondata.facebook_details.email );
					    	System.out.println("after");
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
					};

					thread.start();
					thread.join();
					

				}
				
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
		System.out.println("getprofilebefore");
		try {
			System.out.println("getprofile");
			JSONObject profile = Util.parseJson(commondata.facebook_details.fb
					.request("me"));
			JSONObject profilefriends = Util.parseJson(commondata.facebook_details.fb
					.request("me/friends"));
			
			Log.e("Profile", "" + profile);
			JSONArray jArray = profilefriends.getJSONArray("data");
			commondata.facebook_details.friends = profilefriends.getJSONArray("data");
            for(int i=0;i<jArray.length();i++){

                    JSONObject json_data = jArray.getJSONObject(i);
        
                    
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

	/*
	 * name : postData
	 * @params : String url, String parameter, String parameter, String parameter
	 * @return : String server_response
	 * @desp : This function makes a http post request and returns the server response.
	 */
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
	
	@Override
	public void onBackPressed() {
		HomescreenActivity.this.finish();
	}

	/*
	 * name : haveNetworkConnection
	 * @params : None
	 * @return :Boolean state
	 * @desp : This function checks network connection status.
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
