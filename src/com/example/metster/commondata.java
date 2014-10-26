package com.example.metster;

import java.util.ArrayList;

public class commondata {

	public static class user_information{
		static String account_number;
		static String token_number;
		static String profileimage;
		static String usrfname;
		static String usrlname;
		static String usrgender;
		static String usrabout;
		static String usrprofession;
		static String usrworksat;
		static String usrcurrentcity;
		static String usrstatus;
		static String linkedin;
		static String facebook;
		static Double latitude;
		static Double longitude;
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
