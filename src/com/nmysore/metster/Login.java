package com.nmysore.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
		if(invite_notification.toString() != "none"){
			String invite_from = invite_notification.getString("invite_from", "none");
			final String invite_id = invite_notification.getString("inviteid", "none");
			String invite_message = invite_notification.getString("message", "none");
			System.out.println("you have a invite from " + invite_from + " " + invite_message);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Invite from " + invite_from);
			alert.setMessage(invite_message);
			alert.setPositiveButton("Accept",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// gcm notify accept
							JSONObject json = new JSONObject(); 
							try {
								json.put("host", commondata.facebook_details.facebook);
								json.put("to_id", invite_id); 
								json.put("payload_type", "invite_accept"); 
								json.put("payload_message", "sounds great");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							gcm_send_data(commondata.facebook_details.facebook, invite_id, json.toString());
						}
					});

			alert.setNegativeButton("Reject",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// gcm notify and join		
							
							gcm_send_data(commondata.facebook_details.facebook, invite_id, commondata.facebook_details.name + "rejected your invite");
							
						}
					});
			alert.setCancelable(false);
			
			alert.show();
			
		}else{
			System.out.println("no invite exists");
		}
		
		
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
		
		SharedPreferences eventshosted = getApplicationContext().getSharedPreferences("eventhosted", MODE_PRIVATE);
		SharedPreferences eventsjoined = getApplicationContext().getSharedPreferences("eventjoined", MODE_PRIVATE);
		
		if(eventsjoined != null){
			Map<String,?> joinedkeys = eventsjoined.getAll();
			for(Map.Entry<String,?> entry : joinedkeys.entrySet()){
				String event_id = entry.getKey();
	            String event_name = entry.getValue().toString();
	            commondata.event_information.event_joined_table.put(event_id, event_name);
			}
		}else{
			
		}
		
		
		Map<String,?> hostedkeys = eventshosted.getAll();
		if(eventshosted != null){// means the user has hosted event/events
			for(Map.Entry<String,?> entry : hostedkeys.entrySet()){
				 String event_id = entry.getKey();
				 String event_name = entry.getValue().toString();
				 commondata.event_information.event_hosted_table.put(event_id, event_name);
			}
		}
		
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
			System.out.println("case 0");
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
			/*
			 * setup the listeners this listens to child modification events
			 */
			fb_event_ref.firebaseobj
					.addChildEventListener(child_listner = new ChildEventListener() {

						@Override
						public void onCancelled(FirebaseError arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onChildAdded(DataSnapshot arg0, String arg1) {
							// TODO Auto-generated method stub
							System.out.println("childadded"
									+ arg0.getName().toString());
							
						}

						@Override
						public void onChildChanged(DataSnapshot arg0,
								String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onChildMoved(DataSnapshot arg0, String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onChildRemoved(DataSnapshot child) {
							// TODO Auto-generated method stub

							System.out.println("changed" + child.getName());
							String rawname = child.getName();
							String[] name = rawname.split("--");// name[0] will
																// have id and
																// name[1] will
							boolean stat = false; // have name
							try {
								stat = commondata.event_information.eventID
										.contains(name[0]);
							} catch (Exception e) {
								drop_event();
							}
							if (stat) {// host has left
								/*
								 * host has left safely drop the event
								 */
								drop_event();
							}

						}

					});

			/*
			 * value listeners this listens to child value modifications
			 */

			fb_event_ref.firebaseobj
					.addValueEventListener(listn = new ValueEventListener() {
						@Override
						public void onCancelled(FirebaseError arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onDataChange(DataSnapshot data) {

							if (data.hasChildren()) {// members are present
								// TODO Auto-generated method stub
								System.out
										.println("something child value changed");
								/*
								 * clear all values and recapture the values
								 */
								commondata.places_found.latitudes.clear();
								commondata.places_found.longitudes.clear();
								commondata.places_found.names.clear();
								commondata.places_found.tokens.clear();
								Iterator<DataSnapshot> children = data
										.getChildren().iterator();
								commondata.event_information.host = null;
								while (children.hasNext()) {// handle cases here
															// like if no
															// latitude etc
									DataSnapshot child = children.next();
									String rawname = child.getName();
									String[] rawdata = rawname.split("--"); // rawdata[0]
																			// has
																			// id
																			// and
																			// rawdata[1]
																			// has
																			// name
									if (commondata.event_information.eventID
											.contains(rawdata[0])) {// then he
																	// is the
																	// host
										commondata.event_information.host = rawdata[1];
										setTitle("(host)"
												+ commondata.event_information.host);
									}
									try {
										Iterable<DataSnapshot> kid = child
												.getChildren();
										Iterator<DataSnapshot> ki = kid
												.iterator();
										while (ki.hasNext()) {
											DataSnapshot par = ki.next();
											if (par.getName().contains(
													"Latitude"))
												commondata.places_found.latitudes.add(Double
														.parseDouble(par
																.getValue()
																.toString()));
											if (par.getName().contains(
													"Longitude"))
												commondata.places_found.longitudes.add(Double
														.parseDouble(par
																.getValue()
																.toString()));
										}

										String restrauntname = rawdata[1]
												.replace(".", "")
												.replace("#", "")
												.replace("$", "")
												.replace("[", "")
												.replace("]", "");
										commondata.places_found.names
												.add(restrauntname);
										commondata.places_found.tokens
												.add(rawdata[0]);
									} catch (Exception e) {
										System.out.println("pair error");
									}
								}
								// after the loop if host is still null then
								// host has left
								/*
								 * update map only if we have correct pair of
								 * latitude and longitude
								 */
								if (commondata.places_found.latitudes.size() != 0
										&& commondata.places_found.longitudes
												.size() != 0) {
									if (commondata.places_found.latitudes
											.size() == commondata.places_found.longitudes
											.size()) {
										set_up_map_view();
									}
								}
							} else {// no members -- clear mysql reset eventid
									// and refresh

								commondata.event_information.eventID = null;
								/*
								 * reset the event
								 */
								Thread thread = new Thread() {
								    @Override
								    public void run() {
								    	postData("http://54.183.113.236/metster/resetevent.php", commondata.facebook_details.facebook,"event-" + commondata.facebook_details.facebook, "none" );
								    }
								};

								thread.start();
								Intent intent = new Intent(Login.this,
										Login.class);
								startActivity(intent);
								finish();
							}
						}

					});

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
					System.out.println("setting up : " + commondata.places_found.latitudes.get(i) + ", " + commondata.places_found.longitudes.get(i) );
					if (commondata.places_found.tokens.get(i).contains("final")) {
						mMap.addMarker(new MarkerOptions()
								.position(
										new LatLng(
												commondata.places_found.latitudes
														.get(i),
												commondata.places_found.longitudes
														.get(i)))

								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.finalpoint))
								.title(commondata.places_found.names.get(i)));
					} else {

						if (commondata.places_found.tokens.get(i).contains(
								"rest")) {// they
											// are
											// restraunt
							mMap.addMarker(new MarkerOptions()
									.position(
											new LatLng(
													commondata.places_found.latitudes
															.get(i),
													commondata.places_found.longitudes
															.get(i)))

									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.flag))
									.title(commondata.places_found.names.get(i)));
							
							
							if (event_info.is_host) {
								mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker arg0) {
										// TODO Auto-generated method stub
										AlertDialog.Builder alert = new AlertDialog.Builder(
												Login.this);
										final Marker picked = arg0;
										System.out.println(arg0.getTitle());
										alert.setTitle("Finalize Meet Up");
										alert.setMessage("Do you want to finalize "
												+ arg0.getTitle()
												+ " as meetup area ?");
										// Set an EditText view to get user
										// input

										alert.setPositiveButton(
												"Ok",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

														finalize_place(picked
																.getTitle()
																.toString());
													}
												});

										alert.setNegativeButton(
												"Cancel",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														// just cancel it
													}
												});

										alert.show();

									}
								});
							}
						} else {

							if (commondata.places_found.names.get(i).equals(
									"center")) {// for
												// center
												// button
								final int j = i;
								mMap.addMarker(new MarkerOptions()
										.position(
												new LatLng(
														commondata.places_found.latitudes
																.get(i),
														commondata.places_found.longitudes
																.get(i)))
										// visitor
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.mp))
										.title("explore neighbourhood"));


							} else {
								mMap.addMarker(new MarkerOptions()
										.position(
												new LatLng(
														commondata.places_found.latitudes
																.get(i),
														commondata.places_found.longitudes
																.get(i)))
										// visitor
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.pin))
										.title(commondata.places_found.names
												.get(i)));
							}
						}
					}
				}

				mMap.setMyLocationEnabled(true);
				LatLng currlocation = new LatLng(
						commondata.user_information.latitude,
						commondata.user_information.longitude);// yours
				mMap.getUiSettings().setZoomControlsEnabled(false);
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
	 * name : on_event_selected
	 */

	public void on_event_selected(){
	
		HashMap<String, String> fb_data = new HashMap<String, String>();
		int number_of_hosted_events = commondata.event_information.event_hosted_table.size()+1;
		fb_data.put("username",commondata.facebook_details.name );
		fb_data.put("Latitude", commondata.user_information.latitude.toString());
		fb_data.put("Longitude", commondata.user_information.longitude.toString());
		fb_data.put("price", Float.toString(commondata.prefrences.price));
		fb_data.put("travel", Double.toString(commondata.prefrences.travel));
		fb_data.put("food", commondata.prefrences.food);
		create_firebase_refrence();
		fb_event_ref.firebaseobj.child("event-" + Integer.toString(number_of_hosted_events)).child(commondata.facebook_details.facebook).setValue(fb_data);
		
		Thread thread = new Thread() {
		    @Override
		    public void run() {
		    	postData("http://54.183.113.236/metster/updateevent.php", commondata.facebook_details.facebook,"event-" + commondata.facebook_details.facebook, "none" );
		    }
		};

		thread.start();

		remove_location_listners();
		Intent intent = new Intent(Login.this, Login.class);
		startActivity(intent);
		finish();
	
	}

	/*
	 * name : Create_A_New_Event
	 * @params : View view
	 * @return : null
	 * @desp : This function is triggered after clicking on create event button
	 * 		   The new event will be created if no event exists
	 */
	
	public void Create_A_New_Event(View V) {
		if (commondata.event_information.eventID == null) {// no event exist
			create_event_notification();
		} else{
			toast_info("You are already in an event.");
		}
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
			list_friends();
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

				fb_event_ref.firebaseobj.child("70909141991*799--center")
						.removeValue();
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
	
	public String gcm_send_data(String facebook_id, final String to_facebook_id, final String message){
	
	   
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
					    	response = postData("http://54.183.113.236/metster/exe_gcm_send_test.php", commondata.facebook_details.facebook, to_facebook_id, message);
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
				String server_response = gcm_send_data(commondata.facebook_details.facebook, friendid.get(it).toString(), "dinner tonight?");

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

	public void list_rest() {
		// --------
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
		final ArrayList<String> dollar = new ArrayList<String>();
		
		listtitle.clear();
		listaddress.clear();
		listrating.clear();
		listreview.clear();
		listtypes.clear();
		listreviewscore.clear();
		listdistance.clear();
		listprice.clear();
		listtotalratings.clear();
		/*
		 * this section sets up the common data from the server in sorted order
		 */
		for(Double rnk = 0.0; rnk < commondata.places_found.ranking_places.size(); rnk = rnk + 1.0){
			String current_place_refrence = commondata.places_found.ranking_places.get(rnk);
			place_details node = commondata.places_found.ranking_nodes.get(current_place_refrence);
			listtitle.add(node.place_name);
			listaddress.add(node.address);
			listrating.add(node.rating);
			listtotalratings.add(node.total_ratings);
			listprice.add(node.price_level);
			listtypes.add(node.types);
		}
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.listdisplay,
				(ViewGroup) findViewById(R.id.placesection));
		
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.listdisplay,
				listtitle){
		    ViewHolder holder;
		    Drawable icon;

		    class ViewHolder {
		        //ImageView icon;
		        TextView title;
		        TextView placeaddress;
		        TextView placereviews;
		        RatingBar placeratings;
		        TextView placetype;
		        
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
		            convertView.setTag(holder);
		        } else {
		            // view already defined, retrieve view holder
		            holder = (ViewHolder) convertView.getTag();
		        }       

		        Drawable drawable = getResources().getDrawable(R.drawable.flag); //this is an image from the drawables folder

		        holder.title.setText(listtitle.get(position));
		        holder.placeaddress.setText(listaddress.get(position));
		        holder.placeratings.setRating(listrating.get(position).floatValue());
		        holder.placetype.setText(listtypes.get(position));
		        
		        // set up $
		        for(Double i = 0.0; i<listprice.get(position);i=i+1.0){
		        	dollar.add("$");
		        }
		        String dollarsign = dollar.toString().replace("[", "").replace("]", "").replace(",", "");
		        holder.placereviews.setText(listtotalratings.get(position) +" google reviews   "+ " " + dollarsign);
		       // holder.icon.setImageDrawable(drawable);
		        dollar.clear();
		        return convertView;
		    }
		};
		modeList.setAdapter(modeAdapter);
		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> items, View arg1, int order,
					long arg3) {
				
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
	
	private class get_list extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            
        	
        	System.out.println("finding the meet up point");
    		String ranked_list = postData("http://54.183.113.236/metster/exe_get_loc.php", commondata.event_information.eventID,
    			"event-"+ commondata.facebook_details.facebook, "none");
    		try {
				JSONObject rest_list = new JSONObject(ranked_list);
				Iterator<String> places  = rest_list.keys();
				
				/*
				 * This section reads the json data responded from the server
				 * the data is in the form of place name as key and location and rank as values
				 * example : place_ref : [[15.7637, -80.896987],[2.0]]
				 */
				
				while(places.hasNext()){
					String place_refrence;
					Double latitude = null;
					Double longitude = null;
					Double rank = null;
					String id = null;
					String place = places.next();
					place_refrence = place;
					String values = rest_list.getString(place_refrence);
					
					Double rating = 0.0;
					String website = null;
					String place_name = null;
					Double price_level = 0.0;
					String address = null;
					String contact = null;
					String location = null;
					String total_ratings = null;
					String types = null;
					
					JSONObject dats = new JSONObject(values);
					
					if (dats.has("rating")) rating = Double.parseDouble(dats.getString("rating"));
					if (dats.has("website")) website = dats.getString("website");
					if (dats.has("name")) place_name = dats.getString("name");
					if (dats.has("price_level")) price_level = Double.parseDouble(dats.getString("price_level"));
					if (dats.has("formatted_address")) address = dats.getString("formatted_address");
					if (dats.has("international_phone_number")) contact = dats.getString("international_phone_number");
					if (dats.has("location")) location = dats.getString("location");
					if (dats.has("user_ratings_total")) total_ratings= dats.getString("user_ratings_total");
					if (dats.has("types")) types = dats.getString("types");
					rank = Double.parseDouble(dats.getString("rank"));
					
					place_details node = new commondata.place_details();
					node.rating = rating;
					node.website = website;
					node.place_name = place_name;
					node.price_level = price_level;
					node.address = address;
					node.contact = contact;
					node.total_ratings = total_ratings;
					node.types = types;
					
					// location is in this format [lat, lon] in string, bring it to double so we can use it
					String[] tempclean = location.split(",");
					System.out.println("lat " + tempclean[0]);
					System.out.println("lon"+ tempclean[1]);
					node.latitude = Double.parseDouble(tempclean[0].replace("[", ""));
					node.longitude = Double.parseDouble(tempclean[1].replace("]", ""));
					commondata.places_found.ranking_places.put(rank, place_refrence);
					commondata.places_found.ranking_nodes.put(place_refrence, node);
					
					/*
					 * The node structure is stored in this format
					 * KEY : rank ; Value : place_refrence
					 * KEY : Place_refrence ; Value : Node
					 */
					
				}
				
				/*
				 * this section sets up the common data from the server in sorted order
				 */
				for(Double rnk = 0.0; rnk < commondata.places_found.ranking_places.size(); rnk = rnk + 1.0){
					String current_place_refrence = commondata.places_found.ranking_places.get(rnk);
					place_details node = commondata.places_found.ranking_nodes.get(current_place_refrence);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	toast_info(" ");
        	list_rest();
        	
        	
        }

        @Override
        protected void onPreExecute() {
        	
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
	        nameValuePairs.add(new BasicNameValuePair("appkey", param1));
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
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
	        
	        System.out.println("postData: " + responseString);
	        
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
			drop_event();
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