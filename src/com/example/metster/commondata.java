package com.example.metster;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class commondata {

	public static class facebook_details{
		static String facebook;
		static String name;
		static String email;
		static Bitmap profile_image;
		static String contact;
	}
	public static class keys{
		static String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
	}
	
	public static class gcm_req{
		static String requester_name;
		static String event_id;
	}
	
	public static class user_information{
		static String profileimage;
		static Double latitude;
		static Double longitude;
		static String cityname;
		static String country;
		static String zip;
		static String addressline;
		static String status;
	}
	
	public static class event_information{
		static String eventID;
	}
		
	public static class places_found{
		static ArrayList<String> places = new ArrayList<String>();
		static ArrayList<Double> latitudes = new ArrayList<Double>();
		static ArrayList<Double> longitudes = new ArrayList<Double>();
	}
	
	public static class gcm_incoming{
		static String message;
	}
	
}
