package com.example.metster;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.metster.Login.fbdata;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Rend extends ActionBarActivity {
	GoogleMap mMap;
	AlertDialog levelDialog = null;
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
		static String food_type;
		static String is_exist;
	}
	
	public static class fb_event_ref{
		static String fbref;
		static Firebase firebaseobj;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rend);
		
		setupActionBar();
		set_up_map_view();
		check_if_event_exist();
		create_firebase_refrence();// this is base refrence
        
        //----> set up fire base refrence
        
	}
	
	public void delete_event(View v){
		if(event_info.is_exist != null){
			StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
			strBuilder.append(commondata.user_information.account_number);
		    fb_event_ref.fbref = strBuilder.toString();
		    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
		    fb_event_ref.firebaseobj.removeValue();
		}
	}
	
	private void check_if_event_exist(){
	
		StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
		fb_event_ref.fbref = strBuilder.toString();
		fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
		fb_event_ref.firebaseobj.child(commondata.user_information.account_number.toString()).addValueEventListener(new ValueEventListener() {
			
			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
			    System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
			    if(snapshot.getValue() == null){
			    	event_info.is_exist = "no";
			    	create_event_notfication();// if event doesn`t exist then create an event
			    }else{
			    	event_info.is_exist = "yes";
			    	Iterable<DataSnapshot> children = snapshot.getChildren();
			    	while(children.iterator().hasNext()){
			    		System.out.println(children.iterator().next().getValue());
			    	}
			    	//-- fetch all the info
			    	// and update the info locally
			    }
			  }

			  @Override public void onCancelled(FirebaseError error) { 
				  System.out.println("error baby");  //prints "Do you have data? You'll love Firebase."
			  }

			});;
			
	}
	
	
	public void create_firebase_refrence(){
				StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
				strBuilder.append(commondata.user_information.account_number);
			    fb_event_ref.fbref = strBuilder.toString();
			    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
			    
	}
	
	public void add_a_member_to_fb(String member_email){
		member_count ++ ;
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).setValue(member_email);
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("latitudes").setValue(0.0);
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("longitudes").setValue(0.0);
		
		fb_event_ref.firebaseobj.child("member"+Integer.toString(member_count)).child("latitudes").addValueEventListener(new ValueEventListener() {
		
			  @Override
			  public void onDataChange(DataSnapshot snapshot) {
			    System.out.println(snapshot.getValue());
			    
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
				    Toast.makeText(getApplicationContext(), "member just joined.", Toast.LENGTH_SHORT).show();
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
        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours
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
		if(emailEntry.getText().toString().isEmpty()){
			 Toast.makeText(this, "No email found for contact.",
                     Toast.LENGTH_LONG).show();
		}else{
        group.curr_person = emailEntry.getText().toString();
        Log.w("requesting",group.curr_person);
        group_list.add(group.curr_person);
        latitudes.add(commondata.user_information.latitude);
        longitudes.add(commondata.user_information.longitude);
        
        add_a_member_to_fb(group.curr_person);
        set_up_map_view();
		}
	}
	
	public void pick_food_type(){
		
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items = {" Chinese "," Coffee "," American ","Sea Food"," Pizza "," Asian ", " Japanese "," Mexican "," Italian ", " Indian", "Ice Cream"};
		            
		                // Creating and Building the Dialog 
		                AlertDialog.Builder builder = new AlertDialog.Builder(this);
		                builder.setTitle("Select food type");
		                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int item) {
		                   
		                    
		                    switch(item)
		                    {
		                        case 0:
		                                event_info.food_type = "chinese";
		                                 break;
		                        case 1:
		                                // Your code when 2nd  option seletced
		                        	event_info.food_type = "coffee";
		                                break;
		                        case 2:
		                               // Your code when 3rd option seletced
		                        	event_info.food_type = "american";
		                                break;
		                        case 3:
		                                 // Your code when 4th  option seletced  
		                        	event_info.food_type = "seafood";
		                                break;
		                        case 4:
	                                // Your code when first option seletced
		                        	event_info.food_type = "pizza";
	                                 break;
		                        case 5:
		                        	event_info.food_type = "asian";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 6:
		                        	event_info.food_type = "japanese";
	                               // Your code when 3rd option seletced
	                                break;
		                        case 7:
		                        	event_info.food_type = "mexican";
	                                 // Your code when 4th  option seletced            
	                                break;
		                        case 8:
		                        	event_info.food_type = "italian";
	                                // Your code when first option seletced
	                                 break;
		                        case 9:
		                        	event_info.food_type = "indian";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 10:
		                        	event_info.food_type = "icecream";
	                               // Your code when 3rd option seletced
	                                break;                
		                        
		                    }
		                    levelDialog.dismiss();
		                    setTitle(event_info.event_name+ "  " + event_info.food_type);
		                    }
		                });
		                levelDialog = builder.create();
		                levelDialog.show();
	}
	
	public void get_places_mean_loc(Double mean_latitude, Double mean_longitude){
		Firebase myFirebaseflag = new Firebase("https://met-ster-control.firebaseio.com/");
		myFirebaseflag.child("dataready").setValue("no");
		String req_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+mean_latitude+","+mean_longitude+"&radius=5000&types=food&keyword="+event_info.food_type+"&key=AIzaSyCZQEuWjrNvrvPFzx6SQNxk_2xjtnGWvHE";
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
		  fb_event_ref.firebaseobj.child("EventName").setValue(commondata.user_information.account_number+"-event");
		  fb_event_ref.firebaseobj.child("EventName").setValue(event_info.event_name);//update on firebase
		  // Do something with value!
		  pick_food_type();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Set your Status");
			alert.setMessage("What I am upto!!");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  Editable value = input.getText();
			  commondata.user_information.status = value.toString();
			  setTitle(commondata.user_information.status);
			  fbdata.firebaseobj.child("Status").setValue(commondata.user_information.status);
			  // Do something with value!
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
			return true;
			
		case R.id.person_icon1:
			
			return true;
		case R.id.refresh_icon1:
			
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu1) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.rend_activity_actions, menu1);
	    return super.onCreateOptionsMenu(menu1);
	    
	    
	}
	
	@Override
	public void onBackPressed() {
	 
		finish();
			
	}
}
