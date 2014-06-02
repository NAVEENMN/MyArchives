package com.example.metster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.appstate.b;

public class ProfilelistActivity extends Activity {
	
	String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
	String accnumber = null;
	Button b;
    ScrollView scrollview;
    String profileimage;
    Bitmap decodedByte ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilelist);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//-----------------------------------
		
		
		//--------------------------------> Setup location
				//---------------------------------------
		        LocationManager locationManager;
		        String provider;
		        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        provider = locationManager.getBestProvider(criteria, false);
		        Location location = locationManager.getLastKnownLocation(provider);
				Double latival = location.getLatitude();
				Double Longival = location.getLongitude();
				String output = null;
				String zip = null;
				Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
				List<Address> addresses;
				try {
		            addresses = gcd.getFromLocation(latival, Longival, 1);
		            zip = addresses.get(0).getPostalCode();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		//-----------------------------------
				//--------------------------------> Read user ID
				//-------------------------------------
						
				        try {
				            FileInputStream inputStream = openFileInput("accounts.txt");

				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                String receiveString = "";
				                StringBuilder stringBuilder = new StringBuilder();

				                while ( (receiveString = bufferedReader.readLine()) != null ) {
				                    stringBuilder.append(receiveString);
				                }

				                inputStream.close();
				                accnumber = stringBuilder.toString();
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
		//-----------------------------------
				try {
					output = new RequestTask().execute("http://www.naveenmn.com/Metster/profilelist.php",accnumber,appkey,zip,Double.toString(latival),Double.toString(Longival), accnumber,accnumber).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//-----------------------------------
				System.out.println("lists");
				
				String[] separated = output.split("#%-->");
				int len = separated.length;
				for(int i = 1; i< len-2 ; i+=3 ){
				if(!(separated[i+1].isEmpty()))	System.out.println(separated[i+2]);
				}
				//i+1 has name i+2 
		
		//------------------------------------------------------
				
				//--------------------------------> Read image from file
				//-------------------------------------
						
				        try {
				            FileInputStream inputStream = openFileInput("image.txt");

				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                String receiveString = "";
				                StringBuilder stringBuilder = new StringBuilder();

				                while ( (receiveString = bufferedReader.readLine()) != null ) {
				                    stringBuilder.append(receiveString);
				                }

				                inputStream.close();
				                profileimage = stringBuilder.toString();
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
				
				        if (profileimage!=null){
		                    byte[] decodedString = Base64.decode(profileimage, Base64.DEFAULT);
		   		              decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		                      }
				        Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(decodedByte, 450, 500, true));
		//--------------------------------> display resuts
				
				scrollview = new ScrollView(this);
		        LinearLayout linearlayout = new LinearLayout(this);
		        linearlayout.setOrientation(LinearLayout.VERTICAL);
		        scrollview.addView(linearlayout);
		        for(int i = 1; i< len-2 ; i+=3)
		        {
		        	//--------------------------------------------------------data area
		        	
		        	//--------------------------------------------------------
		            LinearLayout linear1 = new LinearLayout(this);
		            linear1.setPaddingRelative(10, 50, 60, 70);
		            linear1.setOrientation(LinearLayout.HORIZONTAL);
		            linearlayout.addView(linear1);
		            b = new Button(this);
		            b.setText(separated[i+1]);
		            b.setTextAlignment(getWallpaperDesiredMinimumWidth());
		            b.setWidth(10);
		            b.setBackground(d);
		            b.setId(i);
		            b.setTextSize(10);
		            b.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
		            b.setPaddingRelative(10, 10, 10, 10);
		            //b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		           
		            linear1.addView(b);
		           
		           
		            b.setOnClickListener(new View.OnClickListener() {
		                       
		                        @Override
		                        public void onClick(View v) {
		                              // TODO Auto-generated method stub
		                              Toast.makeText(getApplicationContext(), "Yipee.."+ v.getId(), Toast.LENGTH_SHORT).show();
		                        }
		                  });
		        }
		        this.setContentView(scrollview);
		//-------------------------------------------------		
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profilelist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
