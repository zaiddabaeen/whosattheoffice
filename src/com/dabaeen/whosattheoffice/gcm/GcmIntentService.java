package com.dabaeen.whosattheoffice.gcm;

import java.util.Random;

import android.app.IntentService;
import android.app.Notification.Style;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
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

		SharedPreferences prefs = getSharedPreferences("default", MODE_PRIVATE);
		
		if(!prefs.getBoolean("notification", true)){
			return;
		}
		
		int NOTIFICATION_ID = (new Random()).nextInt();

		String msg = intent.getStringExtra("message");
		String user = intent.getStringExtra("user");

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon;
		if(user != null){
			if(user.equals("zaid")) icon = R.drawable.zaid;
			else if(user.equals("omar")) icon = R.drawable.omar;
			else if(user.equals("samer")) icon = R.drawable.samer;
			else icon = R.drawable.ic_launcher;
		} else icon = R.drawable.ic_launcher;

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(icon)
		.setVibrate(new long[]{0,500})
		.setAutoCancel(true);

		if(user.length()==0){
			mBuilder.setContentTitle("Announcement");
			mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
		} else {
			mBuilder.setContentTitle(msg);
		}
		try{
			//mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			if(prefs.getBoolean("sound", true)){
				mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.believe));
			}
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