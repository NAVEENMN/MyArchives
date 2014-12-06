package com.example.metster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
		
        fb=new Facebook(APP_ID);
        
        if(fb.isSessionValid()){
        	System.out.println( "fb" +
        	fb.getAccessToken()
        	);}else{
        		System.out.println("no");
        		
				fb.authorize(this, new String[] {"email", "public_profile"}, new DialogListener(){

					@Override
					public void onComplete(Bundle values) {
						// TODO Auto-generated method stub
						System.out.println("yes bab" + fb.getAccessToken() );
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
        
        

        
       
        
        
		//---------------------------------------------------> if account set then login
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
                stringBuilder.toString();
                
                boolean stat = haveNetworkConnection();
                if(stat){
                	Intent intent = new Intent(this, LoadHome.class);
                	startActivity(intent);
                	finish();
                }else{
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
                
        		
            }
            
            else{
            	Toast.makeText(this, "Login using your Metster account", Toast.LENGTH_SHORT)
                .show();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            
        }
		//------------------------------------------------------------------------------
	}
	
	
	class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

	    private Exception exception;

	    protected void onPostExecute() {
	        // TODO: check this.exception 
	        // TODO: do something with the feed
	    }

		@Override
		protected Void doInBackground(String... params) {
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
	 
	 
	 public void getProfileInformation() {


		    try {

		        JSONObject profile = Util.parseJson(fb.request("me"));
		        Log.e("Profile", "" + profile);

		        final String mUserId = profile.getString("id");
		        final String mUserName = profile.getString("name");
		        final String mUserEmail = profile.getString("email");
		        
		        URL img_value = new URL("https://graph.facebook.com/"+mUserId+"/picture?type=large");
		        final Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
		       
		        //----
		        
		        
		        
		        
		        //----
		        runOnUiThread(new Runnable() {

		            public void run() {
		            	System.out.println(mIcon1.toString());
		                Log.e("FaceBook_Profile",""+mUserName+"\n"+"\n"+mUserEmail+"\n"+mUserEmail);

		                Toast.makeText(getApplicationContext(),
		                        "Name: " + mUserName + "\nEmail: " + mUserEmail,
		                        Toast.LENGTH_LONG).show();



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
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   HomescreenActivity.this.finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}
	
	/** Called when the user clicks the Sign Up button */
	public void Signupaccount(View view) {
		
		boolean stat = haveNetworkConnection();
		
		if(stat){
			File file = new File("token.txt");//delete files just before submit not here
			file.delete();
			file = new File("accounts.txt");
			file.delete();
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(), "Unable to connect to Internet.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
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
	
	
	public void loginprocess(String Email, String Password){
		Log.w("lgin","good to login");
		String server_response = "hello";
		String appkey = "n1a1v2e3e5n8m13y21s34o55r89e"; 
		
		 try {
		    	server_response = new RequestTask().execute("http://54.183.113.236/metster/loginprocess.php", appkey, Email, Password,"1","1", "1", "1"
						, "1", "1", "1", "1", "1", "1" ).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 Log.w("reponse",server_response);
		 
		 if(server_response.contentEquals("noemail")){
			 Toast.makeText(getApplicationContext(), "Please check your email and try again", Toast.LENGTH_SHORT).show();
		 }
		 else{
			 if(server_response.contentEquals("invalidpassword")){
				 Toast.makeText(getApplicationContext(), "Please check your password and try again", Toast.LENGTH_SHORT).show();
			 }
			 else{
				 String[] separated = server_response.split("#%-->");
				 String accountnumber = separated[0];
			     String token = separated[1];
			     String image = separated[2];
			     setuplogin(accountnumber, token,image);
			 }
		 }
		 
		 
	}
	
	
	public void setuplogin(String accountnumber, String token, String image){	
    	String filetoken = "token.txt";
    	String fileaccount = "accounts.txt";
    	String fileimage = "image.txt";
        FileOutputStream outputStream;
        //-------------------------------------- write token to file
        try {
          outputStream = openFileOutput(fileaccount, Context.MODE_PRIVATE);
          outputStream.write(accountnumber.getBytes());
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
       catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
            if( t instanceof ExecutionException ) {
                t = t.getCause();
            }
        }
      //-------------------------------------- write account to file 
        try {
          outputStream = openFileOutput(filetoken, Context.MODE_PRIVATE);
          outputStream.write(token.getBytes());
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
       catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
            if( t instanceof ExecutionException ) {
                t = t.getCause();
            }
        }
      //-------------------------------------- write image to file 
        try {
          outputStream = openFileOutput(fileimage, Context.MODE_PRIVATE);
          outputStream.write(image.getBytes());
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
       catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
            if( t instanceof ExecutionException ) {
                t = t.getCause();
            }
        }
	}
	
	
	/** Called when the user clicks the Login button */
	public void logintoaccount(View view) {
		
		//------------------------------------->
        boolean stat = haveNetworkConnection();
      		
		if(stat){
			EditText email = (EditText)  findViewById(R.id.Email) ;
			EditText password = (EditText)  findViewById(R.id.Password) ;
			final String Email = email.getText().toString();
			final String Password = password.getText().toString();
			Log.w("email",Email);
			Log.w("password",Password);
		
		if(Email.isEmpty()){
			Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
		}else{
			if(Password.isEmpty()){
				Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
			}else{
				loginprocess( Email, Password);
			}
		}
		
		
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
                stringBuilder.toString();
                
        		Intent intent = new Intent(this, LoadHome.class);
        		startActivity(intent);
        		finish();
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
	
		}
		else{
			Toast.makeText(getApplicationContext(), "Unable to connect to Internet.", Toast.LENGTH_SHORT).show();
		}
		
		//-------------------------------------->
		 
	}
	
		

}
