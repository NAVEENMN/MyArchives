package com.example.metster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class HomescreenActivity extends Activity {
	
	
	//-----------------
	
	//------------------
	String val;
	int userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);
		
        //-----------------------------------------------
		Firebase dataRef = new Firebase("https://metster.firebaseIO.com/totalusers/count");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                val =  (String) snapshot.getValue();
                userid = Integer.parseInt(val);
                Log.w("count",val);
            }

            @Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		//------------------------------------------------
		
	}
	
	/** Called when the user clicks the Sign Up button */
	public void Signupaccount(View view) {
		File file = new File("myfile.txt");
		boolean deleted = file.delete();
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	/** Called when the user clicks the Login button */
	public void logintoaccount(View view) {
		
		//------------------------------------->
		
		String ret = "";
        try {
            FileInputStream inputStream = openFileInput("myfile.txt");

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
