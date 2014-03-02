package com.example.pebblepush;

import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.format.Time;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class Push {
	private boolean isConnected;
	private Time lastConnectCheck;
	private UUID uuid;
	private Context state;
	
	public Push ( Context passedState ) {
		lastConnectCheck = new Time();
		isConnected = checkConnection();
		state = passedState;
		uuid = UUID.randomUUID();
	}
	
	public void sendData ( String str ) {
		PebbleDictionary data = new PebbleDictionary();
		data.addString( 3, "test");
		
		if ( isConnected() ) {
			PebbleKit.sendDataToPebble( state, uuid, data );
		}
	}

	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public boolean isConnected () {
		if ( !isConnected ) {
			return false;
		}
		
		Time now = new Time();
		now.setToNow();
		
		if ( Time.compare( lastConnectCheck, now ) > 10 ) {
			if ( isConnected = checkConnection() ) {
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private boolean checkConnection () {
		lastConnectCheck.setToNow();
		return true;
	}
	
	public void launchPebbleApp () {
		PebbleKit.startAppOnPebble( state, uuid );
	}
	
	public void closePebbleApp () {
		PebbleKit.closeAppOnPebble( state, uuid );
	}
	
	public void sendNotification ( String data, boolean vibrate ) {
		PebbleDictionary dict = new PebbleDictionary();
		
		if ( vibrate ) {
			
		}
		// else we got bad vibes mon
		
		PebbleKit.sendDataToPebble( state, uuid, dict );
	}
	
	public void sendNotification ( String data ) {
		sendNotification( data, false );
	}
}
