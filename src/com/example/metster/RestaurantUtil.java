package com.example.metster;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;






public class RestaurantUtil {
	
	static public class RestaurantsJSONParser{
		
		static ArrayList<Restaurant> parseRestaurants(String in) throws JSONException{
			ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
			
			JSONObject root = new JSONObject(in);
			JSONArray restaurantsJSONArray = root.getJSONArray("results");
			
			for(int i=0;i<restaurantsJSONArray.length();i++){
				JSONObject restaurantsJsonObject = restaurantsJSONArray.getJSONObject(i);
				Restaurant restaurant = new Restaurant();
			
				restaurant.setName(restaurantsJsonObject.getString("name"));
				try{
				restaurant.setprice(restaurantsJsonObject.getDouble("price_level"));
				}catch(Exception e){
					restaurant.setprice(0.0);
				}
				try{
				restaurant.setratings(restaurantsJsonObject.getDouble("rating"));
				}catch(Exception e){
					restaurant.setratings(0.0);
				}
				
				Double lon = restaurantsJSONArray.getJSONObject(i)
		                .getJSONObject("geometry").getJSONObject("location")
		                .getDouble("lng");
				Double lat = restaurantsJSONArray.getJSONObject(i)
			                .getJSONObject("geometry").getJSONObject("location")
			                .getDouble("lat");
				restaurant.setLatitude(lat);
				restaurant.setLongitude(lon);
				restaurantList.add(restaurant);
			}
				
		
			return restaurantList;
		}
	}
 

}

