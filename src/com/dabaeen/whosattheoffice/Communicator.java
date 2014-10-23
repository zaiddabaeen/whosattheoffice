package com.dabaeen.whosattheoffice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.dabaeen.whosattheoffice.gcm.PlayServices;

public class Communicator {

	private final static String SERVER_ADDRESS = "http://phpcattest.bugs3.com/office.php";
	private final static String UpdateURL = "http://phpcattest.bugs3.com/office_update.txt";

	public String ME = null;
	private Context context;

	Communicator(){}

	Communicator(Context context){

		this.context = context;
		ME = whoAmI();

	}

	private SharedPreferences getPrefs(){

		return context.getSharedPreferences("default", Context.MODE_PRIVATE);

	}

	private String whoAmI(){

		SharedPreferences prefs = getPrefs();

		if (prefs.getString("user", null) == null){

			prefs.edit().putString("user", MainActivity.initMe).commit();

		}

		return prefs.getString("user", null);

	}

	public void initGCMService(final Context context){

		new Thread(new Runnable() {
			@Override
			public void run() {
				PlayServices PS = new PlayServices(context, "256027487677");
				String regid = PS.getGCMRegistrationID();

				if(!getPrefs().getBoolean("token", false) && regid != null) {
					setToken(regid);
				} else {
					Log.d("Office", "No need to register dev_tok");
				}
			}
		}).start();

	}

	private void setToken(String device_token){

		Log.i("Office", "Setting token: " + device_token);

		try{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			HttpPost httppost = new HttpPost(SERVER_ADDRESS);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("action", "setToken"));
			nameValuePairs.add(new BasicNameValuePair("dev_tok", device_token));
			nameValuePairs.add(new BasicNameValuePair("user", ME));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httpclient.execute(httppost, new ResponseHandler<String>() {
					@Override
					public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

						String res = cleanResponse(EntityUtils.toString(response.getEntity()));

						if(res.equals("success")){

							getPrefs().edit().putBoolean("token", true).commit();
							Log.i("Office", "Registered dev_tok success");

						}

						return null;
					}
				});

			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public void sendLocation(Boolean status){

		Log.i("Office", "Sending location: " + status);

		try{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			HttpPost httppost = new HttpPost(SERVER_ADDRESS);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user", ME));
			nameValuePairs.add(new BasicNameValuePair("status", status?"1":"0"));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httpclient.execute(httppost);

			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public void getStates(final ResponseListener rLis){

		try{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			HttpPost httppost = new HttpPost(SERVER_ADDRESS);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("action", "get"));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httpclient.execute(httppost, new ResponseHandler<String>() {
					@Override
					public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

						String res = cleanResponse(EntityUtils.toString(response.getEntity()));

						rLis.response(res);
						Log.d("Office", res);

						return null;
					}
				});

			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public void checkAPKupdate(){

		new Thread(new Runnable() {

			@Override
			public void run() {
				int currentVersion = 0;
				int updateVersion = 0;

				try {
					PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
					currentVersion = pInfo.versionCode;

					// Create a URL for the desired page
					URL url = new URL(UpdateURL);

					// Read all the text returned by the server
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str;
					while ((str = in.readLine()) != null) {
						updateVersion = Integer.valueOf(str);
					}
					in.close();

				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(updateVersion!=0 && currentVersion!=0){

					if(updateVersion>currentVersion){

						Intent broadIntent = new Intent();
						broadIntent.setAction(context.getPackageName() + ".UPDATE_OFFICE");

						context.sendBroadcast(broadIntent);

					}

				}

			}
		}).start();

	}

	private String cleanResponse(String response){
		return response.substring(0, response.indexOf("<!--"));
	}

	public interface ResponseListener{

		public void response(String resp);

	}

}
