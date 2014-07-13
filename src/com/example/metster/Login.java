package com.example.metster;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Login extends Activity {
		
	public static class addrs{
	
		static String zip;
		static String cityName;
		static String country;
		static String addressline;
		
	};
	
	public static class User{
		
		static String profileimage;
		static String usrfname;
		static String usrlname;
		static String usrgender;
		static String usrage;
		static String usrageandgender;
		static String usrprofession;
		static String usrworksat;
		static String usrcurrentcity;
		
	};
	
	public static class account{
		static String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
		static String accnumber;
		static String tokennumber;
	};
	
	public static class Map{
		
		static Double latival;
		static Double Longival;
		
	};
	
	public static class Userslist{
		static String server_response;
		static String numberofusers;
		static int user_count;
	};
	//----------------------------------------->
		
	Geocoder gcd ;
	GoogleMap mMap;
	LocationManager locationManager;
	LocationListener locationListener;
	Location location;
	Location position;
	String provider;	
	List<Address> addresses;
	private Button find;
	private final Handler _handler = new Handler();
	Runnable getData;
    Criteria criteria = new Criteria();
    Bundle profilelistactdata = new Bundle();
    //---------------------------------------->
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();// Show the Up button in the action bar.
		setTitle("Metster");
	//--------------------------------> Setup location
	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {	
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    }
    public void onProviderEnabled(String provider) {
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    }
    public void onProviderDisabled(String provider) {}
    };
  	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);// Register the listener with the Location Manager to receive location updates
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if( position == null ){
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        position = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        Map.latival = location.getLatitude();
        Map.Longival = location.getLongitude();
    }
    else{
    	provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        Map.latival = location.getLatitude();
        Map.Longival = location.getLongitude();
    }
    
    getData = new Runnable()
    {
            @Override
            public void run()
            {
                updatelocation();
            }
    };
     
//------------------------------------------------------------------> Fetch the location details
		gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		List<Address> addresses;
		
		try {
            addresses = gcd.getFromLocation(Map.latival, Map.Longival, 1);
            if (addresses.size() > 0)
            addrs.cityName = addresses.get(0).getLocality();
            addrs.country = addresses.get(0).getCountryCode();
            addrs.zip = addresses.get(0).getPostalCode();
            profilelistactdata.putString("zip", addrs.zip);
            addrs.addressline = addresses.get(0).getThoroughfare();
           
        } catch (IOException e) {
            e.printStackTrace();
        }

//--------------------------------> Read Bundle
		
		Bundle b = getIntent().getExtras();
		if( b != null ){
			account.accnumber = b.getString("accountnumber");
			profilelistactdata.putString("accountnumber", account.accnumber);
			account.tokennumber = b.getString("tokennumber");
			User.profileimage = b.getString("userimage");
		}
