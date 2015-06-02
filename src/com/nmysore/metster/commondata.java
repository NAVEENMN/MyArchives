package com.nmysore.metster;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.facebook.android.Facebook;

public class commondata {

	
	public static class facebook_details{
		static String facebook;
		static String name;
		static String email;
		static Bitmap profile_image;
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
		static HashMap<String, String> event_hosted_table = new HashMap<String, String>();
		static HashMap<String, String> event_joined_table = new HashMap<String, String>();
		static HashMap<String, ArrayList<host_event_node>> given_events_lookup = new HashMap<String, ArrayList<host_event_node>>();
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
		static int year;
		static int month;
		static int date;
		static String event_name;
	}
		
	public static class places_found{
		static ArrayList<String> places = new ArrayList<String>();
		static ArrayList<Double> latitudes = new ArrayList<Double>();
		static ArrayList<Double> longitudes = new ArrayList<Double>();
		static ArrayList<Double> price = new ArrayList<Double>();
		static ArrayList<Double> ratings = new ArrayList<Double>();
		static ArrayList<String> names = new ArrayList<String>();
		static ArrayList<String> tokens = new ArrayList<String>();
		static ArrayList<String> rankedplaces = new ArrayList<String>();
		static ArrayList<Double> rankedlatitudes = new ArrayList<Double>();
		static ArrayList<Double> rankedlongitudes = new ArrayList<Double>();
		static HashMap<Double, String> ranking_places = new HashMap<Double, String>(); // This hashmap links rank as key and place as value
		static HashMap<String, place_details> ranking_nodes = new HashMap<String, place_details>();
	}
	
	public static class gcm_incoming{
		static String message;
	}
	
	public  static class place_details{ 
		Double rating;
		String website;
		String place_name;
		Double price_level;
		String address;
		String contact;
		Double latitude;
		Double longitude;
		String total_ratings;
		String types;
		
	}
	
	// key --> eventid (host-event-#)
	public static class host_event_node{
		String event_name;// what user enters
		String eventid;//fbid-->event-->#
		Long number_of_people;//
		String food_type;
		String price;
		String travel;
		Double Latitude;
		Double Longitude;
		String nodename;// Philz, stanford theater, naveen, allie --> can be used for title on map
		String nodetype;//restaurant, movie place, host, member# --> can be used for icons on map
	}
	
	
	public static class BlurBuilder {
	    private static final float BITMAP_SCALE = 0.4f;
	    private static final float BLUR_RADIUS = 7.5f;

	    public static Bitmap blur(View v) {
	        return blur(v.getContext(), getScreenshot(v));
	    }

	    public static Bitmap blur(Context ctx, Bitmap image) {
	        int width = Math.round(image.getWidth() * BITMAP_SCALE);
	        int height = Math.round(image.getHeight() * BITMAP_SCALE);

	        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
	        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

	        RenderScript rs = RenderScript.create(ctx);
	        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
	        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
	        theIntrinsic.setRadius(BLUR_RADIUS);
	        theIntrinsic.setInput(tmpIn);
	        theIntrinsic.forEach(tmpOut);
	        tmpOut.copyTo(outputBitmap);

	        return outputBitmap;
	    }

	    private static Bitmap getScreenshot(View v) {
	        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
	        Canvas c = new Canvas(b);
	        v.draw(c);
	        return b;
	    }
	}
	
}
