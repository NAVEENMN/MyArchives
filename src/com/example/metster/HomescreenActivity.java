package com.example.metster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
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
	Facebook fb;
	SharedPreferences sp;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);
		String APP_ID = getString(R.string.facebook_app_id);
		Firebase.setAndroidContext(getApplicationContext());
		
		//-------------------------------
		
		boolean stat = haveNetworkConnection();
        if(stat){// network check ok
        	fb=new Facebook(APP_ID);
        	if(fb.isSessionValid()){
            	// if valid login
        		//Intent serviceIntent = new Intent(LoadHome.this, Login.class);
                //LoadHome.this.startService(serviceIntent);
                //startActivity(serviceIntent);
                //finish();
            }else{
            	System.out.println("no");
            	fb.authorize(this, new String[] {"email", "public_profile"}, new DialogListener(){

    					@Override
    					public void onComplete(Bundle values) {
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
        	
        	
        }else{// network error
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Connection Error");
			alert.setMessage("Please check your network settings");

			alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(HomescreenActivity.this, HomescreenActivity.class);
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
		
		
		//-------------------------------
	}
	
	
	class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

	    private Exception exception;
	    private String response;

	    protected void onPostExecute(Void result) {
	        //create account
	    	super.onPostExecute(result);
	    	System.out.println("on post");
	    	try {
	  	    	 response = new RequestTask().execute("http://54.183.113.236/metster/setup_account.php",commondata.facebook_details.facebook,commondata.facebook_details.name,commondata.facebook_details.email,"1","1","1","1","1"
	  			, "1", "1", "1", "1", "1", "1").get();
	  	    	 System.out.println(response.toString());
	  			} catch (InterruptedException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			} catch (ExecutionException e) {
	  							// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	    	
	    	Intent serviceIntent = new Intent(HomescreenActivity.this, LoadHome.class);
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
	 
	 private void getProfileInformation() {

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
	
	 
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage("Do you want to exit?")
	           .setTitle("Metster")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   
	                   HomescreenActivity.this.finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}
	
	
	
	
	/*
	 * This method will check if network connection exists
	 * Returns boolean 
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
