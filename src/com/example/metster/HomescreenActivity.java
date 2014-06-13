package com.example.metster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
                
        		//Intent intent = new Intent(this, Login.class);
        		//startActivity(intent);
        		Log.d("infile",ret);
        		
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
	           .setMessage("Are you sure you want to exit?")
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
		File file = new File("token.txt");
	    file.delete();
		file = new File("accounts.txt");
		file.delete();
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	/** Called when the user clicks the Login button */
	public void logintoaccount(View view) {
		
		//------------------------------------->
		
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
                
        		Intent intent = new Intent(this, Login.class);
        		startActivity(intent);
        		Log.d("infile",ret);
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
	
		
		//-------------------------------------->
		 
	}
	
		

}
