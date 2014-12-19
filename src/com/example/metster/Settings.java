package com.example.metster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;

public class Settings extends Activity {
	SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
	}
	
	
	
	public void logout(View view){
		Session.StatusCallback statusCallback = new SessionStatusCallback();
		System.out.println(commondata.facebook_details.fb.getSession().toString());
		Session.openActiveSession(this, true, statusCallback); 
		Intent settingsIntent = new Intent(Settings.this, HomescreenActivity.class);
		settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(settingsIntent);
    	finish();
		
	}
	
	public void	deleteaccount(View view){
		Session.StatusCallback statusCallback = new SessionStatusCallback();
		System.out.println(commondata.facebook_details.fb.getSession().toString());
		Session.openActiveSession(this, true, statusCallback); 
		Intent settingsIntent = new Intent(Settings.this, HomescreenActivity.class);
		settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(settingsIntent);
    	finish();
		
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
		Exception exception) {
		session.closeAndClearTokenInformation();
		session.close();
		Editor editor = commondata.prefrence.sp.edit();
		editor.clear();
		editor.commit();
		}
		}
    
	
}
