package com.example.metster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;

import com.firebase.client.Firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetRestaurantAsyncTask extends AsyncTask<String,Void,ArrayList<Restaurant>>{
	
	public GetRestaurantAsyncTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArrayList<Restaurant> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		try {
			URL url = new URL(arg0[0]);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			int statuscode = con.getResponseCode();
			if(statuscode == HttpURLConnection.HTTP_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				
				while(line!=null){
					sb.append(line);
					line = reader.readLine();
				}
				
				Log.d("demo",sb.toString());
				return RestaurantUtil.RestaurantsJSONParser.parseRestaurants(sb.toString());
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
		//return null;
		
	}
	
	protected void onPostExecute(ArrayList<Restaurant> result) {
		// TODO Auto-generated method stub
		
		super.onPostExecute(result);
		for (int i=0; i< result.size(); i++){
			commondata.places_found.places.add(result.get(i).getName());
			commondata.places_found.latitudes.add(result.get(i).getLatitude());
			Log.w("string",result.get(i).toString());
			commondata.places_found.longitudes.add(result.get(i).getLongitude());
		}
		Log.d("doneman",commondata.places_found.places.get(0));
		Login lg  = new Login();
		lg.list_rest();
	}
	
	


}
