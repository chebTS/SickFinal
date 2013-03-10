package org.geekhub.tsibrovsky.sickukrainefinal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticleInfo;
import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.actionbarsherlock.view.Menu;

public class FirstActivity extends SherlockFragmentActivity {
	static final String SERVER_URL = "http://chebtest1.appspot.com/chebtest1";
	private static final String TAG_FEED = "feed";
	private static SharedPreferences mPreferences;
	private List<ArticleInfo> mArticles;
	private DownloderRSS downloadRSS;
	//private ProgressDialog pd;
	FragmentList fragment1;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		fragment1 = (FragmentList)getSupportFragmentManager().findFragmentById(R.id.fragment1);
		downloadRSS = (DownloderRSS) getLastCustomNonConfigurationInstance();
		if (downloadRSS == null) {
			downloadRSS = new DownloderRSS();
			if (isConnectingToInternet()){
				downloadRSS.execute();		
			}else{
				openJSON();
			}
		}
		downloadRSS.link(this);
	}

	
	public Object onRetainCustomNonConfigurationInstance() {
		downloadRSS.unLink();
	    return downloadRSS;
	}
	
	
	
	
	static class DownloderRSS extends AsyncTask<Void, Void, Void>{		
		FirstActivity activity;
		String res;
		void link(FirstActivity act) {
		      activity = act;
		}		    
	    void unLink() {
	      activity = null;
	    }	

	    @Override
		protected Void doInBackground(Void... params) {
	    	res = downloadJSON(SERVER_URL, activity);
			//activity.parseJSON(downloadJSON(SERVER_URL, activity));
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			activity.parseJSON(res);
			Log.i("AsynkTask","download complete");
		}
	}
	
	private void openJSON(){
		mPreferences = getSharedPreferences(TAG_FEED, 0); 
		parseJSON(mPreferences.getString(TAG_FEED, ""));
	}
	
	/**
	 * @author Cheb
	 */
	private void parseJSON(String jsonString){
		JSONObject jRoot;
		JSONArray jItems;
		mArticles = new ArrayList<ArticleInfo>();
		try {
			jRoot = new JSONObject(jsonString);
		} catch (JSONException e) {
			return;
		}
		jItems = jRoot.optJSONObject("rss").optJSONObject("channel").optJSONArray("item");
		Log.i("Count", Integer.toString(jItems.length()));
		for (int i = 0; i<jItems.length(); i++){
			mArticles.add(new ArticleInfo(jItems.optJSONObject(i)));
		}
		fragment1.setmArticlesLocal(mArticles);
		
	}
	
	/**
	 * @author Cheb
	 * 
	 */
	private static String downloadJSON(String URL, Context context) {
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
	
	/**
	 * @author Cheb
	 * @return internet conectivity status
	 */
	public  boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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

}
