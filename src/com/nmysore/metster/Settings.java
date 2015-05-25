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
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.facebook.Session;
import com.facebook.SessionState;

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
		View layout = inflater.inflate(R.layout.profilesettings,
				(ViewGroup) findViewById(R.id.profile_settings_root));
		AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this)
				.setView(layout);
		alert.create();
	
		// dummy section need new ui
		
					 final SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("user_settings_prefrence", MODE_PRIVATE).edit();
					 
		
		// We have stars and bullet button
		RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.pricelevelpreference);
		ratingBar.setRating((float) 2.5);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				float new_price = (float) ratingBar.getRating();
				editor.putFloat("price_preference", new_price);

			}
		});
		final RadioGroup travelchoice = (RadioGroup) layout.findViewById(R.id.Travel_Choice_Preference);
		travelchoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (travelchoice.getCheckedRadioButtonId()) {
				case R.id.radio_car:
					editor.putFloat("travel_prefernce", (float) 5.0);
					break;

				case R.id.radio_public:
					editor.putFloat("travel_prefernce", (float) 4.0);
					break;

				case R.id.radio_bike:
					// do something
					editor.putFloat("travel_prefernce", (float) 3.0);
					break;

				case R.id.radio_walk:
					editor.putFloat("travel_prefernce", (float) 1.0);
					break;
				}
			}
		});
		
		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			
			// save data to shared pref	
				
				editor.commit();

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
