package com.nmysore.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nmysore.metster.commondata.host_event_node;
import com.nmysore.metster.commondata.place_details;

public class Login extends Activity {

	public static class group {
		static String curr_person;
	}

	public static class fbdata {

		static String fbref;
		static Firebase firebaseobj;

	};

	public static class fb_pref_ref {

		static String fbref;
		static Firebase firebaseobj;

	};

	public static class event_info {
		static String event_name;
		static String food_type;
		static Boolean is_host;
	}

	public static class Userslist {
		static String server_response;
		static String numberofusers;
		static int user_count;
	};

	public static class fb_event_ref {
		static String fbref;
		static Firebase firebaseobj;
	}

	private static class contact_info {
		static String contact_name;
	}

	// ----------------------------------------->
	//
	private static final String TAG = Login.class.getSimpleName();
	AlertDialog levelDialog = null;
	Geocoder gcd;
	GoogleMap mMap;
	LocationManager locationManager;
	LocationListener locationListener;
	Location location;
	Location position;
	String provider;
	List<Address> addresses;
	private final Handler _handler = new Handler();
	Runnable getData;
	Criteria criteria = new Criteria();
	Bundle profilelistactdata = new Bundle();
	ProgressDialog pd;
	Location postion_get;
	boolean isGPSEnabled;
	boolean isNetworkEnabled;
	ValueEventListener listn;
	ChildEventListener child_listner;
	Boolean listnerflag;
	RadioGroup travelchoice;
	ArrayList<Restaurant> restlist;
	int infocounter;
	ImageView imageView;
	String server_response;
	Integer async_counter = 0;
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();// Show the Up button in the action bar.
		getActionBar().setIcon( new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		setTitle("New Event");
		Firebase.setAndroidContext(this);
				
		// This is just a verification		
		if (commondata.facebook_details.facebook == null) {
			Intent intent = new Intent(Login.this, HomescreenActivity.class);
			startActivity(intent);
			finish();
		}

		infocounter = 1;
		listnerflag = true; // controls if image is clicked
		event_info.food_type = "american";
		commondata.prefrences.price = (float) 2.5;
		commondata.prefrences.travel = 5.0;
		commondata.prefrences.hour = 0;
		commondata.prefrences.minute = 0;
		commondata.prefrences.food = "american";

		event_info.is_host = false;// by default no host access
		
		create_firebase_refrence();// use fbref object as the refrence.
		
		/*
		 * check if invite exists
		 */
		SharedPreferences invite_notification = getApplicationContext().getSharedPreferences("invite_notification", MODE_PRIVATE);
		String invite_status = invite_notification.getString("invite_status", "none");
		
		if(commondata.event_information.invites != null){
	
			list_invites();
		}
		
		
		// this section contains code to accept or reject invite use it later.
		
		/*
		
		if(invite_notification.toString() != "none"){
			final String invite_from = invite_notification.getString("invite_from", "none"); // the one who sent it
			final String event_reference = invite_notification.getString("eventid", "none");
			final String sender_name =invite_notification.getString("sender_name","none");
			String invite_message = invite_notification.getString("message", "none");
			System.out.println("you have a invite from " + sender_name + " " + invite_message);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Invite from " + invite_from);
			alert.setMessage(invite_message);
			alert.setPositiveButton("Accept",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// gcm notify accept
							// store in local and notify host
							JSONObject json = new JSONObject(); 
							try {
								json.put("host", commondata.facebook_details.facebook);
								json.put("to_id", invite_from); 
								json.put("payload_type", "invite_accept");
								json.put("event_reference", event_reference);
								json.put("sender_name", commondata.facebook_details.name);
								json.put("payload_message", "sounds great");
						
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							System.out.println("in json" + json.toString());
							gcm_send_data(commondata.facebook_details.facebook, invite_from, json.toString());
						}
					});

			alert.setNegativeButton("Reject",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// gcm notify and join		
							
							JSONObject json = new JSONObject(); 
							try {
								json.put("host", commondata.facebook_details.facebook);
								json.put("to_id", invite_from); 
								json.put("payload_type", "invite_reject");
								json.put("event_reference", event_reference);
								json.put("sender_name", commondata.facebook_details.name);
								json.put("payload_message", "sounds great");
									
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							System.out.println("in json" + json.toString());
							gcm_send_data(commondata.facebook_details.facebook, invite_from, json.toString());
							
						}
					});
			alert.setCancelable(false);
			
			alert.show();
			
		}else{
			System.out.println("no invite exists");
		}

		*/
		/*
		 * check if you are on any event
		 */
		
		/*
		 * case a - 0 : user has hosted few events
		 * case b - 1: user is part of few events
		 * case c - 2: user has hosted and also part of few events
		 * case d - 3: user is not part of any event
		 * 
		 * case d --> this is a new event case
		 * case a --> we will display most recent hosted event but can view any
		 * case c --> if user has hosted then will display most recent hosted so it reduced to case a
		 * case b --> we will display most recent event the user is part of.
		 */
		
		
		// to be safe lets pull shared pref on event here.
		int event_case = 3;
		
		//********** retieve events information from localdb
		
		SharedPreferences eventsjoined = getApplicationContext().getSharedPreferences("eventjoined", MODE_PRIVATE);
		
		//******** and store them in commondata
		if(eventsjoined != null){
			Map<String,?> joinedkeys = eventsjoined.getAll();
			for(Map.Entry<String,?> entry : joinedkeys.entrySet()){
				String event_id = entry.getKey();
	            String event_name = entry.getValue().toString();
	            commondata.event_information.event_joined_table.put(event_id, event_name);
			}
		}
		
		
		//********* check which case it belongs to 
		if(commondata.event_information.event_joined_table.isEmpty() & commondata.event_information.event_hosted_table.isEmpty()){ // the user is not in any event
			event_case = 3; // new event case
		}else{
			event_case = 2;
		}
		if(!commondata.event_information.event_joined_table.isEmpty()){
			event_case = 1;
		}
		if(!commondata.event_information.event_hosted_table.isEmpty()){ // user has hosted something
			event_case = 0;
		}
		
		
		switch (event_case){
		case 0 :// the user has hosted few events
			//commondata.event_information.eventID = set to latest hosted event
			String current_event = commondata.event_information.event_hosted_table.get(commondata.event_information.event_hosted_table.size());
			System.out.println("all events " + commondata.event_information.event_hosted_table);
			setTitle("current event" + commondata.event_information.event_hosted_table.size());
			break;
		case 1:// the user is part of few events
			//commondata.event_information.eventID = set to latest joined event
			System.out.println("case 1");
			break;
		case 2:// user has hosted and also part of few events
			//commondata.event_information.eventID = set to latest hosted event
			System.out.println("case 2");
			break;
		case 3: // user is not part of any event
			commondata.event_information.eventID = null;
			System.out.println("case 3");
			break;
		default:// user is not part of any event
			System.out.println("case 0");
			commondata.event_information.eventID = null;
			break;
		}
		
		
		if (commondata.event_information.eventID != null) {// this will be set
															// from res of mysql
			/*
			 * check if this user is host or not
			 */
			if (commondata.event_information.eventID
					.contains(commondata.facebook_details.facebook)) {
				event_info.is_host = true;
			} else {
				event_info.is_host = false;
			}
			

		} else {// event is not there
			setTitle("New Event");// tell user to setup new event
		}

		// --------

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// --------------------------------------------------------
		// Define a listener that responds to location updates
		// ---------------------------------------------------------
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					
					commondata.user_information.latitude = location
							.getLatitude();
					commondata.user_information.longitude = location
							.getLongitude();
					if (commondata.event_information.eventID != null) {
						/*
						create_firebase_refrence();
						fb_event_ref.firebaseobj
								.child(commondata.facebook_details.facebook
										+ "--"
										+ commondata.facebook_details.name)
								.child("Latitude")
								.setValue(commondata.user_information.latitude);
						fb_event_ref.firebaseobj
								.child(commondata.facebook_details.facebook
										+ "--"
										+ commondata.facebook_details.name)
								.child("Longitude")
								.setValue(commondata.user_information.longitude);
								*/
					}
				} else {
					System.out.println("location is null");
				}

			}

			@SuppressWarnings("unused")
			public void onStatusChanged(Location location) {
				if (location != null) {

					commondata.user_information.latitude = location
							.getLatitude();
					commondata.user_information.longitude = location
							.getLongitude();
				} else {
					System.out.println("location is null");
				}

			}

			@SuppressWarnings("unused")
			public void onProviderEnabled(Location location) {
				if (location != null) {

					commondata.user_information.latitude = location
							.getLatitude();
					commondata.user_information.longitude = location
							.getLongitude();
				} else {
					System.out.println("location null");
				}
			}

			public void onProviderDisabled(String provider) {
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}
			
			
		};
		// ------------------------------------------------------------------------
		// getting GPS and network status
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isGPSEnabled && !isNetworkEnabled) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Connection Error");
			alert.setMessage("Please check your network settings");
			alert.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Intent intent = new Intent(Login.this,
									HomescreenActivity.class);
							startActivity(intent);
							finish();
						}
					});

			alert.setNegativeButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});
			alert.show();
		} else {
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);
				postion_get = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (postion_get != null) {
					commondata.user_information.latitude = postion_get
							.getLatitude();
					commondata.user_information.longitude = postion_get
							.getLongitude();
					System.out.println("loc from network ");
				} else {
					System.out.println("loc from network error");
				}

			} else {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				postion_get = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (postion_get != null) {
					commondata.user_information.latitude = postion_get
							.getLatitude();
					commondata.user_information.longitude = postion_get
							.getLongitude();
					System.out.println("loc from gps ");
				} else {

					System.out.println("loc from gps error");
				}
			}

		}

		// ------------------------------------------------------------------>
		// Fetch the location details

		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		List<Address> addresses;

		try {
			addresses = gcd.getFromLocation(
					commondata.user_information.latitude,
					commondata.user_information.longitude, 1);
			if (addresses.size() > 0) {
				commondata.user_information.cityname = addresses.get(0)
						.getLocality();
				commondata.user_information.country = addresses.get(0)
						.getCountryCode();
				commondata.user_information.zip = addresses.get(0)
						.getPostalCode();
				commondata.user_information.addressline = addresses.get(0)
						.getThoroughfare();
			} else {
				System.out.println("address was zero");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------->
		// Button Actions
		SetupUIdata();
		
		// ----------------------- long pressed ends here
	
	}// on create

	/*
	 * name : set_firebase_listner
	 * @params : event_id root (facebook id of host and event id under that host)
	 * @return:
	 * @desp : this function stops old listner and sets new listner to new event
	 */
	private ChildEventListener set_firebase_listner(String eventid){
	
		String[] data = eventid.split("-->");
		final String host = data[0];// this give refrence to facebook id
		final String eventref = data[1]+"-->"+data[2];//event-->1
		StringBuilder strBuilder = new StringBuilder(
				"https://met-ster-event.firebaseio.com/");
		strBuilder.append(host+"/"+host+"-->"+eventref);
		final String baseref = strBuilder.toString();//https://met-ster-event.firebaseio.com/80897978789/80897978789-->event-->1 
		Firebase event_fb_ref = new Firebase(baseref);
		
		
		ChildEventListener listner = event_fb_ref.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				System.out.println("child removed");
			}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("child moved");
			}
			
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("child changed");
			}
			
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				System.out.println("nee child added");
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return listner;
		
	}
	
	
	
	
	/*
	 * name : remove_fb_listner
	 * @params : eventid
	 * @return : void
	 * @desp : for a given event the firebase listner will be removed. 
	 */
	
	/*
	private void remove_fb_listner(String eventid, listner){
		String[] data = eventid.split("-->");
		final String host = data[0];// this give refrence to facebook id
		final String eventref = data[1]+"-->"+data[2];//event-->1
		StringBuilder strBuilder = new StringBuilder(
				"https://met-ster-event.firebaseio.com/");
		strBuilder.append(host+"/"+host+"-->"+eventref);
		final String baseref = strBuilder.toString();//https://met-ster-event.firebaseio.com/80897978789/80897978789-->event-->1 
		Firebase event_fb_ref = new Firebase(baseref);
		event_fb_ref.removeEventListener();
	}
	*/
	
	
	/*
	 * name : display_events
	 * @params : json data
	 * @return : none
	 * @desp : this function displays the event data after pulling from firebase 
	 */
	
	
	private void display_events(JSONObject host_data){
		System.out.println("display_events");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Your Events");
		
		ListView inviteslist = new ListView(this);
		final ArrayList<String> listhostnames = new ArrayList<String>();
		final ArrayList<String> listmembers = new ArrayList<String>();
		final ArrayList<String> listeventnames = new ArrayList<String>();
		final ArrayList<Bitmap> listhostimages = new ArrayList<Bitmap>();
		final ArrayList<String> listhostref = new ArrayList<String>();
		final ArrayList<String> listeventids = new ArrayList<String>();
		listhostnames.clear();
		listeventnames.clear();
		listhostimages.clear();
		listmembers.clear();
		listhostref.clear();
		// all events come here now we need to parse and display.
		
		System.out.println("host_data" + host_data);
		
		
		Iterator<String> events = host_data.keys();
		while(events.hasNext()){
			String eventid = events.next();
			listeventids.add(eventid);
			try{
			JSONObject event_data = (JSONObject) host_data.get(eventid);
			String hostname = event_data.getString("hostname");
			String eventname = event_data.getString("eventname");
			String members = event_data.getString("number_of_children");
			String host_ref = event_data.getString("host_id");
			System.out.println("hostname" + hostname);
			
			//***
			/*
			URL img_value = new URL("https://graph.facebook.com/" + host_ref
					+ "/picture?type=large");
			final Bitmap himage = BitmapFactory.decodeStream(img_value
					.openConnection().getInputStream());
			listhostimages.add(himage);
			*/
			//****
			
			listhostnames.add(hostname);
			listmembers.add(members);
			listeventnames.add(eventname);
			listhostref.add(host_ref);
			
			}catch(Exception e){
				System.out.println("exception " + e);
			}
		}
		
		
		// display section
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.myevents,
				(ViewGroup) findViewById(R.id.myeventssection));
		
		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.myevents,
				listhostnames){
		    ViewHolder holder;

		    class ViewHolder {
		        ImageView himage;
		        TextView fullname;
		        TextView eventname;
		        ImageButton accept;
		    }

		    public View getView(final int position, View convertView,
		            final ViewGroup parent) {
		        final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		                .getSystemService(
		                        Context.LAYOUT_INFLATER_SERVICE);

		        if (convertView == null) {
		            convertView = inflater.inflate(
		                    R.layout.myevents, null);

		            holder = new ViewHolder();
		           // holder.icon = (ImageView) convertView
		            //        .findViewById(R.id.icon);
		            holder.fullname = (TextView) convertView
		                    .findViewById(R.id.fullname);
		            holder.eventname = (TextView) convertView.findViewById(R.id.eventname);
		            //View v = inflater.inflate( R.layout.invitelist, parent, false);
		            holder.accept = (ImageButton) convertView.findViewById(R.id.myevent_friendinvite);
		            holder.himage = (ImageView)  convertView.findViewById(R.id.myeventhostimage);
		            holder.accept.setTag(position);
		           
		            holder.accept.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("launching... " + listeventids.get(Integer.parseInt(v.getTag().toString())));
							parent.getChildAt(Integer.parseInt(v.getTag().toString())).setBackgroundColor(Color.BLUE);
					
						
							String eventid = listeventids.get(Integer.parseInt(v.getTag().toString()));
							System.out.println("event to be launched" + eventid);
							pull_data_from_firebase(eventid);
							
							// this will store in shared pref
							//on_invite_accepted(listeventreferences.get(Integer.parseInt(v.getTag().toString())));
							// On a new thread handle this case	
						}
					}); 
		            
		            convertView.setTag(holder);
		        } else {
		            // view already defined, retrieve view holder
		            holder = (ViewHolder) convertView.getTag();
		        }       
		        System.out.println("setting" + listhostnames.get(position));
		        holder.fullname.setText(listhostnames.get(position));
		        holder.eventname.setText(listeventnames.get(position) + " (" +listmembers.get(position)+")");
		        //holder.himage.setImageBitmap(listhostimages.get(position));
		         
		        
		        if(commondata.lazyload.image_ref.containsKey(listhostref.get(position))){
		       
		        	holder.himage.setImageBitmap( commondata.lazyload.image_ref.get(listhostref.get(position)));
		        }else{
		        new Thread(new Runnable() { 
		            public void run(){  
		            	System.out.println("thread issued");
		            
		            	try {
							new ImageDownloaderTask(holder.himage).execute(listhostref.get(position)).get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }).start();
		    }
				
		        
		        // load from yelp and set async
		        //Drawable drawable = getResources().getDrawable(R.drawable.eventbutton); //this is an image from the drawables folde
		        return convertView;
		    }
		};
		
		
		
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						
						dialog.dismiss();
					}
				});
		final Dialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();	
		//*****************
		
		
		
	}
	
	/*
	 * name : pull_host_info
	 * @params : eventid
	 * @return : none
	 * @desp : this funtion returns the host info for the event.
	 */
	
	private void pull_host_info(String eventslisting){
		System.out.println("incoming pull_host_info" + eventslisting);
		final String[] eventsids = eventslisting.split("<<");
		final String last_event = eventsids[eventsids.length - 1];
		final JSONObject all_events = new JSONObject();
		for(int i=0; i < eventsids.length ; i++){
		if(!eventsids[i].isEmpty() && eventsids[i].contains("event")){	
			System.out.println("hosting " + eventsids[i]);	
			final String eventid = eventsids[i];
			String[] data = eventid.split("-->");
			final String host = data[0];
			final String eventref = data[1]+"-->"+data[2];//event-->1
			StringBuilder strBuilder = new StringBuilder(
					"https://met-ster-event.firebaseio.com/");
			strBuilder.append(host+"/"+host+"-->"+eventref);
			strBuilder.append("/" + host);
			final String baseref = strBuilder.toString();
			//https://met-ster-event.firebaseio.com/80897978789/80897978789-->event-->1 /80897978789
			final Firebase event_fb_ref = new Firebase(baseref);
			System.out.println("pulling data for " + eventref + "from " + baseref);
			final JSONObject host_data = new JSONObject();
			// This is called only once
			// gets the host data
				event_fb_ref.addValueEventListener(new ValueEventListener() {
					
					@Override
					public void onDataChange(final DataSnapshot snapshot) {
						 
						Firebase parent = event_fb_ref.getParent();
						// gets the number of member for this event
						parent.addValueEventListener(new ValueEventListener() {
							
							@Override
							public void onDataChange(DataSnapshot parentref) {
								// TODO Auto-generated method stub
								long number_of_children = 0;
								try {
									number_of_children = parentref.getChildrenCount();
									if(number_of_children != 0){
										host_data.put("number_of_children", number_of_children);
										host_data.put("host_id", host);
										Iterable<DataSnapshot> host_attr = snapshot.getChildren();
										
										Iterator<DataSnapshot> params = host_attr.iterator();
										while(params.hasNext()){
										DataSnapshot val = params.next();
										if(val.getName().toString() == "nodename") host_data.put("hostname", val.getValue());
										if(val.getName().toString() == "eventname") host_data.put("eventname", val.getValue());
										}
									}
										
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
								
								if(eventid == last_event){
									
									try{
										all_events.put(eventid, host_data);
										display_events(all_events);
										}catch(Exception e){
											System.out.println("exception " + e);
										}
								
								}else{
									try{
									all_events.put(eventid, host_data);
									}catch(Exception e){
										System.out.println("exception " + e);
									}
								}
								
								
							}
							
							@Override
							public void onCancelled(FirebaseError arg0) {
								// TODO Auto-generated method stub
								
							}
						});
						
					  
						
				    }
					
					
					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}else{
				//toast_info("no events, please join or create a event");
				
			}
		}
	}
	
	
	/*
	 * name : pull_data_from_firevbase
	 * @params : user_root (facebookid)
	 * @return :
	 * @desp : This function pulls data from firebase and sets up event nodes.
	 * 			you just eventid, it fetches the host and it pulls all data for the event
	 * 			ot also sets up nodes for that which we can use
	 * 			input is of this form 80897978789-->event-->1 
	 */
	private void pull_data_from_firebase(final String eventid){
		commondata.event_information.given_events_lookup.clear();
		String[] data = eventid.split("-->");
		final String host = data[0];// this give refrence to facebook id
		final String eventref = data[1]+"-->"+data[2];//event-->1
		StringBuilder strBuilder = new StringBuilder(
				"https://met-ster-event.firebaseio.com/");
		strBuilder.append(host+"/"+host+"-->"+eventref);
		final String baseref = strBuilder.toString();//https://met-ster-event.firebaseio.com/80897978789/80897978789-->event-->1 
		Firebase event_fb_ref = new Firebase(baseref);
		System.out.println("pulling data for " + eventref + "from " + baseref);
		// This is called only once
		event_fb_ref.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				commondata.event_information.given_events_lookup.clear();
				Iterable<DataSnapshot> events = snapshot.getChildren();
		    	Iterator<DataSnapshot> members = events.iterator();
		    	ArrayList<host_event_node> nodelist = new ArrayList<host_event_node>();
		    	boolean flag = false;
		    	while(members.hasNext()){//this segment pulls hosted events
		    		DataSnapshot eventkey = members.next();
		    		host_event_node hostnode = new commondata.host_event_node();
		    		hostnode.eventid = host+"-->"+eventref;
		    		hostnode.number_of_people = eventkey.getChildrenCount();
		    		System.out.println("event "+ hostnode.eventid );// bug
		    		Iterable<DataSnapshot> data = eventkey.getChildren();
		    		Iterator<DataSnapshot> dat = data.iterator();
		    		while(dat.hasNext()){// this segment pulls users
		    			DataSnapshot params = dat.next();	
		    			if(params.getName().toString() == "eventname") hostnode.event_name = params.getValue().toString();
		    			commondata.event_information.eventname = hostnode.event_name;
		    			if(params.getName().toString() == "nodename") hostnode.nodename = params.getValue().toString();
		    			if(params.getName().toString() == "nodetype") hostnode.nodetype = params.getValue().toString();
	    				if(params.getName().toString() == "food") hostnode.food_type = params.getValue().toString();
	    				if(params.getName().toString() == "price") hostnode.price = params.getValue().toString();
	    				if(params.getName().toString() == "travel") hostnode.travel = params.getValue().toString();
	    				if(params.getName().toString() == "Latitude") {
	    					hostnode.Latitude = Double.parseDouble(params.getValue().toString());
	    					System.out.println("values added location "+ hostnode.Latitude + hostnode.Longitude);
	    				}
	    				if(params.getName().toString() == "Longitude"){
	    					hostnode.Longitude = Double.parseDouble(params.getValue().toString());
	    					System.out.println("values added location "+ hostnode.Latitude + hostnode.Longitude);
	    				}
	    				if(hostnode.Longitude != null || hostnode.Longitude != null){
	    					flag = true;
	    				}
	    				
		    		}
		    		nodelist.add(hostnode);	
		    		
		    	}
		    	System.out.println("nodelist has this data : " +  nodelist.size());
		    	if(flag){
		    	commondata.event_information.given_events_lookup.put(host+"-->"+eventref, nodelist);
		    	System.out.println("and ppp " + commondata.event_information.given_events_lookup.keySet().toString());
		    	System.out.println("checking " + commondata.event_information.given_events_lookup.size());
		    	launch_event(host+"-->"+eventref);
		    	flag = false;
		    	}else{
		    		System.out.println("lat of lon is null");
		    	}
				//display_node_data();
		    	//*********** prepare for launch
		    	//*********** launch the event
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ChildEventListener lister = set_firebase_listner(eventid);
	}
	
	/*
	 * name : display_node_data
	 * @params : None
	 * @return : null
	 * @desp : This is a test funtion to check data stores in nodes
	 */
	
	private void display_node_data(){
		System.out.println("displaying all node");
		Set<String> eventkeys = commondata.event_information.given_events_lookup.keySet();
		System.out.println("its keys are" + eventkeys);
		Iterator<String> keys = eventkeys.iterator();
		while(keys.hasNext()){
			String event = keys.next();
			System.out.println("event key - " + event);
			ArrayList<host_event_node> nodearry = commondata.event_information.given_events_lookup.get(event);
			
			System.out.println("number of people in this event" + nodearry.size());
			Iterator<host_event_node> arriter = nodearry.iterator();
			while(arriter.hasNext()){
				host_event_node node = arriter.next();
				System.out.println("eventid" + node.eventid);
				System.out.println("location" + node.Latitude + " " + node.Longitude);
			}
			
		}
		
	}
	
	/*
	 * name : launch_event (called from pull data from firebase)
	 * @params : event_id root (facebook id of (any)host and event id under that host)
	 * @return :
	 * @desp : This function takes the event id and sets view on that
	 * 			from event id we can get hostid and we can set listners on that
	 * 			This function first pulls data from the firebase and updates local nodes
	 *  		and launches based on that.
	 */
	private void launch_event(String eventid){
		commondata.event_information.eventID = eventid;
		System.out.println("handling event " + eventid);		
		Set<String> keys = commondata.event_information.given_events_lookup.keySet();
		Iterator<String> eventnodes = keys.iterator();
		while(eventnodes.hasNext()){
			String ky = eventnodes.next();
			ArrayList<host_event_node> nodes = commondata.event_information.given_events_lookup.get(ky);
			Iterator<host_event_node> node = nodes.iterator();
			commondata.places_found.latitudes.clear();
			commondata.places_found.longitudes.clear();
			commondata.places_found.names.clear();
			commondata.places_found.tokens.clear();
			while(node.hasNext()){
				host_event_node data = node.next();
				System.out.println("location" + data.Latitude +", " + data.Longitude );
				commondata.places_found.latitudes.add(data.Latitude);
				commondata.places_found.longitudes.add(data.Longitude);
				commondata.places_found.names.add(data.nodename);
				commondata.places_found.tokens.add(data.nodetype);
			}
		}
		
		//** all members are added
		set_up_map_view();
				
	}
	
	/*
	 * name : tost_info
	 * @params : String
	 * @return : void
	 * @desp : This function prints the data on to the screen. location of the data can be tuned here.
	 */

	private void toast_info(String info) {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int height = size.y;
		Toast toast = Toast.makeText(getApplicationContext(), info,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, height / 3);
		TextView v = (TextView) toast.getView().findViewById(
				android.R.id.message);
		v.setBackgroundColor(Color.TRANSPARENT);
		v.setTextColor(Color.rgb(66, 66, 66));
		v.setTextSize((float) 20.0);
		v.setTypeface(null, Typeface.BOLD);
		v.setTypeface(Typeface.SANS_SERIF);
		View vw = toast.getView();
		vw.setBackgroundColor(Color.TRANSPARENT);
		toast.show();
	}

	/*
	 * name : SetupUIdata
	 * @params : None
	 * @return : void
	 * @desp : This function is used to setup the initial screen upon loading. 
	 */
	public void SetupUIdata() {

		// ------------> Setup the GUI with data acquired
		TextView fname = (TextView) findViewById(R.id.FirstName);
		String first_name;
		String last_name;
		/*
		 * for some name we might not have second name so handle exception
		 */
		try {
			String[] name = commondata.facebook_details.name.split(" ");
			first_name = name[0];
			last_name = name[1];
		} catch (Exception e) {
			first_name = commondata.facebook_details.name;
			last_name = " ";
		}
		// ----------- Section Profile data
		fname.setText(first_name);
		TextView lname = (TextView) findViewById(R.id.LastName);
		lname.setText(last_name);
		TextView prof = (TextView) findViewById(R.id.address_line);
		prof.setText((String) commondata.user_information.addressline);
		TextView cc = (TextView) findViewById(R.id.CurrentCity);
		cc.setText((String) commondata.user_information.cityname);
		// ----------- Section Profile Image
		if (commondata.user_information.profileimage != null) {
			byte[] decodedString = Base64.decode(
					commondata.user_information.profileimage, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,
					0, decodedString.length);
			ImageButton imageButton = (ImageButton) findViewById(R.id.ProfileImage);
			imageButton.setImageBitmap(decodedByte);
		}
		// ----------- Section Maps
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.visitormap)).getMap();

		LatLng currlocation = new LatLng(commondata.user_information.latitude,
				commondata.user_information.longitude);// yours

		map.setMyLocationEnabled(true);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
		map.getUiSettings().setZoomControlsEnabled(false);

		// --------------------------------------------------------------------------------------------

	}

	
	/*
	 * name : set_up_map_view
	 * @params : None
	 * @retun : void
	 * @desp : This function sets up the initial map view 
	 */
	public void set_up_map_view() {
				
		
		try {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.visitormap)).getMap();
			mMap.clear();
			mMap.setTrafficEnabled(true);
			try {
				for (int i = 0; i < commondata.places_found.latitudes.size(); i++) {
					
					System.out.println("token " + commondata.places_found.tokens.get(i));
					
						if ( commondata.places_found.tokens.get(i).contains("host")){
							mMap.addMarker(new MarkerOptions()
							.position(
									new LatLng(
											commondata.places_found.latitudes
													.get(i),
											commondata.places_found.longitudes
													.get(i)))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.host))
							.title(commondata.places_found.names.get(i))
							);	
						}else{
					
								mMap.addMarker(new MarkerOptions()
										.position(
												new LatLng(
														commondata.places_found.latitudes
																.get(i),
														commondata.places_found.longitudes
																.get(i)))
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.pin))
										.title(commondata.places_found.names.get(i))
										);	
						}
				}

				mMap.setMyLocationEnabled(true);
				LatLng currlocation = new LatLng(
						commondata.user_information.latitude,
						commondata.user_information.longitude);// yours
				mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
			} catch (Exception e) {
				System.out.println("something fishy in setting up map");
				// ret_data();
			}
		} catch (Exception e) {
			System.out.println("unable to put maps");
		}
	}

	public void stopRepeatingTask() {
		_handler.removeCallbacks(getData);
	}

	/*
	 * name : on_image_click
	 * @params : View view
	 * @return : void
	 * @desp : This function sets the the location listener for user
	 * 		   This is a toggle function, If the color is red location listener is turned off
	 * 		   If the color is blue then the application will keep pushing user location.
	 */
	public void on_image_click(View view) {
		if (listnerflag) {
			listnerflag = false;
			LinearLayout ll = (LinearLayout) findViewById(R.id.Profiledata);
			View v = (View) findViewById(R.id.Dividerone);
			ll.setBackgroundColor(Color.parseColor("#FE642E"));
			v.setBackgroundColor(Color.parseColor("#FE642E"));
			locationManager.removeUpdates(locationListener);

		} else {
			listnerflag = true;
			LinearLayout ll = (LinearLayout) findViewById(R.id.Profiledata);
			ll.setBackgroundColor(Color.parseColor("#2E9AFE"));
			View v = (View) findViewById(R.id.Dividerone);
			v.setBackgroundColor(Color.parseColor("#2E9AFE"));
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}
		System.out.println("status: " + listnerflag.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 * name : onBackPressed
	 * @params : None
	 * @return : void
	 * @desp : This function takes care of functionalities when back button is pressed.
	 * 			We need to clear all location listeners and firebase listners.
	 */
	@Override
	public void onBackPressed() {

		new AlertDialog.Builder(this)
				.setView(
						getLayoutInflater().inflate(R.layout.custom_exit, null))
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								stopRepeatingTask();
								remove_location_listners();
								try {
									/*
									 * while existing remove firebase listners
									 */
									remove_firebase_listners();
								} catch (Exception e) {
									System.out.println("no fb ref");
								}

								Login.this.finish();
								System.exit(0);
							}
						}).setNegativeButton("No", null).show();
	}

	/*
	 * name : removed_firebase_listners
	 * @params : None
	 * @return : void
	 * @desp : this function removes all listners for firebase. 
	 */
	private void remove_firebase_listners() {
		fb_event_ref.firebaseobj.removeEventListener(listn);
		fb_event_ref.firebaseobj.removeEventListener(child_listner);
	}

	/*
	 * name : remove_location_listners
	 * @params : None
	 * @return : void
	 * @desp : this function removed the location updates 
	 */
	private void remove_location_listners() {
		locationManager.removeUpdates(locationListener);
	}

	/*
	 * name : create_event_notification
	 * @params : None
	 * @return : void
	 * @desp : this function creates the alert box for new event.
	 * 		   It takes event details like transport, time and rate preferences.
	 */
	@SuppressWarnings("deprecation")
	public void create_event_notification() {

		String food_type = pick_food_type();
 
	}
		
	
	
	/*
	 * name : on_invite_declined
	 * @params : event_id
	 * @return : none
	 * @desp : This function pulls invite data from the server and removes this event_id
	 * 			listing and updates the invites tabel on the sever.
	 */
	
	
	private void on_invite_declined(final String event_reference){
	
    	final JSONObject invites_to_server = new JSONObject();
    	String[] id = event_reference.split("-->");
    	String hostid = id[0];
    	
    	try {
    		
    		invites_to_server.put("id", commondata.facebook_details.facebook);
    		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	
    	Thread thread = new Thread() {
	
			@Override
			public void run() {
				
				String invites_response = postData("http://52.8.173.36/metster/handel_invites.php",
		    			"get_list",invites_to_server.toString(), commondata.facebook_details.email);
				
				String new_invites_data = null;
				if(invites_response != null){
					String[] lists = invites_response.split("%%");
					for(int i =0; i<lists.length; i++){
						if(lists[i].contains(event_reference)){
							// dont add to this list
						}else{
							
								if(new_invites_data == null){
									new_invites_data = lists[i];
								}else{
									new_invites_data = new_invites_data + "%%"+ lists[i];
								}
								
							
							
						}	
					}
				}else{
					//some error calls
				}
				
				try {
		    		
		    		invites_to_server.put("id", commondata.facebook_details.facebook);
		    		invites_to_server.put("data", new_invites_data);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
				
				System.out.println("updating this data" + new_invites_data);
				invites_response = postData("http://52.8.173.36/metster/handel_invites.php",
		    			"update",invites_to_server.toString(), commondata.facebook_details.email);
			
				System.out.println("in invites on server " + invites_response);
			}
		};
		
		thread.start();
    	
		JSONObject json = new JSONObject(); 
		try {
			json.put("host", commondata.facebook_details.facebook);
			json.put("to_id", hostid); 
			json.put("payload_type", "invite_reject");
			json.put("event_reference", event_reference);
			json.put("sender_name", commondata.facebook_details.name);
			json.put("payload_message", "sounds great");
	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("in json" + json.toString());
		
		System.out.println("respoding to " + hostid + "from" + commondata.facebook_details.facebook);
		gcm_send_data(commondata.facebook_details.facebook, hostid, json.toString());
		
		
		
	}
	
	
	/*
	 * name : on_event_accepted
	 * @params : event_id
	 * @return : none
	 * @desp : This function takes the invited event refrence and stores in the 
	 * 			shared pref. The user can see this event in show events and data 
	 * 			related to this event will be pulled there and launched.
	 */
	
	public void on_invite_accepted(final String event_reference){
		
		//************ pull data from local db
		
		SharedPreferences prefs = getSharedPreferences("myevents", MODE_PRIVATE); 
		String joinedevents = prefs.getString("joinedevents", null);
		String []events = null;
		if (joinedevents != null) {
		  String joinedeventlist = prefs.getString("joinedevents", "None");//"No name defined" is the default value.
		  events = joinedeventlist.split("<<");
		}
		final String eventreference = event_reference;
		String[] dat = event_reference.split("-->");
		String host = dat[0];
		String url = "https://met-ster-event.firebaseio.com/";
		url = url + host +"/"+eventreference + "/" + commondata.facebook_details.facebook;
		
		final HashMap<String, String> fb_data = new HashMap<String, String>();
		fb_data.put("nodename",commondata.facebook_details.name );
		fb_data.put("eventname",event_reference);
		fb_data.put("Latitude", commondata.user_information.latitude.toString());
		fb_data.put("Longitude", commondata.user_information.longitude.toString());
		fb_data.put("price", Float.toString(commondata.prefrences.price));
		fb_data.put("travel", Double.toString(commondata.prefrences.travel));
		fb_data.put("food", commondata.prefrences.food);
		fb_data.put("nodetype", "host");
		
		StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
		strBuilder.append(host);
		String refg = strBuilder.toString();
		Firebase newev = new Firebase(refg);
		
		newev.child(eventreference).child(commondata.facebook_details.facebook).setValue(fb_data);
		System.out.println("storing " + eventreference );
		//************** store this new event copy to local data base
		SharedPreferences.Editor editor = getSharedPreferences("myevents", MODE_PRIVATE).edit();
		 editor.putString("joinedevents", joinedevents +"<<"+eventreference);
		//editor.clear();
		 editor.commit();
		 
		 
		 //** once accepted to the invite remove the copy in sever else it will keep showing 
		 
		 
		 final JSONObject invites_to_server = new JSONObject();
	     String[] id = event_reference.split("-->");
	     String hostid = id[0];
	    	
	    	try {
	    		
	    		invites_to_server.put("id", commondata.facebook_details.facebook);
	    		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	
	    	
	    	Thread thread = new Thread() {
		
				@Override
				public void run() {
					
					String invites_response = postData("http://52.8.173.36/metster/handel_invites.php",
			    			"get_list",invites_to_server.toString(), commondata.facebook_details.email);
					
					String new_invites_data = null;
					if(invites_response != null){
						String[] lists = invites_response.split("%%");
						for(int i =0; i<lists.length; i++){
							if(lists[i].contains(event_reference)){
								// dont add to this list
							}else{
								
									if(new_invites_data == null){
										new_invites_data = lists[i];
									}else{
										new_invites_data = new_invites_data + "%%"+ lists[i];
									}
									
								
								
							}	
						}
					}else{
						//some error calls
					}
					
					try {
			    		
			    		invites_to_server.put("id", commondata.facebook_details.facebook);
			    		invites_to_server.put("data", new_invites_data);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
					
					System.out.println("updating this data" + new_invites_data);
					invites_response = postData("http://52.8.173.36/metster/handel_invites.php",
			    			"update",invites_to_server.toString(), commondata.facebook_details.email);
				
					System.out.println("in invites on server " + invites_response);
				}
			};
			
			thread.start();
		 
		 //****************
		 
			JSONObject json = new JSONObject(); 
			try {
				json.put("host", commondata.facebook_details.facebook);
				json.put("to_id", host); 
				json.put("payload_type", "invite_accept");
				json.put("event_reference", event_reference);
				json.put("sender_name", commondata.facebook_details.name);
				json.put("payload_message", "sounds great");
		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			System.out.println("in json" + json.toString());
			
			System.out.println("respoding to " + host + "from" + commondata.facebook_details.facebook);
			gcm_send_data(commondata.facebook_details.facebook, host, json.toString());
		
	}
		
	/*
	 * name : on_event_selected
	 * @params : None
	 * @return : void
	 * @desp : This function is called after you select from view events
	 * 			This function pulls data from firebase and stored contents in local
	 * 			member pulling data from firebase also launches view.
	 */
	public void on_event_selected(){
		
		//************* push data to firebase
		final HashMap<String, String> fb_data = new HashMap<String, String>();
		//************* get hosted event count
		//************** get event name from user
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Event Name");
		alert.setMessage("Event name helps your friends to identify");
		//************** check how many events you have hosted.
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  Editable value = input.getText();
		  String event_name = value.toString();
		  fb_data.put("nodename",commondata.facebook_details.name );
			fb_data.put("eventname",event_name);
			fb_data.put("Latitude", commondata.user_information.latitude.toString());
			fb_data.put("Longitude", commondata.user_information.longitude.toString());
			fb_data.put("price", Float.toString(commondata.prefrences.price));
			fb_data.put("travel", Double.toString(commondata.prefrences.travel));
			fb_data.put("food", commondata.prefrences.food);
			fb_data.put("nodetype", "host");
			create_firebase_refrence();
			//************ pull data from local db
			SharedPreferences prefs = getSharedPreferences("myevents", MODE_PRIVATE); 
			String hostedevents = prefs.getString("hostedevents", null);
			int number_of_hosted_events = 0;
			String []events = null;
			if (hostedevents != null) {
			  String hostedeventlist = prefs.getString("hostedevents", "None");//"No name defined" is the default value.
			  events = hostedeventlist.split("<<");
			  number_of_hosted_events = events.length+1;
			}
			final String eventrefrence = commondata.facebook_details.facebook+"-->"+"event"+"-->"+ number_of_hosted_events;
			fb_event_ref.firebaseobj.child(eventrefrence).child(commondata.facebook_details.facebook).setValue(fb_data);
			//************** get data from firebase and update local
			pull_data_from_firebase(eventrefrence);
			//************** store this new event copy to local data base
			SharedPreferences.Editor editor = getSharedPreferences("myevents", MODE_PRIVATE).edit();
			 editor.putString("hostedevents", hostedevents +"<<"+eventrefrence);
			 editor.commit();
			//************** prepare for launch
			remove_location_listners();
			Intent intent = new Intent(Login.this, Login.class);
			startActivity(intent);
			finish();
		  }
		});
		alert.show();
	
	}

	/*
	 * name : Create_A_New_Event
	 * @params : View view
	 * @return : null
	 * @desp : This function is triggered after clicking on create event button
	 * 		   The new event will be created if no event exists
	 */
	
	public void Create_A_New_Event(View V) {

			create_event_notification();
		
	}
	
	
	/*
	 * name : Add_Friends
	 * @params : View view
	 * @return : null
	 * @desp : This function is triggered after clicking on add friends button
	 * 		   new friends will be added if you are in a event 
	 */
	
	public void Add_Friends(View V){
		if (commondata.event_information.eventID == null){
			toast_info("Please join or create an event to add friends.");
		} else {
			list_friend();
		}
	}
	
	/*
	 *  name : Meet_Up
	 *  @params : View V
	 *  @return : null
	 *  @desp : This function computes the ranked list for the group
	 *  		add lists meetup places
	 */
		
	public void Meet_Up(View V){
		if (commondata.event_information.eventID == null){
			toast_info("Please join or create an event to add friends.");
		} else {
			if (commondata.event_information.eventID != null) {
				System.out.println("long pressed");
				toast_info("finding a meetup place...");

				mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000,
						null);

				/*
				 * remove if mp data on firebase exist
				 */

				//fb_event_ref.firebaseobj.child("70909141991*799--center")
					//	.removeValue();
				/*
				 * execute the get convience point algorithm on sever
				 */
				
				
				new get_list().execute("");
				
			}
		}
	}
	
	
	
	
	
	/*
	 * name : pick_food_type
	 * @params : None
	 * @return : void
	 * @desp : This function prompts a dialog for user to pick food type and the same is set in firebase.
	 */

	public String pick_food_type() {

		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items = { " Chinese ", " Coffee ", " American ",
				"Sea Food", " Pizza ", " Asian ", " Japanese ", " Mexican ",
				" Italian ", " Indian", "Ice Cream" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select food type");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							commondata.prefrences.food = "chinese";
							
							on_event_selected();

							break;
						case 1:
							// Your code when 2nd option seletced
							commondata.prefrences.food = "coffee";
							on_event_selected();
							
							break;
						case 2:
							// Your code when 3rd option seletced
							commondata.prefrences.food = "american";
							on_event_selected();
							
							break;
						case 3:
							// Your code when 4th option seletced
							commondata.prefrences.food = "seafood";
							on_event_selected();
							
							break;
						case 4:
							// Your code when first option seletced
							commondata.prefrences.food = "pizza";
							on_event_selected();
						
							break;
						case 5:
							commondata.prefrences.food = "asian";
							
							on_event_selected();
							// Your code when 2nd option seletced
							break;
						case 6:
							commondata.prefrences.food = "japanese";
							on_event_selected();
					
							// Your code when 3rd option seletced
							break;
						case 7:
							commondata.prefrences.food = "mexican";
							on_event_selected();
							
							// Your code when 4th option seletced
							break;
						case 8:
							commondata.prefrences.food = "italian";
							on_event_selected();
							
							// Your code when first option seletced
							break;
						case 9:
							commondata.prefrences.food = "indian";
							on_event_selected();
						
							// Your code when 2nd option seletced
							break;
						case 10:
							commondata.prefrences.food = "icecream";
							on_event_selected();
						
							// Your code when 3rd option seletced
							break;
						default:
							commondata.prefrences.food = "american";
							on_event_selected();
							
							break;

						}
						levelDialog.dismiss();
					}
				});
		levelDialog = builder.create();
		levelDialog.show();
		return commondata.prefrences.food;
	}

	
	/*
	 * name : gcm_send_data
	 * @params : facebook_id, to_facebook_id, message
	 * @return : server response
	 * @desp : This function takes care of sending gcm messages
	 *         Still need to handle server response
	 */
	
	public String gcm_send_data(String facebook_id, final String to_facebook_id, final String payload){
	
	   
		Thread thread = new Thread() {
			String server_resp;
			String response = "error";
			@Override
			public void run() {
				Looper.prepare();
				try {
					Thread thread = new Thread() {
					    @Override
					    public void run() {
					    	response = postData("http://52.8.173.36/metster/send_gcm_message.php", commondata.facebook_details.facebook, to_facebook_id, payload);
					    	System.out.println("gcm_server : " + server_resp);
					    }
					};
					thread.start();
					thread.join();
					if (server_resp.contains("doesnot-exist")) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								toast_info(contact_info.contact_name
										+ " seems to not have a Metster account!!");
							}
						});

					} else {
						int offst = server_resp.indexOf("success");

						char response_of_gcm = server_resp
								.charAt(offst + 9);
						if (response_of_gcm == '1') {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									toast_info("invite sent");
								}
							});

						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									toast_info("We encountered some error while adding this person!!");
								}
							});

						}
					}
					System.out.println(server_resp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		};
		
		thread.start();
		return "done";

	}
	
	
	/*
	 * name : list_friends
	 * @params : None
	 * @return : void
	 * @desp : This function lists all Facebook friends and send invite upon click.
	 */
	private void list_friends() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add a friend");
		ListView modeList = new ListView(this);
		final ArrayList<String> friendname = new ArrayList<String>();
		final ArrayList<String> friendid = new ArrayList<String>();
		friendname.clear();
		friendid.clear();
		JSONArray frnd_list = commondata.facebook_details.friends;
		for (int i = 0; i < frnd_list.length(); i++) {

			JSONObject json_data = null;
			try {
				json_data = frnd_list.getJSONObject(i);
				friendname.add(json_data.get("name").toString());
				friendid.add(json_data.get("id").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				friendname);
		modeList.setAdapter(modeAdapter);
		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int it = arg2;
				final String name = arg0.getItemAtPosition(arg2).toString();
				// TODO Auto-generated method stub	
				
				JSONObject payload = new JSONObject(); 
				
				try {
					payload.put("host", commondata.facebook_details.facebook); 
					payload.put("to_id", commondata.facebook_details.facebook);// need to fetch and send
					payload.put("payload_type", "invite_check");
					payload.put("payload_message", "dinner tonight");
					payload.put("event_reference", "123456-->event-->0");
					payload.put("event_name", "lunch and discuss");
					payload.put("sender_name", "alan turing");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				//String server_response = gcm_send_data(commondata.facebook_details.facebook, friendid.get(it).toString(), "dinner tonight?");
				String server_response = gcm_send_data(commondata.facebook_details.facebook, commondata.facebook_details.facebook, payload.toString());

			}

		});
		builder.setView(modeList);
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								toast_info("Once members join long press the home key to explore ");
							}
						});
					}
				});
		final Dialog dialog = builder.create();
		
		dialog.show();
	}

	
	/*
	 * name : view_events
	 * @params : View v
	 * @return : void
	 * @desp : This function is called by view button on the map
	 * 			It lists all the events the user is assosiated with
	 * 			currently handle hosted***
	 * 			case a : user picks a event he has hosted (commondata_hosted_table)
	 * 			case b : user picks a event he has joined (commondata_joined_table)
	 * 			case a --> you know the firebase root (facebookid) and eventroot (event-#)
	 * 						parse all the users under that as display, set firebase listern to this
	 */
	
	public void view_events(View v){
		System.out.println("view events");	
		//******* get all the events and put a array list
		final ArrayList<String> eventtitle = new ArrayList<String>();
		eventtitle.clear();
		//************ pull data from local db
		//**** hosted
		SharedPreferences prefs = getSharedPreferences("myevents", MODE_PRIVATE); 
		String hostedevents = prefs.getString("hostedevents", "None");
		String invitedevents = prefs.getString("joinedevents", "None");
		System.out.println("invited listing" + invitedevents);
		System.out.println("hosted listing" + hostedevents);
		
		String newlisting = null;
		
		if(invitedevents != "None"){
			if(invitedevents != null){
				newlisting = invitedevents ;
			}
		}
		
		if(hostedevents != "None"){
			if(hostedevents != null){
				newlisting = newlisting + "<<" + hostedevents;
			}
		}
		if(newlisting == null){
			toast_info("No events to list, please join or create one");
		}else{
			newlisting.replaceAll("null<<", "").replaceAll("<<null<<", "<<");
			pull_host_info(newlisting);
		}
	}
	
	
	
	
	/*
	 * name : list_invites
	 * @params : none
	 * @return : void
	 * @desp : This function list the invites in a dialog.
	 */
	
	public void list_invites(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Invites");
		ListView inviteslist = new ListView(this);
		final ArrayList<String> listeventnames = new ArrayList<String>();
		final ArrayList<String> listhostnames = new ArrayList<String>();
		final ArrayList<String> listeventreferences = new ArrayList<String>();
		listeventnames.clear();
		listhostnames.clear();
		listeventreferences.clear();
		
		String invitedata = commondata.event_information.invites;
		String[] jsoncontents = invitedata.split("%%");
		for(int i = 0; i< jsoncontents.length; i++){
			String jdata = jsoncontents[i];
			System.out.println("contents from ino" + jdata);
			try{
				JSONObject contents = new JSONObject(jdata);
				
				String host_name = contents.getString("from_name");
				String from_id = contents.getString("from_id");
				String status = contents.getString("status");
				String event_name = contents.getString("event_name");
				String event_reference = contents.getString("event_reference");
				if(status.contains("pending")){
					listhostnames.add(host_name);
					listeventnames.add(event_name);
					listeventreferences.add(event_reference);
				}
			}catch(Exception e){
				System.out.println("json decode exception " + e);
			}
		}
		// listeventsnames insert
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.invitelist,
				(ViewGroup) findViewById(R.id.invitesection));
		
		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.invitelist,
				listeventnames){
		    ViewHolder holder;

		    class ViewHolder {
		        //ImageView icon;
		        TextView title;
		        TextView hostname;
		        ImageButton accept;
		        ImageButton reject;
		    }

		    public View getView(int position, View convertView,
		            final ViewGroup parent) {
		        final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		                .getSystemService(
		                        Context.LAYOUT_INFLATER_SERVICE);

		        if (convertView == null) {
		            convertView = inflater.inflate(
		                    R.layout.invitelist, null);

		            holder = new ViewHolder();
		           // holder.icon = (ImageView) convertView
		            //        .findViewById(R.id.icon);
		            holder.title = (TextView) convertView
		                    .findViewById(R.id.event_title);
		            holder.hostname = (TextView) convertView.findViewById(R.id.host_name);
		            //View v = inflater.inflate( R.layout.invitelist, parent, false);
		            holder.accept = (ImageButton) convertView.findViewById(R.id.buttonaccept);
		            holder.reject = (ImageButton) convertView.findViewById(R.id.buttonreject);
		            holder.accept.setTag(position);
		            holder.reject.setTag(position);
		            holder.accept.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("accepted " + listeventreferences.get(Integer.parseInt(v.getTag().toString())));
							parent.getChildAt(Integer.parseInt(v.getTag().toString())).setBackgroundColor(Color.BLUE);
							// this will store in shared pref
							on_invite_accepted(listeventreferences.get(Integer.parseInt(v.getTag().toString())));
							// On a new thread handle this case
							
							
						}
					}); 
		            holder.reject.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							// TODO Auto-generated method stub
							System.out.println("rejected " + v.getTag().toString());
							parent.getChildAt(Integer.parseInt(v.getTag().toString())).setBackgroundColor(Color.RED);
							on_invite_declined(listeventreferences.get(Integer.parseInt(v.getTag().toString())));
							// On a new thread handle this case
						}
					});
		            convertView.setTag(holder);
		        } else {
		            // view already defined, retrieve view holder
		            holder = (ViewHolder) convertView.getTag();
		        }       
		        System.out.println("setting" + listhostnames.get(position));
		        holder.hostname.setText(listhostnames.get(position));
		        holder.title.setText(listeventnames.get(position));
		       
		        // load from yelp and set async
		        //Drawable drawable = getResources().getDrawable(R.drawable.eventbutton); //this is an image from the drawables folde
		        return convertView;
		    }
		};
		
		
		
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
					}
				});
		final Dialog dialog = builder.create();
		dialog.show();
	}
	
	
	
	/*
	 * name : list_friends
	 * @params : none
	 * @return : void
	 * @desp : This function list the invites in a dialog.
	 */
	
	public void list_friend(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add your friends");
		
		ListView inviteslist = new ListView(this);
		final ArrayList<String> listfirstnames = new ArrayList<String>();
		final ArrayList<String> listids = new ArrayList<String>();
		final ArrayList<Bitmap> listimages = new ArrayList<Bitmap>();
		listfirstnames.clear();
		listids.clear();
		listimages.clear();
		
		// build data
		
		JSONArray frnd_list = commondata.facebook_details.friends;
		for (int i = 0; i < frnd_list.length(); i++) {

			JSONObject json_data = null;
			try {
				json_data = frnd_list.getJSONObject(i);
				listfirstnames.add(json_data.get("name").toString());
				listids.add(json_data.get("id").toString());
				
				Bitmap image = commondata.lazyload.image_ref.get(json_data.get("id").toString());
				listimages.add(image);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// listeventsnames insert
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.friends_list,
				(ViewGroup) findViewById(R.id.friendssection));
		
		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.friends_list,
				listfirstnames){
		    ViewHolder holder;

		    class ViewHolder {
		        //ImageView icon;
		        TextView firstname;
		        TextView lastname;
		        ImageView image;
		        ImageButton invite;
		    }

		    public View getView(int position, View convertView,
		            final ViewGroup parent) {
		        final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		                .getSystemService(
		                        Context.LAYOUT_INFLATER_SERVICE);

		        if (convertView == null) {
		            convertView = inflater.inflate(
		                    R.layout.friends_list, null);

		            holder = new ViewHolder();
		           // holder.icon = (ImageView) convertView
		            //        .findViewById(R.id.icon);
		            holder.firstname = (TextView) convertView
		                    .findViewById(R.id.firstname);
		            holder.lastname = (TextView) convertView.findViewById(R.id.lastname);
		            //View v = inflater.inflate( R.layout.invitelist, parent, false);
		            holder.image = (ImageView) convertView.findViewById(R.id.friendimage);
		            holder.invite = (ImageButton) convertView.findViewById(R.id.friendinvite);
		            
		            
		            holder.invite.setTag(position);
		           
		            
		            holder.invite.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("sendinvite to  " + listids.get(Integer.parseInt(v.getTag().toString())));
							parent.getChildAt(Integer.parseInt(v.getTag().toString())).setBackgroundColor(Color.BLUE);
							// this will store in shared pref
							//on_invite_accepted(listeventreferences.get(Integer.parseInt(v.getTag().toString())));
							// On a new thread handle this case
							
							
							JSONObject payload = new JSONObject(); 
							
							try {
								payload.put("host", commondata.facebook_details.facebook); 
								payload.put("to_id", commondata.facebook_details.facebook);// need to fetch and send
								payload.put("payload_type", "invite_check");
								payload.put("payload_message", "dinner tonight");
								payload.put("event_reference", "123456-->event-->0");
								payload.put("event_name", "lunch and discuss");
								payload.put("sender_name", "alan turing");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							
							//String server_response = gcm_send_data(commondata.facebook_details.facebook, friendid.get(it).toString(), "dinner tonight?");
							String server_response = gcm_send_data(commondata.facebook_details.facebook, commondata.facebook_details.facebook, payload.toString());

							
							
							
							
						}
					}); 
		            
					
		            convertView.setTag(holder);
		        } else {
		            // view already defined, retrieve view holder
		            holder = (ViewHolder) convertView.getTag();
		        }       
		        System.out.println("setting" + listfirstnames.get(position));
		        String[] name = listfirstnames.get(position).split(" ");
		        holder.firstname.setText(name[0]);
		        holder.lastname.setText(name[1]);
		        holder.image.setImageBitmap(listimages.get(position));
		       
		        // load from yelp and set async
		        //Drawable drawable = getResources().getDrawable(R.drawable.eventbutton); //this is an image from the drawables folde
		        return convertView;
		    }
		};
		
		
		
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
					}
				});
		
		final Dialog dialog = builder.create();
		dialog.show();
	}
	
	
	
	
	/*
	 * name : list_rest
	 * @params  : none
	 * @return : void
	 * @desp: This function lists the restraunts in a dialog.
	 */
	public void list_rest() {
		// --------
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.Profiledata);
			View v = (View) findViewById(R.id.Dividerone);
			
			v.setBackgroundColor(Color.parseColor("#2E64FE"));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick your choices");

		ListView modeList = new ListView(this);
		final ArrayList<String> listtitle = new ArrayList<String>();//
		final ArrayList<String> listaddress = new ArrayList<String>();//
		final ArrayList<Double> listrating = new ArrayList<Double>();//
		final ArrayList<String> listreview = new ArrayList<String>();
		final ArrayList<String> listtypes = new ArrayList<String>();
		final ArrayList<String> listreviewscore = new ArrayList<String>();
		final ArrayList<String> listdistance = new ArrayList<String>();
		final ArrayList<Double> listprice = new ArrayList<Double>();
		final ArrayList<String> listtotalratings = new ArrayList<String>();
		final ArrayList<String> listsnippets = new ArrayList<String>();
		final ArrayList<String> dollar = new ArrayList<String>();
		final ArrayList<String> listplace_url = new ArrayList<String>();
		
		listtitle.clear();
		listaddress.clear();
		listrating.clear();
		listreview.clear();
		listtypes.clear();
		listreviewscore.clear();
		listdistance.clear();
		listprice.clear();
		listtotalratings.clear();
		listsnippets.clear();
		listplace_url.clear();
		/*
		 * this section sets up the common data from the server in sorted order
		 */
		System.out.println("conrsnsn" + commondata.places_found.ranking_places.size());
		
		for(Integer rnk = 0; rnk < commondata.places_found.ranking_places.size(); rnk = rnk + 1){
			String current_place_refrence = commondata.places_found.ranking_places.get(rnk);
			place_details node = commondata.places_found.ranking_nodes.get(current_place_refrence);
			listtitle.add(node.place_name);
			listaddress.add(node.address);
			listrating.add(node.rating);
			listtotalratings.add(node.total_ratings);
			listprice.add(node.price_level);
			listtypes.add(node.types);
			listsnippets.add(node.snippet);
			listplace_url.add(node.website);
			
		}
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.listdisplay,
				(ViewGroup) findViewById(R.id.placesection));
		
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.listdisplay,
				listtitle){
		    ViewHolder holder;
		    //Drawable icon;

		    class ViewHolder {
		        //ImageView icon;
		        TextView title;
		        TextView placeaddress;
		        TextView placereviews;
		        RatingBar placeratings;
		        TextView placetype;
		        TextView snippet;
		        
		    }

		    public View getView(int position, View convertView,
		            ViewGroup parent) {
		        final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		                .getSystemService(
		                        Context.LAYOUT_INFLATER_SERVICE);

		        if (convertView == null) {
		            convertView = inflater.inflate(
		                    R.layout.listdisplay, null);

		            holder = new ViewHolder();
		           // holder.icon = (ImageView) convertView
		            //        .findViewById(R.id.icon);
		            holder.title = (TextView) convertView
		                    .findViewById(R.id.title);
		            holder.placeaddress = (TextView) convertView.findViewById(R.id.placeaddress);
		            holder.placereviews = (TextView) convertView.findViewById(R.id.placereviews);
		            holder.placeratings = (RatingBar) convertView.findViewById(R.id.placeratings);
		            holder.placetype = (TextView) convertView.findViewById(R.id.placetype);
		            holder.snippet = (TextView) convertView.findViewById(R.id.snippet);
		            convertView.setTag(holder);
		        } else {
		            // view already defined, retrieve view holder
		            holder = (ViewHolder) convertView.getTag();
		        }       

		        // load from yelp and set async
		        Drawable drawable = getResources().getDrawable(R.drawable.eventbutton); //this is an image from the drawables folder

		        holder.title.setText(listtitle.get(position));
		        String address = listaddress.get(position);
		        address = address.replaceAll("u", "").replaceAll("'", "");
		        holder.placeaddress.setText(address);
		        holder.placeratings.setRating(listrating.get(position).floatValue());
		        holder.placetype.setText(listtypes.get(position));
		        holder.snippet.setText(listsnippets.get(position));
		        System.out.println("types " + listtypes.get(position));
		        // set up $
		        for(Double i = 0.0; i<listprice.get(position);i=i+1.0){
		        	dollar.add("$");
		        }
		        String dollarsign = dollar.toString().replace("[", "").replace("]", "").replace(",", "");
		        if(listtotalratings.get(position) == "null"){
		        	holder.placereviews.setText(" no reviews   "+ " " + dollarsign);
		        }else{
		        holder.placereviews.setText(listtotalratings.get(position) +" user reviews   "+ " " + dollarsign);
		        }
		        //holder.icon.setImageDrawable(drawable);
		        dollar.clear();
		        return convertView;
		    }
		};
		modeList.setAdapter(modeAdapter);
		
		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> items, View arg1, int order,
					long arg3) {
				
				System.out.println("held" + order);
				String link = listplace_url.get(order);
				if(link != "null"){
					/*
					webView = (WebView) findViewById(R.id.webView1);
				    webView.getSettings().setJavaScriptEnabled(true);
				    webView.loadUrl(link);
					*/
				Uri url = Uri.parse(link);
		        Intent intent = new Intent(Intent.ACTION_VIEW, url);
		        startActivity(intent);
				}
				
				/*
				System.out.println("selected " + items.getItemIdAtPosition(order)); // order is also traced to rank
				
				create_firebase_refrence();
				
				double rank = (double)items.getItemIdAtPosition(order); // we have to select item of that rank
				
				String current_place_refrence = commondata.places_found.ranking_places.get(rank);
				final place_details node = commondata.places_found.ranking_nodes.get(current_place_refrence);
				
				
				AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
    			alert.setTitle(node.place_name);
    			alert.setMessage("Do you wish to add this place?");
    			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				
    			  }
    			});

    			alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int whichButton) {
    				  
    					fb_event_ref.firebaseobj
    					.child("rest*0--"
    							+ node.place_name.replace(".", ""))
    					.child("Latitude")
    					.setValue(node.latitude);
    					fb_event_ref.firebaseobj
    					.child("rest*0--"
    							+ node.place_name.replace(".", ""))
    					.child("Longitude")
    					.setValue(node.longitude);
    				  
    				  
    			  }
    			});
    			alert.show();
				*/
			
			}

		});
		
		
		builder.setView(modeList);
		builder.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
					}
				});
		final Dialog dialog = builder.create();
		dialog.show();

	}

	private void finalize_place(String name) {

		for (int i = 0; i < commondata.places_found.names.size(); i++) {
			String temp = commondata.places_found.names.get(i);
			if (temp.contains(name)) {
				Double latitude = commondata.places_found.latitudes.get(i);
				Double longitude = commondata.places_found.longitudes.get(i);
				System.out.println("updating" + name);
				create_firebase_refrence();

				fb_event_ref.firebaseobj
						.child("final*0--" + name.replace(".", ""))
						.child("Latitude").setValue(latitude);
				fb_event_ref.firebaseobj
						.child("final*0--" + name.replace(".", ""))
						.child("Longitude").setValue(longitude);

				fb_event_ref.firebaseobj.child(
						"rest*0--" + name.replace(".", "")).removeValue();
			}
		}
	}

	public void create_firebase_refrence() {
		StringBuilder strBuilder = new StringBuilder(
				"https://met-ster-event.firebaseio.com/");
		strBuilder.append(commondata.facebook_details.facebook);
		fb_event_ref.fbref = strBuilder.toString();
		fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
	}

	private void drop_event() {
		if (commondata.event_information.eventID != null) {
			try {
				locationManager.removeUpdates(locationListener);
			} catch (Exception e) {

			}

			remove_firebase_listners();// before dropping event stop firebase
										// listners

			StringBuilder strBuildertmp = new StringBuilder(
					"https://met-ster-event.firebaseio.com/");
			StringBuilder strBuilderpref = new StringBuilder(
					"https://met-ster.firebaseio.com/");
			if (event_info.is_host) {
				strBuildertmp.append(commondata.event_information.eventID);
				strBuilderpref.append(commondata.event_information.eventID);
			} else {
				strBuildertmp.append(commondata.event_information.eventID + "/"
						+ commondata.facebook_details.facebook + "--"
						+ commondata.facebook_details.name);
				strBuilderpref.append(commondata.event_information.eventID
						+ "/" + commondata.facebook_details.facebook + "--"
						+ commondata.facebook_details.name);
			}

			String tempref = strBuildertmp.toString();
			String pref = strBuilderpref.toString();
			Firebase tempfb = new Firebase(tempref);
			Firebase tempfbpref = new Firebase(pref);
			tempfb.removeValue();
			tempfbpref.removeValue();
			commondata.event_information.eventID = null;

			Thread thread = new Thread() {
			    @Override
			    public void run() {
			    	postData("http://54.183.113.236/metster/resetevent.php", commondata.facebook_details.facebook,"event-" + commondata.facebook_details.facebook, "none" );
			    }
			};

			thread.start();
 
			Intent intent = new Intent(Login.this, Login.class);
			startActivity(intent);
			finish();
		}
	}
	
	
	/*
	 * name : drop_all_event
	 * @desp : this funtion clears all event
	 */
	
	private void drop_all_event() {
		//******* flush hashmaps
		commondata.event_information.event_hosted_table.clear();
		commondata.event_information.event_joined_table.clear();
		//******* flush local db
		Editor eventsjoinededitor = getApplicationContext().getSharedPreferences("myevents", MODE_PRIVATE).edit();
		eventsjoinededitor.clear();
		eventsjoinededitor.commit();
		//******* clears firebase data
		create_firebase_refrence();
		fb_event_ref.firebaseobj.removeValue();
		try {
			locationManager.removeUpdates(locationListener);
		} catch (Exception e) {
			System.out.println("location manager error while dropping");
		}
		//remove_firebase_listners();// this need to taken care of remove listers before dropping
		//***** Launch
		Intent intent = new Intent(Login.this, Login.class);
		startActivity(intent);
		finish();
	}
	
	
	/*
	 * name : drop_a_event()
	 * @params : event_id
	 * return : none
	 * @desp : this funtion clears the current event stored in the shared pref
	 * 		   and refesh the event 
	 */
	
	
	private void drop_a_event(String eventid){
		
		if(!eventid.contains(commondata.facebook_details.facebook)){ // this is a joined event
			

			Editor eventsstored = getApplicationContext().getSharedPreferences("myevents", MODE_PRIVATE).edit();
			
			//************ remove from local db
			SharedPreferences prefs = getSharedPreferences("myevents", MODE_PRIVATE); 
			String hostedevents = prefs.getString("hostedevents", null);
			String joinedevents = prefs.getString("joinedevents", null);
			String []events = null;
			String[] joinedlist = joinedevents.split("<<");
		
			String stemmed = joinedevents.replaceAll( "<<"+ eventid + "<<", "<<").replaceAll("<<"+ eventid, "").replaceAll("null", "");
			System.out.println("after cleaer" + stemmed);
			eventsstored.clear();
			eventsstored.commit();
			eventsstored.putString("joinedevents", stemmed);
			eventsstored.commit();
			
			//*********** remove from firebase
			
			String[] ids = eventid.split("-->");
			StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
			strBuilder.append(ids[0]);
			String refg = strBuilder.toString();
			Firebase newev = new Firebase(refg);
			
			newev.child(eventid).child(commondata.facebook_details.facebook).removeValue();
			
			//********************************
			
			
			
			
			Intent intent = new Intent(Login.this, Login.class);
			startActivity(intent);
			finish();
			
			
		}else{ // its a hosted event
			
		}
		
	
	
		
		
		/*
		 *  first check which event has to be dropped
		 *  case hosted event : 
		 *  		if hosted event has to dropped then all joined memeber has to be notified
		 *  		on gcm notified clear their shared pref if they had accepted the invite
		 *  
		 *  	NOTE: in accept or reject events first check if that event exists
		 *  
		 *  
		 *  case joined event :
		 *  	clear you shared pref, drop from firebase and notify the host
		 *  
		 *   
		 */
		
		
		/*
		if (hostedevents != null) {
		  String hostedeventlist = prefs.getString("hostedevents", "None");//"No name defined" is the default value.
		  events = hostedeventlist.split("<<");
		  number_of_hosted_events = events.length+1;
		}
		*/
		
		
	}
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private void infowindow() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.infodialog,
				(ViewGroup) findViewById(R.id.new_info_root));
		final View lay = layout;
		AlertDialog.Builder alert = new AlertDialog.Builder(this)
				.setView(layout);
		alert.create();
		alert.show();
		Button next = (Button) layout.findViewById(R.id.infobutton);
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageView imgFp = (ImageView) lay.findViewById(R.id.infoview);
				System.out.println("click" + infocounter);
				infocounter = infocounter + 1;
				switch (infocounter){
				case 1:
					imgFp.setImageResource(R.drawable.infoone);
					break;
				case 2:
					imgFp.setImageResource(R.drawable.infotwo);
					break;
				case 3:
					imgFp.setImageResource(R.drawable.infothree);
					break;
				case 4:
					imgFp.setImageResource(R.drawable.infofour);
					infocounter = 0;
					break;
				default:
					imgFp.setImageResource(R.drawable.infoone);
					infocounter = 0;
					break;
				}
			}
		});
	}
	
	
	
	private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;

	    String facebookid;
	    
	    public ImageDownloaderTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    @Override
	    protected Bitmap doInBackground(String... params) {
	        Bitmap himage = null;
	        System.out.println("trying to get image for " + params[0]);
	        facebookid = params[0];
	    	try{
	        URL img_value = new URL("https://graph.facebook.com/" + params[0]
					+ "/picture?type=large");
			himage = BitmapFactory.decodeStream(img_value
					.openConnection().getInputStream());
	    	}catch(Exception e){
	    		
	    	}
			return himage;
	        
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                if (bitmap != null) {
	                    imageView.setImageBitmap(bitmap);
	                    commondata.lazyload.image_ref.put(facebookid, bitmap);
	                } else {
	                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.chooseimage);
	                    imageView.setImageDrawable(placeholder);
	                }
	            }
	        }
	        return ;
	    }
	}
	
	
	
	private class post_req extends AsyncTask<String, Void, String>{
		
		String choice;
		String count_limit;
		
		@Override
		protected String doInBackground(String... params){
			
			System.out.println("param0" + params[0]);
			System.out.println("param1" + params[1]);
			System.out.println("param2" + params[2]);
			choice = params[2];
			count_limit = params[3];
			
			//****************************
		
			// Create a new HttpClient and Post Header
			
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(params[0]);
		    HttpResponse response = null;
		    String responseString = null;
		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("param1", params[1]));
		        nameValuePairs.add(new BasicNameValuePair("param2", params[2]));
		        nameValuePairs.add(new BasicNameValuePair("param3", null));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        response = httpclient.execute(httppost);
		        StatusLine statusLine = response.getStatusLine();
	           
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	                //System.out.println("postData: " + responseString);
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
		        
		        
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    return responseString;
			
			//****************************
	
		}
		
		@Override
        protected void onPostExecute(String result) {
			async_counter +=1;
		
        	System.out.println("pushing " + choice);
        	commondata.server_res.server_says.put(choice, result);
        	if(async_counter.toString() == count_limit){
        		System.out.println("good to list");
        		async_counter = 0;
        		String status = extract_data();
        		
        		list_rest();
        	}
        	
        }

        @Override
        protected void onPreExecute() {
        	
        	//toast_info("Exploring...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        	//toast_info("Exploring...");
        }
		
		
	}
	
	
	private String extract_data(){
		
		Set<String> keys = commondata.server_res.server_says.keySet();
		Iterator<String> choice_iterator = keys.iterator();
		System.out.println("number of sets " + commondata.server_res.server_says.size());
		// this loop through choices.
		Integer rank = 0;
		while(choice_iterator.hasNext()){
			
			String choice = choice_iterator.next();
			String jason_data = commondata.server_res.server_says.get(choice);
			//System.out.println("kkk" + jason_data);
			try{
				JSONObject rest_list = new JSONObject(jason_data);
				System.out.println("kys " + rest_list);
				Iterator<String> places  = rest_list.keys();
				// this loop goes through all places for a given choice
				while(places.hasNext()){
					
					//*****
					Double latitude = null;
 					Double longitude = null;
 					String place_id = places.next();
 					place_details node = new commondata.place_details();
 					Double rating = 0.0;
 					String website = null;
 					String place_name = null;
 					Double price_level = 0.0;
 					String address = null;
 					String contact = null;
 					String total_ratings = null;
 					String types = null;
 					String snippet = null;
 					String image_url = null;
					//*****
					
					
 					String content = rest_list.getString(place_id);
 					JSONObject info_about_place = new  JSONObject(content);
					// fetch the co ordinates.
   				 if(info_about_place.has("coordinate")) {
   					 System.out.println("cor");
   					 String s = info_about_place.getString("coordinate");
   					 String[] ss = s.split(",");
   					 latitude = Double.parseDouble(ss[0].replace("latitude", "").replace("{u'':", "").replace(",", ""));
   					 longitude = Double.parseDouble(ss[1].replace("longitude", "").replace("u'':", "").replace("}", ""));
   					 System.out.println("location" + latitude +", " + longitude);
   				 }
					
   				 if(info_about_place.has("rating")){ rating = Double.parseDouble(info_about_place.getString("rating"));}
				 if(info_about_place.has("review_count")){total_ratings = info_about_place.getString("review_count");}
				 if(info_about_place.has("image_url")){image_url = info_about_place.getString("image_url");}
				 if(info_about_place.has("name")){
					 if(info_about_place.getString("name") != null){
					 place_name = info_about_place.getString("name");
					 node.place_name = place_name;
					 }else{
						 place_name = "place";
						 node.place_name = place_name;
					 }
				
				}
				 
				 if(info_about_place.has("category")){
					 types = info_about_place.getString("category");
					 
					 }
				 if(info_about_place.has("url")){website = info_about_place.getString("url");}
				 if(info_about_place.has("snippet")){snippet = info_about_place.getString("snippet");}
				 if(info_about_place.has("phone")){contact = info_about_place.getString("phone");}
				 if(info_about_place.has("price_level")){price_level = Double.parseDouble(info_about_place.getString("price_level"));}
				 if(info_about_place.has("address")){
					 address = info_about_place.getString("address");
				 }
				 
				 
				// creat a new node and set its value 
 				
					node.rating = rating;
					System.out.println("ratings" + rating);
					node.website = website;
					node.place_name = place_name;
					node.price_level = price_level;
					node.address = address;
					node.contact = contact;
					node.total_ratings = total_ratings;
					node.types = types;     				
					node.latitude = latitude;
					node.longitude = longitude;
					node.snippet =snippet;
					node.image_url = image_url;
					commondata.places_found.ranking_places.put(rank, place_id);
 					commondata.places_found.ranking_nodes.put(place_id, node);
 					rank +=1;
				}
				//rank_refrence += commondata.places_found.ranking_places.size()+1;
			}catch(Exception e){
				System.out.println("exception" + e);
			}

    		 }
	
		 //list_rest();
		return "done"; 
		 //display_node_data();
		
	}
	
	
	private class get_list extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
        	System.out.println("finding the meet up point");
        	 final String ranked_list = null;
        	 ArrayList<String> results = new ArrayList<String>();
        	 String eventres = commondata.event_information.eventID;
        	 eventres = eventres.replaceAll("-->", "--");
        	 
        	
        	 
        	//************** this segment handles food choices.
        	 ArrayList<host_event_node> looks = commondata.event_information.given_events_lookup.get(commondata.event_information.eventID);
        	 Iterator<host_event_node> per = looks.iterator();
        	 HashMap<String, Integer> food_keys = new HashMap<String, Integer>();
        	 final HashMap<String, String> map_results = new HashMap<String, String>();
        	 while(per.hasNext()){
        		 host_event_node node = per.next();
        		 System.out.println(node.nodetype);
        		 int count = 0;
        		 if(node.nodetype.contains("member") || node.nodetype.contains("host")){
        			 String choice  = node.food_type;
        			 System.out.println("choice is " +  choice);
        			 if(food_keys.containsKey(choice)){
        				 count = food_keys.get(choice);
        				 count = count + 1;
        				 food_keys.put(choice, count);
        			 }else{
        				 food_keys.put(choice, 1);
        			 }
        		 }
        	 }
        	 //************************

        	 Set<String> keys = food_keys.keySet();
        	 Iterator<String> food_iter = keys.iterator();
        	 while(food_iter.hasNext()){
        		 final String choice  = food_iter.next();
        		 final String eventid = commondata.event_information.eventID.replaceAll("-->", "--");
        		 

 					    	String list;
 					    	System.out.println("posting for" + choice);
 					    	
 					    	new post_req().execute("http://52.8.173.36/metster/exe_yelp.php", eventid, choice, Integer.toString(keys.size()));	
 			        		 
        	 }
        	 
        	 
        	
            return "Thread issued";
        }

        @Override
        protected void onPostExecute(String result) {
        	 
        	System.out.println("status " + result);
        	
        }

        @Override
        protected void onPreExecute() {
        	
        	
  			View v = (View) findViewById(R.id.Dividerone);
  			v.setBackgroundColor(Color.parseColor("#2EFEC8"));
        	toast_info("Exploring...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        	toast_info("Exploring...");
        }
    }


	
	
	
	/*
	 * name : postData
	 * @params : String url, String, String
	 * @return : String
	 * @desp : This function makes http post request and returns the server response
	 */
	private String postData(String url, String param1, String param2, String param3) {
	
	    // Create a new HttpClient and Post Header
	
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    HttpResponse response = null;
	    String responseString = null;
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("param1", param1));
	        nameValuePairs.add(new BasicNameValuePair("param2", param2));
	        nameValuePairs.add(new BasicNameValuePair("param3", param3));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        response = httpclient.execute(httppost);
	        StatusLine statusLine = response.getStatusLine();
           
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                //System.out.println("postData: " + responseString);
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
	        
	        
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return responseString;
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.login, menu);
		// return true;
		MenuInflater mif = getMenuInflater();
		mif.inflate(R.menu.main_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (commondata.event_information.eventID != null) {
				mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
			}
			return true;

		case R.id.about_icon:
			infowindow();
			return true;

		case R.id.delete_icon:
			//drop_all_event();
			//drop_event();
			if(commondata.event_information.eventID != null){
				
				drop_a_event(commondata.event_information.eventID);
			}
			return true;
		case R.id.settings_icon:
			
			locationManager.removeUpdates(locationListener);
			Intent settingsIntent = new Intent(Login.this, Settings.class);
			startActivity(settingsIntent);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}