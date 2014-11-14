package com.example.metster;

import java.util.ArrayList;

public class commondata {

	public static class keys{
		static String appkey = "n1a1v2e3e5n8m13y21s34o55r89e";
	}
	
	public static class user_information{
		static String account_number;
		static String token_number;
		static String profileimage;
		static String linkedin;
		static String facebook;
		static Double latitude;
		static Double longitude;
		static String cityname;
		static String country;
		static String zip;
		static String addressline;
		static String firstname;
		static String lastname;
		static String gender;
		static String about;
		static String profession;
		static String worksat;
		static String currentcity;
		static String status;
	}
	
	public static class visitorinfo{
		
		static String profileid;
		static String FirstName;
		static String LastName;
		static String Image;
		static String Gender;
		static String Age;
		static String Status;
		static String Profession;
		static String worksat;
		static String CurrentCity;
		static String hometown;
		static String hobbies;
		static String music;
		static String movies;
		static String books;
		static String AboutMe;
		static String Passion;
		static Double latitude;
		static Double longitude;
		static String facebookurl;
		static String linkedinurl;
		
	};
	
	public static class places_found{
		static ArrayList<String> places = new ArrayList<String>();
		static ArrayList<Double> latitudes = new ArrayList<Double>();
		static ArrayList<Double> longitudes = new ArrayList<Double>();
	}
	
	public static class gcm_incoming{
		static String message;
	}
	
}