//-------------------------------------> Read data from server
	    try {
	    	Userslist.server_response = new RequestTask().execute("http://54.183.113.236/metster/fetchprofile.php",account.accnumber,account.tokennumber,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival),(String)addrs.country,"1"
			, "1", "1", "1", "1", "1", "1").get();
			} catch (InterruptedException e) {
							// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
							// TODO Auto-generated catch block
				e.printStackTrace();
			}
	              
	            updatelocation();
	    if(Userslist.server_response.contains("null")){
					Toast.makeText(getApplicationContext(), "Metster is unable to connect to server at this time.", Toast.LENGTH_SHORT).show();
		}
		else{			
					String[] separated = Userslist.server_response.split("-");
		            User.usrfname = separated[0];
		            User.usrlname = separated[1];
		            User.usrgender = separated[2];
		            User.usrage = separated[3];
		            User.usrprofession = separated[4];
		            User.usrworksat = separated[5];
		            User.usrcurrentcity = separated[6];
		            User.usrageandgender = User.usrgender + " | "+User.usrage;
					
		            SetupUIdata();
	//----------------------------------
			if(Userslist.numberofusers.isEmpty()) 
			{
				Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
			}
			else {
				final String[] accountnumbers = Userslist.numberofusers.split("#%-->");
				Userslist.user_count = accountnumbers.length;
				Userslist.user_count --;
			}
					        
//-----------------------------------------------------------------------> Button Actions	            
	            //------------------------------------- meet someone
		        find = (Button) findViewById(R.id.buttonmeet);
		        find.setOnClickListener(new View.OnClickListener() {	
						public void onClick(View v) {
							
							if(Userslist.user_count > 0){
							stopRepeatingTask();
							locationManager.removeUpdates(locationListener);
							Intent intentprofilelist = new Intent( Login.this, ProfilelistActivity.class);
							intentprofilelist.putExtras(profilelistactdata);
			        		startActivity(intentprofilelist);
							}
							else{
								//Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT).show();
							}
							       
					}//on click
					
		        		}
		        );	        
} // else ends here
				
				
	}
	//------------------------------------- update profile
	
	public void SetupUIdata(){
		
//-----------------------------------------------------------> Setup the GUI with data acquired
		//----------- Section 1
		TextView fname = (TextView)findViewById(R.id.FirstName); 
        fname.setText((String)User.usrfname);
        TextView lname = (TextView)findViewById(R.id.LastName); 
        lname.setText((String)User.usrlname);
        TextView prof = (TextView)findViewById(R.id.Profession); 
        prof.setText((String)User.usrprofession);
        TextView wat = (TextView)findViewById(R.id.Worksat); 
        wat.setText((String)User.usrworksat);
        TextView ag = (TextView)findViewById(R.id.AgeandGender); 
        ag.setText((String)User.usrageandgender);
        TextView cc = (TextView)findViewById(R.id.CurrentCity); 
        cc.setText((String)User.usrcurrentcity);
        //----------- Section 2
        TextView loca = (TextView)findViewById(R.id.YourLocation); 
        loca.setText((String)addrs.addressline);
        TextView locacity = (TextView)findViewById(R.id.YourLocationcity); 
        locacity.setText((String)addrs.cityName);
        //----------- Section Profile Image
        if (User.profileimage!=null){
            byte[] decodedString = Base64.decode(User.profileimage, Base64.DEFAULT);
	             Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	             	ImageButton imageButton =(ImageButton)findViewById(R.id.ProfileImage);
	                imageButton.setImageBitmap(decodedByte);
              }
        //----------- Section Maps
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.visitormap)).getMap();

        LatLng currlocation = new LatLng(Map.latival, Map.Longival);// yours

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currlocation, 18));
        //-------------- Section Footer
        String footertext = "Metster \u00a9 2014, Apha Ver1.1";
        TextView foot = (TextView)findViewById(R.id.footer);
        foot.setText(footertext);
//-------------------------------------------------------------------------------------------- 
		
	}
	
	public void updatelocation()
	{
		
		Log.w("called","gaina");
		
		_handler.postDelayed(getData, 3000);

		position = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	provider = locationManager.getBestProvider(criteria, false);
    	location = locationManager.getLastKnownLocation(provider);
    	Map.latival = location.getLatitude();
    	Map.Longival = location.getLongitude();
    	profilelistactdata.putDouble("latitude",Map.latival);
    	profilelistactdata.putDouble("longitude", Map.Longival);
    	
        try {
        	 new RequestTask().execute("http://54.183.113.236/metster/updatedash.php",account.accnumber,account.appkey,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival), "1","1",
					"1", "1", "1", "1", "1", "1").get();
        	 Userslist.numberofusers = new RequestTask().execute("http://54.183.113.236/metster/numberofusers.php",account.accnumber,account.appkey,addrs.zip,Double.toString(Map.latival),Double.toString(Map.Longival), "1","1",
					"1", "1", "1", "1", "1", "1").get();
        	 profilelistactdata.putString("accountnumberlist", Userslist.numberofusers);
		     if(Userslist.numberofusers.isEmpty()) 
				{
					//Toast.makeText(getApplicationContext(), "Oops no metster users around you.", Toast.LENGTH_SHORT).show();
				}
				else {
					final String[] accountnumb = Userslist.numberofusers.split("#%-->");
					Userslist.user_count = accountnumb.length;
					Userslist.user_count --;
				}
        	TextView num = (TextView)findViewById(R.id.NumberofUsers); 
            num.setText(Integer.toString(Userslist.user_count));
            
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}
	
	 

     public void stopRepeatingTask()
     {
    	 _handler.removeCallbacks(getData);
     }
	
	public void updateprofile(View view){
		stopRepeatingTask();
    	Intent intent2 = new Intent( Login.this, UpdateProfile.class);
		startActivity(intent2);
		finish();
    }
	
	@Override
	public void onBackPressed() {
	 
		
		new AlertDialog.Builder(this)
        .setMessage("You will go invisible, Do you wish to exit?")
        .setTitle("Metster")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	stopRepeatingTask();
        		locationManager.removeUpdates(locationListener);
        		Login.this.finish();
            }
        })
        .setNegativeButton("No", null)
        .show();	
	}

	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		//getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.login, menu);
		//return true;
		
		
		MenuInflater mif = getMenuInflater();
		mif.inflate(R.menu.main_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.logout_icon:
			Log.w("this","toad");
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
