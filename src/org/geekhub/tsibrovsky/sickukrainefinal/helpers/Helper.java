package org.geekhub.tsibrovsky.sickukrainefinal.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Helper {
	private static final String TAG_FEED = "feed";

	/**
	 * @author Cheb
	 * @return Internet connectivity status
	 */
	public static boolean isConnectingToInternet(Activity activity){
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
          if (connectivity != null){
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null){
                  for (int i = 0; i < info.length; i++){
                      if (info[i].getState() == NetworkInfo.State.CONNECTED){
                          return true;
                      }
                  }
              }
          }
          return false;
    }
	
	/**
	 * @author Cheb
	 * @param URL - URL to server
	 * @param context - context
	 * @param mPreferences SharedPreferences
	 * downloading JSON from URL
	 */
	public static String downloadJSON(String URL, Context context, SharedPreferences mPreferences) {
		StringBuilder sb = new StringBuilder();
		DefaultHttpClient mHttpClient = new DefaultHttpClient();
		HttpGet dhttpget = new HttpGet(URL);
		HttpResponse dresponse = null;
		try{
			dresponse = mHttpClient.execute(dhttpget);
		}catch (IOException e) {
			e.printStackTrace();
		}
		int status = dresponse.getStatusLine().getStatusCode();
		if (status == 200){
			char[] buffer = new char[1];
			try{
				InputStream content = dresponse.getEntity().getContent();
	            InputStreamReader isr = new InputStreamReader(content);
	            while (isr.read(buffer) != -1) {
	                sb.append(buffer);
	            }
			}catch (IOException e) {
				e.printStackTrace();
			}
			//saving JSON 
			mPreferences = context.getSharedPreferences(TAG_FEED, 0); 
			mPreferences.edit().putString(TAG_FEED, sb.toString()).commit();
		}else{	
			Log.i("Error","Connection error : "+ Integer.toString(status));
		}
		return sb.toString();
	}
}
