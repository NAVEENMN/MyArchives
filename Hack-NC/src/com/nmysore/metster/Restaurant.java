package com.nmysore.metster;

import org.json.JSONException;
import org.json.JSONObject;



public class Restaurant {
	String name;
	double latitude;
	double longitude;
	double price;
	double ratings;
	@Override
	public String toString() {
		return "Restaurant [name=" + name + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", ratings=" + ratings + ", price=" + price +"]";
	}

	
	
	
	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public double getLatitude() {
		return latitude;
	}




	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}




	public double getLongitude() {
		return longitude;
	}




	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getratings(){
		return ratings;
	}
	public void setratings(double ratings){
		this.ratings = ratings;
	}
	public double getprice(){
		return price;
	}
	public void setprice(double price){
		this.price = price;
	}


	public static Restaurant createRestaurant(JSONObject js) throws JSONException{
		
		Restaurant restaurant = new Restaurant();
		
		restaurant.setName(js.getString("name"));
		restaurant.setLatitude(js.getDouble("latitude"));
		restaurant.setLongitude(js.getDouble("longitude"));
		restaurant.setratings(js.getDouble("ratings"));
		restaurant.setprice(js.getDouble("price"));
		return restaurant;

}
}

