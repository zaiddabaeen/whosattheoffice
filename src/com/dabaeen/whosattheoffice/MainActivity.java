package com.dabaeen.whosattheoffice;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dabaeen.whosattheoffice.Communicator.ResponseListener;

public class MainActivity extends Activity {

	private LinearLayout lSamer, lOmar, lZaid;

	public enum Users{SAMER,OMAR,ZAID};

	public String ME = null;
	public final static String initMe = "omar";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		Drawable background = findViewById(R.id.layout).getBackground();
		background.setAlpha(20);

		lSamer = (LinearLayout) findViewById(R.id.lSamer);
		lOmar = (LinearLayout) findViewById(R.id.lOmar);
		lZaid = (LinearLayout) findViewById(R.id.lZaid);

		ME = whoAmI();

		getUsersState();

		(new Communicator(this)).initGCMService(this);
		(new Communicator(this)).checkAPKupdate();

		startTracking();

		MediaPlayer.create(this, R.raw.believe).start();

	}

	@Override
	protected void onStart() {

		IntentFilter broadFilter = new IntentFilter();
		broadFilter.addAction(this.getPackageName() + ".REFRESH_OFFICE");
		broadFilter.addAction(this.getPackageName() + ".UPDATE_OFFICE");
		broadFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

		registerReceiver(bRec, broadFilter);
		super.onStart();

	}

	@Override
	protected void onStop() {
		if(bRec!=null) unregisterReceiver(bRec);
		bRec = null;

		super.onStop();
	}

	private String whoAmI(){

		SharedPreferences prefs = getSharedPreferences("default", MODE_PRIVATE);

		if (prefs.getString("user", null) == null){

			prefs.edit().putString("user", initMe);

		}

		return prefs.getString("user", null);

	}

	private void startTracking(){
		GPSTracker gps = new GPSTracker(this);
		//gps.getLocationString();

		Intent intent2 = new Intent("com.dabaeen.whosattheoffice"+ ".LOCATION_CHANGED");
		LocationManager manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		long minTime = 5*60*1000;     // Milliseconds
		float minDistance = 100*1; // Meters

		PendingIntent launchIntent = PendingIntent.getBroadcast(this, 787, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
		if(gps.isNetworkEnabled)
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, launchIntent); 
		else if(gps.isGPSEnabled)
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, launchIntent);
		else {
			Toast.makeText(this, "Your location services are not running. Please run it at battery saver.", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	private void setUserStatus(Enum user, boolean status){

		Log.d("Office", "Setting " + user.toString() + " to " + status);

		if(user == Users.SAMER){
			if(status) lSamer.setAlpha(1f);
			else lSamer.setAlpha(0.3f);
		} else if(user == Users.ZAID){
			if(status) lZaid.setAlpha(1f);
			else lZaid.setAlpha(0.3f);
		} else if(user == Users.OMAR){
			if(status) lOmar.setAlpha(1f);
			else lOmar.setAlpha(0.3f);
		}

	}

	private DownloadManager dm;
	private long enqueue;
	private void updateApp(){

		setProgressBarIndeterminateVisibility(true);

		try{

			dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			Request request = new Request(
					Uri.parse("http://phpcattest.bugs3.com/office_update.apk"));
			request.setDestinationUri(Uri.fromFile(new File(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "office_update.apk")));
			enqueue = dm.enqueue(request);

		} catch (Exception e){
			setProgressBarIndeterminateVisibility(false);
			Log.e("Office", "Failed to download update. " + e.getMessage());
		}
	}

	BroadcastReceiver bRec = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			if(intent == null) return;

			try{

				if(intent.getAction().equals(context.getPackageName() + ".REFRESH_OFFICE")){
					getUsersState();
				}
				else if(intent.getAction().equals(context.getPackageName() + ".UPDATE_OFFICE")){
					updateApp();
				} else if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

					Query query = new Query();
					query.setFilterById(enqueue);
					Cursor c = dm.query(query);
					if (c.moveToFirst()) {
						int columnIndex = c
								.getColumnIndex(DownloadManager.COLUMN_STATUS);
						if (DownloadManager.STATUS_SUCCESSFUL == c
								.getInt(columnIndex)) {

							String uriString = c
									.getString(c
											.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

							Log.i("Office Updater", uriString);
							Intent upIntent = new Intent(Intent.ACTION_VIEW);
							upIntent.setDataAndType(Uri.parse(uriString), "application/vnd.android.package-archive");
							upIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
							MainActivity.this.startActivity(upIntent);
						} else {

						}
						setProgressBarIndeterminateVisibility(false);
					}
				}

			} catch (Exception e){
				setProgressBarIndeterminateVisibility(false);
				Log.e("Office", "Failed to run update. " + e.getMessage());
			}

		};
	};

	private void getUsersState(){

		final Handler handler = new Handler();

		new Thread(new Runnable() {

			@Override
			public void run() {

				Communicator comm = new Communicator(MainActivity.this);
				comm.getStates(new ResponseListener() {

					@Override
					public void response(String resp) {

						try {
							JSONArray jResp = new JSONArray(resp);

							JSONObject jSamer = jResp.getJSONObject(0);
							JSONObject jOmar = jResp.getJSONObject(1);
							JSONObject jZaid = jResp.getJSONObject(2);
							final Boolean samerState = parseStatus(jSamer.getString("status"));
							final Boolean omarState = parseStatus(jOmar.getString("status"));
							final Boolean zaidState = parseStatus(jZaid.getString("status"));

							handler.post(new Runnable() {

								@Override
								public void run() {

									setUserStatus(Users.SAMER, samerState);
									setUserStatus(Users.OMAR, omarState);
									setUserStatus(Users.ZAID, zaidState);

								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}


					}
				});

			}
		}).start();

	}

	private boolean parseStatus(String status){
		return status.equals("1");
	}

	public void Privacy(View v){

		Toast.makeText(this, "Your location never leaves your device.", Toast.LENGTH_SHORT).show();

	}

}
