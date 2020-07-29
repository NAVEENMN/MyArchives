package com.pebblemeetup.push;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.format.Time;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class Push extends Application{
	private boolean isConnected;
	private Time lastConnectCheck;
	private UUID uuid;
	private Context state;
	
	private Push () {
		isConnected = checkConnection();
		state = getApplicationContext();
		uuid = UUID.randomUUID();
	}
	
	public void sendData ( PebbleDictionary data ) {
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
