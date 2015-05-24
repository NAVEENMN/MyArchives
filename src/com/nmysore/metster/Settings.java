package com.nmysore.metster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TimePicker.OnTimeChangedListener;

import com.facebook.Session;
import com.facebook.SessionState;
import com.nmysore.metster.Login.fb_event_ref;

public class Settings extends Activity {
	SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
	}
	
	/*
	 * name : profile_settings
	 * params : View v
	 * @return : void
	 * @desp : This function pops up the dialog and saves the user settings
	 * 			in a shared preference.
	 */
	public void profile_settings(View view){
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_dialog,
				(ViewGroup) findViewById(R.id.new_event_root));
		AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this)
				.setView(layout);
		alert.create();
	
		
		TimePicker timePicker = (TimePicker) layout
				.findViewById(R.id.timePicker);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				commondata.prefrences.hour = hourOfDay;
				commondata.prefrences.minute = minute;
			}
		});
		
		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			
			// save data to shared pref	

			}

		});

		alert.show();
		
	}
		
	
	/*
	 * name : logout
	 * @params : View V
	 * @return : void
	 * @desp : This function clears up Facebook sessions and clears all flags and 
	 * 		    takes you back to home screen 
	 */
	public void logout(View view){
		Session.StatusCallback statusCallback = new SessionStatusCallback();
		System.out.println(commondata.facebook_details.fb.getSession().toString());
		Session.openActiveSession(this, true, statusCallback); 
		Intent settingsIntent = new Intent(Settings.this, HomescreenActivity.class);
		settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(settingsIntent);
    	finish();
		
	}
	
	
	/*
	 * name : deleteaccount
	 * @params : View v
	 * @return : void
	 * @desp : This function clears up Facebook sessions and clears all flags and 
	 * 		    takes you back to home screen. 
	 */
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
		/*
		 * name : SessionStatusCallback --> call
		 * @params : session, session_state, exception
		 * @return : void
		 * @desp : this function clears up all active session and clears all tokens.
		 * 			also it clears up shared prefrences.
		 */
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
