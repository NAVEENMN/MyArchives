package com.example.metster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;




public class HomescreenActivity extends Activity {
	
	String val;
	int userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);
		//---------------------------------------------------> if account set then login
		String ret = "";
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
                ret = stringBuilder.toString();
        		Intent intent = new Intent(this, LoadHome.class);
        		startActivity(intent);
        		finish();
        		
            }
            
            else{
            	Toast.makeText(this, "Login using your Metster account", Toast.LENGTH_SHORT)
                .show();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            Toast.makeText(this, "Welcome to Metster", Toast.LENGTH_SHORT)
            .show();
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            Toast.makeText(this, "Welcome to Metster", Toast.LENGTH_SHORT)
            .show();
        }
		//------------------------------------------------------------------------------
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
		
		File file = new File("token.txt");
	    file.delete();
		file = new File("accounts.txt");
		file.delete();
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
		}
		else{
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
				 Toast.makeText(getApplicationContext(), "Login ok", Toast.LENGTH_SHORT).show();
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
		//------------------------------------->
			
		EditText email = (EditText)  findViewById(R.id.Email) ;
		EditText password = (EditText)  findViewById(R.id.Password) ;
		final String Email = email.getText().toString();
		final String Password = password.getText().toString();
		Log.w("email",Email);
		Log.w("password",Password);
		
		if(Email.isEmpty()){
			Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
		}
		else{
			if(Password.isEmpty()){
				Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
			}
			else{
				loginprocess( Email, Password);
			}
		}
		
		String ret = "";
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
                ret = stringBuilder.toString();
                
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
