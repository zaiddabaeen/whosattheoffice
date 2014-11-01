package com.dabaeen.whosattheoffice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		Log.i("office", "Broadcast received");
		
		if(!intent.getAction().equals("com.dabaeen.whosattheoffice"+".LOCATION_CHANGED")){
			
			GPSTracker gps = new GPSTracker(context);
			gps.getLocationString();

			Intent intent2 = new Intent(context.getPackageName() + ".LOCATION_CHANGED");
			LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			
			long minTime = 5*60*1000;     // Milliseconds
			float minDistance = 50*1; // Meters

			PendingIntent launchIntent = PendingIntent.getBroadcast(context, 787, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
			if(gps.isNetworkEnabled)
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, launchIntent); 
			else if(gps.isGPSEnabled)
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, launchIntent);

			return;
		}
		
		Bundle locBundle = intent.getExtras();
		
		final Location loc = (Location) locBundle.get(LocationManager.KEY_LOCATION_CHANGED);

		if(loc == null) return;
		
		Log.d("Office", "Got location bundle " + loc.toString());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Communicator comm = new Communicator(context);
				//31.966852, 35.851811
				float[] results = new float[5];
				Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), 31.966852, 35.851811, results);
				Log.d("Office", "Distance from office: " + results[0]);
				comm.sendLocation(results[0]<500);
				
			}
		}).start();

	}

}
