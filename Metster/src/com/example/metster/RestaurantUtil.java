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
				
				//Restaurant restaurant = Restaurant.createRestaurant(restaurantsJsonObject);
				restaurant.setName(restaurantsJsonObject.getString("name"));
				
				Double lon = restaurantsJSONArray.getJSONObject(0)
		                .getJSONObject("geometry").getJSONObject("location")
		                .getDouble("lng");
				Double lat = restaurantsJSONArray.getJSONObject(0)
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

