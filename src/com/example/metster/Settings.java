package com.example.metster;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
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
    	//commondata.facebook_details.fb.logout(getApplicationContext());
		//new LogoutOperation().execute();
    	//Intent intent = new Intent(Settings.this,HomescreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
	
	private class LogoutOperation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
        	if( commondata.facebook_details.fb.isSessionValid() ) {
        		  try {
        			  commondata.facebook_details.fb.getSession().closeAndClearTokenInformation();
        		    String str=commondata.facebook_details.fb.logout(getApplicationContext());
        		        } catch (MalformedURLException e)       
        		                 {                      
        		            e.printStackTrace();
        		         } catch (IOException e) {
        		           e.printStackTrace();
        		                }
        		                }
        	return null;
        }

        protected void onPostExecute(Void result) {
        	sp = getPreferences(MODE_PRIVATE);
        	Editor editor = sp.edit();
        	editor.putString("access_token", null);
			editor.putLong("access_expires", 0);
			editor.commit();
        	Intent intent = new Intent(Settings.this,HomescreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        	finish();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
	
}
