package com.example.metster;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.metster.Login.Map;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Rend extends Activity {
	private static final int CONTACT_PICKER_RESULT = 1001;
	ArrayList<String> group_list = new ArrayList<String>();
	public static class group{
		static String curr_person;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rend);
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Map.latival, Map.Longival)) // visitor
                .title("me")).showInfoWindow();
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 11));
        mMap.getUiSettings().setZoomControlsEnabled(false);   
	}
	public void doLaunchContactPicker(View view) {
	    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
	            Contacts.CONTENT_URI);
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}
	public void add_this_person(View view){
		EditText emailEntry = (EditText) findViewById(R.id.invite_email);
        group.curr_person = emailEntry.getText().toString();
        Log.w("requesting",group.curr_person);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        switch (requestCode) {
	        case CONTACT_PICKER_RESULT:
	            Cursor cursor = null;
	            String email = "";
	            try {
	                Uri result = data.getData();
	                Log.v("res", "Got a contact result: "
	                        + result.toString());

	                // get the contact id from the Uri
	                String id = result.getLastPathSegment();

	                // query for everything email
	                cursor = getContentResolver().query(Email.CONTENT_URI,
	                        null, Email.CONTACT_ID + "=?", new String[] { id },
	                        null);

	                int emailIdx = cursor.getColumnIndex(Email.DATA);

	                // let's just get the first email
	                if (cursor.moveToFirst()) {
	                    email = cursor.getString(emailIdx);
	                    Log.v("em", "Got email: " + email);
	                } else {
	                    Log.w("em", "No results");
	                }
	            } catch (Exception e) {
	                Log.e("em", "Failed to get email data", e);
	            } finally {
	                if (cursor != null) {
	                    cursor.close();
	                }
	                EditText emailEntry = (EditText) findViewById(R.id.invite_email);
	                emailEntry.setText(email);
	                group_list.add(email);
	                group.curr_person = email;
	                Log.w("email list", group_list.get(0));
	                if (email.length() == 0) {
	                    Toast.makeText(this, "No email found for contact.",
	                            Toast.LENGTH_LONG).show();
	                }

	            }

	            break;
	        }

	    } else {
	        Log.w("em", "Warning: activity result not ok");
	    }
	}
}
