package com.nmysore.metster;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nmysore.metster.Login.fb_event_ref;

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
        System.out.println("gcm data incoming...");
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
                 * When a gcm message comes we need to check what type of message it is
                 * 1) invite from a friend -- store in chache and join
                 * 2) reject for an invite sent from you -- just notify dont store
                 * 3) accept for an invite sent from you -- just notify dont store, already data on firebase
                 */
                
                String message_info = extras.getString("message", null);
                if(message_info  != null ){
                	System.out.println("message is : " + message_info);
                	
                	try {
                		JSONObject gcmdata = new JSONObject(message_info);
						String host = gcmdata.getString("host");
						String  to_id = gcmdata.getString("to_id");
	                	String  payload_type = gcmdata.getString("payload_type");
	                	String  payload_message = gcmdata.getString("payload_message");
	                	String sender_name = gcmdata.getString("sender_name");
	                	String event_refrence = gcmdata.getString("event_refrence");
	                	String[] data = payload_message.split("--");
	                	
	                	/*
	                	 * payload_type : invite_check, invite_accept, invite_no
                                host_cancel, invite_drop
	                	 * case a 0 : invite_check 
	                	 * 			in login if in server response ok then sent else fail
	                	 * case b 1: invite_accept
	                	 * 			a member accepted invite send gcm to host(organizer) from this user
	                	 * case c 2: invite_no
	                	 * 			a member rejected invite send gcm to host (organizer) from this user
	                	 * case d 3: host cancel
	                	 * 			this is incoming saying host cancled.. let this user know that host
	                	 * 			cancled the evrnt and flush all data in local data for that event
	                	 * case e 4: invite_drop
	                	 * 			this is when user bails out... let the host know by sending a gcm
	                	 */
	                	int type = 0;
	                	if(payload_type == "invite_check") type = 0;
	                	if(payload_type == "invite_accept") type = 1;
	                	if(payload_type == "invite_reject") type = 2;
	                	if(payload_type == "host_cancel") type = 3;
	                	if(payload_type == "invite_drop") type = 4;
	                	
	                	
	                	Log.w("from", host);
	                    Log.w("message", to_id);
	                    Log.w("type", payload_type);//id
	                    
	                    switch(type){
	                    	case 0:// invite_check store in shared and ask user join / reject
	                    		
	                    		final SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("invite_notification", MODE_PRIVATE).edit();
	                            editor.putString("invite_status", "yes");
	                            editor.putString("invite_from", host);
	                            editor.putString("eventid", event_refrence);
	                            editor.putString("sender_name", sender_name);
	                            editor.putString("message", "invite from" + sender_name);
	                            editor.commit();
	                            sendNotification("New Invite", event_refrence, "invite from " + sender_name);
	                    		break;
	                    	case 1:// invite_accept comes back on gcm
	                    		
	                    		sendNotification("Notification", data[1], "Accepted your invite");
	                    		
	                    		break;
	                    	case 2:// invite_reject comes back on gcm
	                    		sendNotification("Notification", data[1], "Rejected your invite");
	                    		break;
	                    	case 3:// host_cancelcomes on gcm
	                    		break;
	                    	case 4:// invite_drop comes on gcm
	                    		break;
	                    	default:
	                    		break;
	                    }
	                    
	                    
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}                    

                    
                }else{
                	System.out.println("GCM Error");
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
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, Accept_invite.class), 0);
	    
	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle("Metster")
	        .setContentText(message + " is requesting your location.").setSmallIcon(R.drawable.logo)
	        .setAutoCancel(true)
	        .setContentIntent(pIntent).build();
	    
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    noti.defaults |= Notification.DEFAULT_VIBRATE;
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
