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
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticlesTable;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ChebProvider;
import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FirstActivity extends SherlockFragmentActivity {
	static final String SERVER_URL = "http://chebtest1.appspot.com/chebtest1";
	private static final String TAG_FEED = "feed";
	private static SharedPreferences mPreferences;
	private static List<ArticleInfo> mArticles;
	private static List<ArticleInfo> mArticlesFromDB;
	private DownloderRSS downloadRSS;
	private static Boolean isShowingLiked = false;
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
		if (isShowingLiked){
			fragment1.setmArticlesLocal(mArticlesFromDB);
		}else{
			if (mArticles != null){
				fragment1.setmArticlesLocal(mArticles);
			}
		}
		downloadRSS.link(this);
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.first_active, menu);
		MenuItem menuItem = menu.findItem(R.id.action_show_liked);
		if (isShowingLiked){
			menuItem.setTitle("Show all");
		}else{
			menuItem.setTitle("Show liked");
			
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	
	private Cursor getDataBaseData(){
		Cursor cursor = getContentResolver().query(
				ChebProvider.CONTENT_URI, 
				ArticlesTable.PROJECTION, 
				ArticlesTable.COLUMN_ID , 
				null, null);
		return cursor;
		//Log.i("DB size", Integer.toString(cursor.getCount()) );
	}
	
	

	
	@Override
	protected void onResume() {
		super.onResume();
		if (isShowingLiked){
			Cursor cursor  = getDataBaseData();
			if (cursor.getCount() == 0){
				Toast.makeText(getApplicationContext(), "There are no any liked articles anymore", Toast.LENGTH_LONG).show();
				isShowingLiked = false;
				fragment1.setmArticlesLocal(mArticles);
				invalidateOptionsMenu();
			}else{
				mArticlesFromDB  = new ArrayList<ArticleInfo>();
				while (cursor.moveToNext()) {
					mArticlesFromDB.add(new ArticleInfo(cursor.getString(4), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
			    }
				fragment1.setmArticlesLocal(mArticlesFromDB);
			}
		}
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_show_liked){
			if (isShowingLiked){
				fragment1.setmArticlesLocal(mArticles);
				isShowingLiked = false;
				invalidateOptionsMenu();
			}else{
				Cursor cursor  = getDataBaseData();
				if (cursor.getCount() == 0){
					Toast.makeText(getApplicationContext(), "There are no any liked articles yet", Toast.LENGTH_LONG).show();
				}else{
					mArticlesFromDB  = new ArrayList<ArticleInfo>();
					while (cursor.moveToNext()) {
						mArticlesFromDB.add(new ArticleInfo(cursor.getString(4), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
				    }
					fragment1.setmArticlesLocal(mArticlesFromDB);
					isShowingLiked = true;
					invalidateOptionsMenu();
				}
			}
		
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	/**
	 * @author Cheb
	 * Open chosen article at second activity
	 * @param article - chosen article
	 */

	public void viewArticle(ArticleInfo article){
		Intent sendIntent = new Intent(getApplicationContext(), SecondActivity.class);
		sendIntent.putExtra("url", article.getLinkURL());
		sendIntent.putExtra("id", article.getId());
		sendIntent.putExtra("title", article.getTitle());
		sendIntent.putExtra("description", article.getDescription());
		Log.i("Put url in intent", article.getLinkURL());
		startActivity(sendIntent);
		
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
		isShowingLiked = false;
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
