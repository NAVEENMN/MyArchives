package com.example.metster;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.facebook.android.Facebook;

public class commondata {

	public static class facebook_details{
		static String facebook;
		static String name;
		static String email;
		static Bitmap profile_image;
		static String contact;
		static Facebook fb;
		static JSONArray friends;
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
	}
	
	public static class event_information{
		static String eventID;
		static String host;
	}
	
	public static class prefrence{
		static SharedPreferences sp;
	}
	
	public static class prefrences{
		static Double travel;
		static float price;
		static int hour;
		static int minute;
		static String food;
	}
		
	public static class places_found{
		static ArrayList<String> places = new ArrayList<String>();
		static ArrayList<Double> latitudes = new ArrayList<Double>();
		static ArrayList<Double> longitudes = new ArrayList<Double>();
		static ArrayList<String> names = new ArrayList<String>();
		static ArrayList<String> tokens = new ArrayList<String>();
	}
	
	public static class gcm_incoming{
		static String message;
	}
	
}
