package com.dabaeen.whosattheoffice.gcm;

import java.util.Random;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dabaeen.whosattheoffice.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

	private final String TAG = "redtroops-sdk";
	
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

			Log.i(TAG, "Push notification received");
			
			if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				showNotification(intent);
				
			}
		}
	}
	
	private void showNotification(Intent intent){

		int NOTIFICATION_ID = (new Random()).nextInt();
		
		String msg = intent.getStringExtra("message");
		String user = intent.getStringExtra("user");
		
		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon;
		if(user.equals("zaid")) icon = R.drawable.zaid;
		else if(user.equals("omar")) icon = R.drawable.omar;
		else if(user.equals("samer")) icon = R.drawable.samer;
		else icon = R.drawable.ic_launcher;
		
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(icon)
		.setContentTitle(msg)
		.setVibrate(new long[]{0,500})
		.setAutoCancel(true);
		
		try{
			
		//mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.believe));
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

}