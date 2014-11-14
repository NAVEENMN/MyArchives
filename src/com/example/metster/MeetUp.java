package com.example.metster;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetUp extends Activity {
    
	private static final int CONTACT_PICKER_RESULT = 1001;
	String server_response;
	ArrayList<String> group_list = new ArrayList<String>();
	
	public static class group{
		static String curr_person;
	}
	
	public static class event_info{
		static String event_name;
	}
	
	public static class fb_event_ref{
		static String fbref;
		static Firebase firebaseobj;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_up);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//-------------------------------------------------------   Copy from rend
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rend);
		//----> set up maps
		set_up_map_view();
		//----> call event function
        create_event_notfication();
        //----> set up fire base refrence
        create_firebase_refrence();// this is base refrence
		
		
		
		//------------------------------------------------------------------------
		
		
		
		
	}
	
	//-------------------------------------------------------   Copy from rend func
	public void create_firebase_refrence(){
		//----------------------> Fire base reference creation
				StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
				strBuilder.append(commondata.user_information.account_number);
			    fb_event_ref.fbref = strBuilder.toString();
			    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
			    fb_event_ref.firebaseobj.child("EventName").setValue(commondata.user_information.account_number+"-event");
			    fb_event_ref.firebaseobj.child("members").setValue("none");
	}
	
	public void add_a_member_to_fb(ArrayList<String> member_ref){
		fb_event_ref.firebaseobj.child("members").setValue(member_ref);;
	}
	
	public void set_up_map_view(){
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(commondata.user_information.latitude, commondata.user_information.longitude)) // visitor
                .title("me")).showInfoWindow();
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours
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
        group_list.add(group.curr_person);
        get_this_person_loc(group.curr_person);
        add_a_member_to_fb(group_list);
	}
	
	public void get_this_person_loc(String per_email){
		new GetRestaurantAsyncTask().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE");
		/*
		try {
	    	server_response = new RequestTask().execute("http://54.183.113.236/metster/getprofiledata.php", account.appkey, group.curr_person, "1","1","1", "1", "1"
					, "1", "1", "1", "1", "1", "1" ).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public void create_event_notfication(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("New Meetup");
		alert.setMessage("Set meetup name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
		  event_info.event_name = value.toString();
		  setTitle(event_info.event_name);
		  fb_event_ref.firebaseobj.child("EventName").setValue(event_info.event_name);//update on firebase
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  event_info.event_name = commondata.user_information.account_number+"-event";
			  setTitle(event_info.event_name);
		  }
		});

		alert.show();
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
	
	
	//------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meet_up, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_meet_up,
					container, false);
			return rootView;
		}
	}

}
