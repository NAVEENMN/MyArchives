package com.example.metster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.Session;

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
            	
            	Session session = Session.getActiveSession();
            	if (session != null) {

                    if (!session.isClosed()) {
                        session.closeAndClearTokenInformation();
                        //clear your preferences if saved
                    }
                } else {

                    session = new Session(getBaseContext());
                    Session.setActiveSession(session);

                    session.closeAndClearTokenInformation();
                        //clear your preferences if saved
                }
            	
            	Intent intent = new Intent(Settings.this,HomescreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            	finish();
            	System.exit(2);
            }
        })
        .setNegativeButton("No", null)
        .show();
		
	}

	
}
