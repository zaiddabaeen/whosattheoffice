package com.dabaeen.whosattheoffice.gcm;

import java.io.IOException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class PlayServices {

	private final String TAG = "Nyan-PlayServices";
	private static final String GCM_PREFS_FILE = "gcm_prefs_file";
	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private String GCM_SENDER_ID = null;

	GoogleCloudMessaging gcm;
	String regid;

	private static Context mContext;

	public PlayServices(Context c, String gcm_senderId){
		mContext = c;
		GCM_SENDER_ID = gcm_senderId;
	}

	/** Gets the registration ID. Registers if it didn't exist or has changed.
	 * 
	 * @return {@code String} Registration ID
	 */
	public String getGCMRegistrationID(){

		if(checkPlayServices()){
			gcm = GoogleCloudMessaging.getInstance(mContext);
			
			regid = getRegistrationId();
			
			if (regid==null||regid.length()==0) {
				regid = registerInBackground();
				Log.d(TAG, "GCM_DEVICE_TOKEN Registered = " + regid);
			}

		}
		return regid;
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				/*GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) mContext,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
				Log.e(TAG, "init: Google Play Services aren't installed");
			} else {
				Log.i(TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service from the SharedPreferences
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(){
		String registrationID;

		SharedPreferences pref = mContext.getSharedPreferences(
				GCM_PREFS_FILE, Context.MODE_PRIVATE);
		registrationID = pref.getString(PROPERTY_REG_ID, "");
		if (registrationID.equals("")) {
			return null;
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		String registeredVersion = pref.getString(PROPERTY_APP_VERSION, "");
		String currentVersion = getAppVersion(mContext)+"";
		if (!registeredVersion.equals(currentVersion)) {
			
			mContext.getSharedPreferences("default", Context.MODE_PRIVATE).edit().putBoolean("token", false);
			
			Log.i(TAG, "App version changed.");
			return "";
		}
		
		return registrationID;

	}

	/**
	 * Registers the application with GCM servers <strike>asynchronously</strike>.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 * 
	 * @return Registration ID
	 */
	private String registerInBackground() {
		/*  new AsyncTask<Void,Void,String>() {
	        @Override
	        protected String doInBackground(Void... params) {*/
		Log.d(TAG, "init: Registering in Google Cloud Servers");
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(mContext);
			}
			
			regid = gcm.register(GCM_SENDER_ID);

			// Persist the regID - no need to register again.
			storeRegistrationId(mContext, regid);
		} catch (IOException ex) {
			// If there is an error, don't just keep trying to register.
			// Require the user to click a button again, or perform
			// exponential back-off.
			ex.printStackTrace();
			return null;
		}
		return regid;
		/*  }

	        @Override
	        protected void onPostExecute(String msg) {
	           // mDisplay.append(msg + "\n");
	        }

	    }.execute(null, null, null);*/

	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context mContext) {
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param mContext application's mContext.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context mContext, String regId) {
		int appVersion = getAppVersion(mContext);
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				GCM_PREFS_FILE, Context.MODE_PRIVATE).edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putString(PROPERTY_APP_VERSION, appVersion+"");
		editor.commit();
	}

}
