package com.example.metster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
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
	ArrayList<Double> lat_lon = new ArrayList<Double>();
	
	int temp_mem;
	public static class group{
		static String curr_person;
	}
	
	public static class event_info{
		static String event_name;
		static String food_type;
		static String is_exist;
		static String is_food_chosen;
	}
	
	public static class fb_event_ref{
		static String fbref;
		static Firebase firebaseobj;
	}
	
	private static class contact_info{
		static String contact_name;
		static String contact_number;
	}
	
	//
	private static final String TAG = Rend.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private String access_to_button;
    private String event_tracer_is_setup;
    private String initiate;
    
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rend);
		setupActionBar();
		initiate = "no";
		create_firebase_refrence();// this is base refrence
		add_child_listener();
		check_if_event_exist();
		try{
			setup_initial_map();
		}catch(Exception e){
			System.out.println("view error");
		}
		if(event_info.is_exist == "yes"){//event already exists
			access_to_button = "no";
			System.out.println("event exist");
			String file_contents = read_event_file("metster_event_info.txt");
			String[] parts = file_contents.split("-->");
			setTitle(parts[0]+"--"+parts[1]);
			Thread thread = new Thread()
			{
			      @Override
			      public void run() {
			    	  Looper.prepare();
			    	  ret_data();
			      }
			  };

			thread.start();
			
			System.out.println("final;" + lat_lon.toString());
		}else{//event doesnot exists
			setTitle("Create an Event");
			access_to_button = "yes";
			event_info.is_food_chosen = "no";
			event_info.is_exist = "no";
			create_event_notfication();//create a new event
		}
		
	}
	
	private void setup_initial_map(){
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 16));
        mMap.getUiSettings().setZoomControlsEnabled(false);
	}
	
	private void ret_data(){
		
		fb_event_ref.firebaseobj.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				// TODO Auto-generated method stub
				latitudes.clear();
				longitudes.clear();
				group_list.clear();
				
				for (DataSnapshot child : snapshot.getChildren()) {// go through each member
					group_list.add("member");		
					latitudes.add(Double.parseDouble(child.child("latitudes").getValue().toString()));
					longitudes.add(Double.parseDouble(child.child("longitudes").getValue().toString()));
					set_up_map_view();
		        }
				
			}
			
		});
		
	}
	
	private void add_child_listener(){
		fb_event_ref.firebaseobj.addChildEventListener( new ChildEventListener() {
			
			public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
				String changed_member = snapshot.getName().toString();
				try{
				String changed_member_id = changed_member.split("-")[1];// get only the number
				System.out.println("member id is" + changed_member_id );
				System.out.println("latitude is:" + snapshot.child("latitudes").getValue().toString());
				System.out.println("longitude is " + snapshot.child("longitudes").getValue().toString());
				latitudes.set(Integer.parseInt(changed_member_id)-1, Double.parseDouble(snapshot.child("latitudes").getValue().toString()));
				longitudes.set(Integer.parseInt(changed_member_id)-1, Double.parseDouble(snapshot.child("longitudes").getValue().toString()));
				set_up_map_view();
				}catch(Exception e){
					System.out.println("something");
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				
			}

			
		});
	}
	
	/*
	 * This method creates the event files and stores info in this format
	 * event_name-->memberid1-->memberid2-->...-->memberidn
	 */
	
	private void create_event_file(){
		
		delete_event_file();
		
        FileOutputStream outputStream;
        StringBuilder stringBuilder = null;
        stringBuilder = new StringBuilder();
          try {
            outputStream = openFileOutput("metster_event_info.txt", Context.MODE_PRIVATE);
            
            /*
             * get the data to write into it
             */
          
            stringBuilder.append(event_info.event_name);
            stringBuilder.append("-->");
            stringBuilder.append(event_info.food_type);
            stringBuilder.append("-->");
            stringBuilder.append(Integer.toString(member_count+1));
            /*
            for(int i = 0; i< group_list.size(); i++){
            	stringBuilder.append("-->");
            	stringBuilder.append(group_list.get(i));
            }
            */
            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.close();
          } catch (Exception e) {
            e.printStackTrace();
          }catch( Throwable t ) { //Exception handling of nested exceptions is painfully clumsy in Java
              if( t instanceof ExecutionException ) {
                  t = t.getCause();
              }
          }
	}
	
	/*
	 * This method reads event file information
	 */
	
	private String read_event_file(String fname){
		StringBuilder stringBuilder = null;
		FileInputStream inputStream;
		BufferedReader bufferedReader;
		try {
            inputStream = openFileInput(fname);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
            .show();
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT)
            .show();
        }	
		return stringBuilder.toString();
	}
	
	/*
	 * This method deletes the event_file
	 */
	
	private void delete_event_file(){
		File dir = getFilesDir();
		File file = new File(dir, "metster_event_info.txt");
		file.delete();
	}
	
	/*
	 *  This method deletes the event and restarts the event
	 */
	
	public void delete_event(){
		//if(event_info.is_exist != null){ // verify later when we add file
			StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
			strBuilder.append(commondata.user_information.account_number);
		    fb_event_ref.fbref = strBuilder.toString();
		    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
		    fb_event_ref.firebaseobj.removeValue();
		    delete_event_file();
		//}
	}
	
	/*
	 * this method checks if event exists
	 */
	
	private void check_if_event_exist(){
	
		try {
            FileInputStream inputStream = openFileInput("metster_event_info.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                System.out.println(stringBuilder.toString());
                event_info.is_exist = "yes";
                } else{
            	event_info.is_exist = "no";
            }
        }
        catch (FileNotFoundException e) {
        	event_info.is_exist = "no";
            
        } catch (IOException e) {
        	event_info.is_exist = "no";
        }
		
	}
	
	public void create_firebase_refrence(){
				StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
				strBuilder.append(commondata.user_information.account_number);
			    fb_event_ref.fbref = strBuilder.toString();
			    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
	}
	
	/*
	 * This method will add a new member in firebase
	 */
	
	public void add_a_member_to_fb(final String member_email){
		
		Thread thread = new Thread()
		{
		      @Override
		      public void run() {
		    	  Looper.prepare();
				  member_count ++ ;
					create_firebase_refrence();
					fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count)).setValue("user");
					fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count)).child("latitudes").setValue(0.0);
					fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count)).child("longitudes").setValue(0.0);
					Toast.makeText(getApplicationContext(), group.curr_person + " has been added", Toast.LENGTH_SHORT).show();
					try {
				    	 String server_resp = new RequestTask().execute("http://54.183.113.236/metster/exe_gcm_send.php",commondata.user_information.account_number,member_email,Integer.toString(member_count),"1","1","1","1"
						, "1", "1", "1", "1", "1", "1").get();
						} catch (InterruptedException e) {
										// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
										// TODO Auto-generated catch block
							e.printStackTrace();
						}
		      }
		  };

		thread.start();			
	}
	
	/*
	 * This method will find the mean point to meet	
	 */
	
	public void find_places(View v){
		
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
		group_list.add("mean");// mean is the last point equivalent for group_list content
		Log.w("meanlat",Double.toString(temp_lat));
		Log.w("meanlon",Double.toString(temp_long));
		/*
		 * set up mean point on fb
		 */
		create_firebase_refrence();
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).setValue("user");
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).child("latitudes").setValue(temp_lat);
		fb_event_ref.firebaseobj.child("member-"+Integer.toString(member_count+1)).child("longitudes").setValue(temp_long);
		
		set_up_map_view();
		get_places_mean_loc(temp_lat, temp_long);
		
	}
	
	
	public void set_up_map_view(){
		GoogleMap mMap;
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.visitormap)).getMap();
        mMap.clear();
        try{
        for(int i = 0; i< group_list.size(); i++){
        if(i==longitudes.size()-1){
        	mMap.addMarker(new MarkerOptions()
            .position(new LatLng(latitudes.get(i), longitudes.get(i))) // visitor
            .title("meet here!")).showInfoWindow();
        }else{
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudes.get(i), longitudes.get(i))) // visitor
                ).showInfoWindow();
        }
        }
        mMap.setMyLocationEnabled(true);
        LatLng currlocation = new LatLng(commondata.user_information.latitude, commondata.user_information.longitude);// yours
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 16));
        mMap.getUiSettings().setZoomControlsEnabled(false);
        }catch(Exception e){
        	System.out.println("something fishy");
        	System.out.println("size" + group_list.size());
        	System.out.println("latitudes" + latitudes.toString());
        	System.out.println("longitudes" + longitudes.toString());
        	//ret_data();
        }
	}
	
	public void AccessButton(View view) {
		if(access_to_button == "yes"){
			if(event_tracer_is_setup == "yes"){
				if(event_info.is_food_chosen == "yes"){
					//---------
					ContactPicker();
					//----------
				}else{
					pick_food_type();
				}
			}else{
				create_event_notfication();
			}
		}else{
			/*
			 *  Tell them you can`t add more people and check if they want to create tracer 
			 */
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Notification");
			alert.setMessage("You cannot add more people to this tracer. Would you like to create a new tracer?");


			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  
				delete_event();
				Intent intent2 = new Intent(Rend.this, Rend.class);
				startActivity(intent2);
				finish();
				
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			  }
			});

			alert.show();
			
			
		}
	}
	
	private void ContactPicker(){
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
	            Contacts.CONTENT_URI);
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	    //---
	    
	    AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Notification");
		alert.setMessage("Would you like to add more people to this tracer?");


		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  
			ContactPicker();
		  }
		});

		alert.setNegativeButton("No I am done", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // 
			  initiate = "yes";
		  }
		});

		alert.show();
		//---------
	    
	    //---
	}
	
	private void add_this_person(){
		
		
			group.curr_person = contact_info.contact_number;
			Log.w("requesting",group.curr_person);
			group_list.add(group.curr_person);
			create_event_file();//update local file after every member added
			latitudes.add(commondata.user_information.latitude);
			longitudes.add(commondata.user_information.longitude);
			add_a_member_to_fb(group.curr_person);
			set_up_map_view();
		
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
		                                event_info.is_food_chosen = "yes";
		                                 break;
		                        case 1:
		                                // Your code when 2nd  option seletced
		                        	event_info.food_type = "coffee";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 2:
		                               // Your code when 3rd option seletced
		                        	event_info.food_type = "american";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 3:
		                                 // Your code when 4th  option seletced  
		                        	event_info.food_type = "seafood";
		                        	event_info.is_food_chosen = "yes";
		                                break;
		                        case 4:
	                                // Your code when first option seletced
		                        	event_info.food_type = "pizza";
		                        	event_info.is_food_chosen = "yes";
	                                 break;
		                        case 5:
		                        	event_info.food_type = "asian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 6:
		                        	event_info.food_type = "japanese";
		                        	event_info.is_food_chosen = "yes";
	                               // Your code when 3rd option seletced
	                                break;
		                        case 7:
		                        	event_info.food_type = "mexican";
		                        	event_info.is_food_chosen = "yes";
	                                 // Your code when 4th  option seletced            
	                                break;
		                        case 8:
		                        	event_info.food_type = "italian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when first option seletced
	                                 break;
		                        case 9:
		                        	event_info.food_type = "indian";
		                        	event_info.is_food_chosen = "yes";
	                                // Your code when 2nd  option seletced
	                                break;
		                        case 10:
		                        	event_info.food_type = "icecream";
		                        	event_info.is_food_chosen = "yes";
	                               // Your code when 3rd option seletced
	                                break;   
	                             default:
	                            	 event_info.food_type = "american";
	                            	 event_info.is_food_chosen = "no";
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
			    System.out.println("get_places"+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
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

		alert.setTitle("New Event Tracer");
		alert.setMessage("Set Event name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
		  event_info.event_name = value.toString();
		  create_firebase_refrence();
		  fb_event_ref.firebaseobj.setValue(event_info.event_name);//update on firebase
		  create_event_file();//update local file
		  pick_food_type();
		  create_event_file();//update local file after food chosen
		  event_tracer_is_setup = "yes";
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  event_info.event_name = "Metster-event";
			  setTitle("Create an Event");
			  event_info.is_exist = "no";
			  event_tracer_is_setup = "no";
		  }
		});

		alert.show();
	}
	
	private void confirm_add_this_person(){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		final int height = size.y;
		
		ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.ic_home);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Confirm");
		alert.setMessage("Do you want to add " + contact_info.contact_name);
		alert.setIcon(R.drawable.ic_action_add_person);
		// Set an EditText view to get user input 

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		add_this_person();
		  // add this person
			Toast toast= Toast.makeText(getApplicationContext(), 
					contact_info.contact_name + " has been added", Toast.LENGTH_SHORT);  
					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, height/4);
					TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
					//v.setBackgroundColor(Color.TRANSPARENT);
					v.setTextColor(Color.rgb(175, 250, 176));
					toast.show();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  //just cancel it
		  }
		});

		alert.show();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		super.onActivityResult(requestCode, resultCode, data);
		 
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onactivityres: " + data.toString());
            uriContact = data.getData();
 
            retrieveContactName();
            retrieveContactNumber();
            confirm_add_this_person();
        }
		
	}
	
	/*
	 * (non-Javadoc)
	 * Contact functions
	 */
	 
    private void retrieveContactNumber() {
 
        String contactNumber = null;
 
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
 
        if (cursorID.moveToFirst()) {
 
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
 
        cursorID.close();
 
        Log.d(TAG, "Contact ID: " + contactID);
 
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
 
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
 
                new String[]{contactID},
                null);
 
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
 
        cursorPhone.close();
        contactNumber = contactNumber.replace('(',' ');
        contactNumber = contactNumber.replace(')',' ');
        contactNumber = contactNumber.replace('-',' ');
        contactNumber = contactNumber.replace(" ","");
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        contact_info.contact_number = contactNumber;
    }
 
    private void retrieveContactName() {
 
        String contactName = null;
 
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
 
        if (cursor.moveToFirst()) {
 
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
 
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
 
        cursor.close();
 
        Log.d(TAG, "Contact Name: " + contactName);
        contact_info.contact_name = contactName;
    }	
	//-----------------
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			delete_event();
			create_event_notfication();
			return true;
		case R.id.delete_icon:
			/*
			 *  when we delete an event just restart this intent
			 */
			delete_event();
			Intent intent2 = new Intent(Rend.this, Rend.class);
			startActivity(intent2);
			finish();
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
	
	
}
