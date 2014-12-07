package com.example.metster;

import java.util.Random;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.example.metster.Rend.fb_event_ref;
import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(),);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +
                       // extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<3; i++) {
                    Log.i("tag", "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i("tag", "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                System.out.println("gcm says: " + extras.toString());
                /*
                String message[] = extras.toString().split(" ");
                Log.w("from",message[1]);//message=firstname
                Log.w("message",message[2]);
                Log.w("id",message[3]);
                */
                //String message_data[] = message[1].split("="); 
                //String info_gcm[] = message_data[1].split("-#>");
                //sendNotification(info_gcm[0]);
                //Log.w("memberid",info_gcm[1]);
                //Log.w("from",info_gcm[2].replace(",", ""));
                //Log.i("tag", "Received: " + extras.toString());
                //update_loc_of(info_gcm[1],info_gcm[2].replace(",", ""));
                
                String message[] = extras.toString().split(" ");// xxx message=fname lname-#>messagedata  xxx
                String name[] = message[1].split("="); //message=fname --> message fname 
                String message_data[] = message[2].split("-#>"); // lname  messagedata 
                String messagedata = message_data[1];
                String action  = intent.getStringExtra("action");
                Log.w("from",name[1]);//message=firstname
                Log.w("message",message_data[1]);//actual message
                Log.w("id",message_data[2]);//id
                
                sendNotification(name[1], message_data[1], "event-"+message_data[2]);
                int extra = 0;
                try {
                        extra = Integer.parseInt(intent.getStringExtra("action_id"));
                } catch (Exception e){
                        /* ignore */
                }
                
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
    public void update_loc_of(String memberid, String from){
    	StringBuilder strBuilder = new StringBuilder("https://met-ster-event.firebaseio.com/");
		strBuilder.append(from+"/"+"member-"+memberid);
	    fb_event_ref.fbref = strBuilder.toString();
	    fb_event_ref.firebaseobj = new Firebase(fb_event_ref.fbref);
	    fb_event_ref.firebaseobj.child("latitudes").setValue(commondata.user_information.latitude);
	    fb_event_ref.firebaseobj.child("longitudes").setValue(commondata.user_information.longitude);
    }

    public void createNotification(View view, String message) {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(this, Login.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, GCM_handle.class), 0);
	    
	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle("Metster")
	        .setContentText(message + " is requesting your location.").setSmallIcon(R.drawable.ic_action_group)
	        .addAction(R.drawable.ic_action_next_item, "Send location", pIntent).build();
	    
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    notificationManager.notify(0, noti);

	  }
    
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String name, String message, String event_id) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        	commondata.gcm_req.requester_name = name;
        	commondata.gcm_req.event_id = event_id.replace(",", "");
        	createNotification(null, name);
        
        
    }
}
