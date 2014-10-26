package com.example.metster;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.metster.Login.Map;
import com.example.metster.Login.Userslist;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Rend extends Activity {
	GoogleMap mMap;
	private static final int CONTACT_PICKER_RESULT = 1001;
	public int member_count = 0;
	String server_response;
	ArrayList<String> group_list = new ArrayList<String>();
	ArrayList<Double> latitudes = new ArrayList<Double>();
	ArrayList<Double> longitudes = new ArrayList<Double>();
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
		setContentView(R.layout.activity_rend);
		//----> set up maps
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(commondata.user_information.latitude, commondata.user_information.longitude)) // visitor
                .title(Integer.toString(Userslist.user_count))).showInfoWindow();
		//----> call event function
        create_event_notfication();
        //----> set up fire base refrence
        create_firebase_refrence();// this is base refrence
        
        
	}
	
	
	
	public void create_firebase_refrence(){
		//----------------------> Fire base reference creation
				StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
				strBuilder.append(commondata.user_information.account_number);
			    fb_event_ref.fbref = strBuilder.toString();
			    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
			    fb_event_ref.firebaseobj.child("EventName").setValue(commondata.user_information.account_number+"-event");
	}
	
	public void add_a_member_to_fb(String member_email){
		member_count ++ ;
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).setValue(member_email);
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("latitudes").setValue(0.0);
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("longitudes").setValue(0.0);
		
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("latitudes").addValueEventListener(new ValueEventListener() {
		
			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
			    System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
			    
			    //----- parse and get what changed
			    String[] refrencetosnap = snapshot.getRef().toString().split("/");
			    String reftoarray = refrencetosnap[4].substring(refrencetosnap[4].length()-1);
			    Log.w("this is", refrencetosnap[4].substring(refrencetosnap[4].length()-1));//get the last char
			    // update that particular in array list
			    latitudes.indexOf(Integer.parseInt(reftoarray));
			    latitudes.set(Integer.parseInt(reftoarray)-1, Double.parseDouble(snapshot.getValue().toString()));
			    set_up_map_view();
			  }

			  @Override public void onCancelled(FirebaseError error) { }

			});
		

		

		
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("longitudes").addValueEventListener(new ValueEventListener() {

			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
				
				//----- parse and get what changed
				    String[] refrencetosnap = snapshot.getRef().toString().split("/");
				    String reftoarray = refrencetosnap[4].substring(refrencetosnap[4].length()-1);
				    Log.w("this is", refrencetosnap[4].substring(refrencetosnap[4].length()-1));//get the last char
				    // update that particular in array list
				    longitudes.indexOf(Integer.parseInt(reftoarray));
				    longitudes.set(Integer.parseInt(reftoarray)-1, Double.parseDouble(snapshot.getValue().toString()));
				    set_up_map_view();
			  }

			  @Override public void onCancelled(FirebaseError error) { }

			});
		Toast.makeText(getApplicationContext(), group.curr_person + " has been added", Toast.LENGTH_SHORT).show();
		try {
 	    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/exe_gcm_send.php",commondata.user_information.account_number,group.curr_person,Integer.toString(member_count),"1","1","1","1"
 			, "1", "1", "1", "1", "1", "1").get();
 			} catch (InterruptedException e) {
 							// TODO Auto-generated catch block
 				e.printStackTrace();
 			} catch (ExecutionException e) {
 							// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
	}
	
	public void find_places(View w){
		
		Double temp_lat = 0.0;
		Double temp_long = 0.0;
		for(int i = 0;i< latitudes.size();i++){
			temp_lat += latitudes.get(i);
		}
		for (int i = 0; i < longitudes.size(); i++){
			temp_long += longitudes.get(i);
		}
		temp_lat = temp_lat / latitudes.size();
		temp_long = temp_long / latitudes.size();
		latitudes.add(temp_lat);
		longitudes.add(temp_long);
		group_list.add("mean");
		Log.w("meanlat",Double.toString(temp_lat));
		Log.w("meanlon",Double.toString(temp_long));
		set_up_map_view();
		get_places_mean_loc(temp_lat, temp_long);
		
	}
	
	
	public void set_up_map_view(){
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        
        for(int i = 0; i< group_list.size(); i++){
        
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudes.get(i), longitudes.get(i))) // visitor
                ).showInfoWindow();
        }
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 16));
        mMap.getUiSettings().setZoomControlsEnabled(false);
	}
	
	public void set_up_map_for_places(){
		      
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
       
        
        for(int i = 0; i< commondata.places_found.places.size(); i++){
        Log.w("plcae",commondata.places_found.places.get(i));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(commondata.places_found.latitudes.get(i), commondata.places_found.longitudes.get(i))) // visitor
                ).showInfoWindow();
        }
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 16));
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
        latitudes.add(Map.latival);
        longitudes.add(Map.Longival);
        
        add_a_member_to_fb(group.curr_person);
        set_up_map_view();
	}
	
	public void get_places_mean_loc(Double mean_latitude, Double mean_longitude){
		Firebase myFirebaseflag = new Firebase("https://met-ster-control.firebaseio.com/");
		myFirebaseflag.child("dataready").setValue("no");
		String req_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mean_latitude+","+mean_longitude+"&radius=500&types=food&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE";
		 try {
			ArrayList<Restaurant> run_places = new GetRestaurantAsyncTask().execute(req_url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//set_up_map_for_places();
		//Log.w("plcea",commondata.places_found.places.get(0));

		myFirebaseflag.child("dataready").addValueEventListener(new ValueEventListener() {

			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
			    System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
			    //set_up_map_for_places();
			    list_rest();
			  }

			  @Override public void onCancelled(FirebaseError error) { }

			});
		//Log.w("plcea",commondata.places_found.places.get(0));
		
	}
	
	public void list_rest(){
		//--------
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Best Matching places");

		ListView modeList = new ListView(this);
		ArrayList<String> stringArray = new ArrayList<String>();
		for(int i =0 ; i< commondata.places_found.places.size();i++)
		stringArray.add(commondata.places_found.places.get(i));
		//String[] stringArray = new String[] { "Bright Mode", "Normal Mode" };
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		dialog.show();
		
		//--------
		/*
		final CharSequence[] items = {
				commondata.places_found.places.get(0), 
				commondata.places_found.places.get(1),
				commondata.places_found.places.get(2),
				commondata.places_found.places.get(3),
				commondata.places_found.places.get(4),
				"Blue"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Best match places");
		builder.setItems(items, new DialogInterface.OnClickListener() {

		   public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		   }

		});

		AlertDialog alert = builder.create();

		alert.show(); */
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
}
