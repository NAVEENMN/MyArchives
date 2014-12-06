package com.example.metster;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	public void logout(View view){
		
		new AlertDialog.Builder(this)
        .setMessage("Do you wish to Logout?")
        .setTitle("Metster")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Intent intent2 = new Intent( Settings.this, HomescreenActivity.class);
        		File dir = getFilesDir();
        		File file = new File(dir, "accounts.txt");
        		boolean deleted = file.delete();
        		File dir2 = getFilesDir();
        		File file2 = new File(dir2, "metster_event_info.txt");
        		file2.delete();
        		startActivity(intent2);
        		finish();
            }
        })
        .setNegativeButton("No", null)
        .show();
		
	}
	
	@Override
	public void onBackPressed() {
	 
		Intent intent2 = new Intent( Settings.this, Login.class);
		startActivity(intent2);
		finish();
			
	}
	
}
