package org.geekhub.tsibrovsky.sickukrainefinal;

import java.util.ArrayList;
import java.util.List;

import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticleInfo;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticlesTable;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ChebProvider;
import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentList;
import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentWeb;
import org.geekhub.tsibrovsky.sickukrainefinal.helpers.Helper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

/**
 * 
 * @author Cheb
 * Activity for downloading and showing titles list of articles
 * If device is tablet in landscape - shows article in right part of screen.
 */
public class FirstActivity extends SherlockFragmentActivity {
	static final String SERVER_URL = "http://chebtest1.appspot.com/chebtest1";
	private static final String TAG_FEED = "feed";
	private static SharedPreferences mPreferences;
	private static List<ArticleInfo> mArticles;
	private static List<ArticleInfo> mArticlesFromDB;
	private DownloderRSS downloadRSS;
	private static Boolean isShowingLiked = false;
	private ProgressDialog pd;
	FragmentList fragment1;
	FragmentWeb fragment2;
	ArticleInfo curArticle = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		fragment1 = (FragmentList)getSupportFragmentManager().findFragmentById(R.id.fragment1);
		fragment2 = (FragmentWeb)getSupportFragmentManager().findFragmentById(R.id.fragment2);
		
		downloadRSS = (DownloderRSS) getLastCustomNonConfigurationInstance();
		if (downloadRSS == null) {
			downloadRSS = new DownloderRSS();
			pd = new ProgressDialog(FirstActivity.this, ProgressDialog.THEME_HOLO_DARK);
			pd.setTitle("Loading...");
			pd.setCancelable(false);
			pd.show();
			
			if (Helper.isConnectingToInternet(this)){
				downloadRSS.execute();		
			}else{
				openJSON();
				
			}
		}
		if (isShowingLiked){
			fragment1.setArticlesLocal(mArticlesFromDB);
		}else{
			if (mArticles != null){
				fragment1.setArticlesLocal(mArticles);
			}
		}
		downloadRSS.link(this);				
	}
	
	/**
	 * @author Cheb
	 * @return cursor to database
	 */
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
			getSupportMenuInflater().inflate(R.menu.first_active, menu);
			MenuItem menuItem = menu.findItem(R.id.action_show_liked);
			if (isShowingLiked){
				menuItem.setTitle("Show all");
			}else{
				menuItem.setTitle("Show liked");
			}
			tabletLandMenu(menu);
			return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * @author Cheb
	 * Making menu for tablet in landscape
	 * @param menu
	 */
	private void tabletLandMenu(Menu menu){
		if(fragment2 != null){
			if (fragment2.isInLayout()){
				MenuItem menuItemShare = menu.findItem(R.id.share);
				ShareActionProvider mShareActionProvider =  (ShareActionProvider) menuItemShare.getActionProvider();  
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
			    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    shareIntent.setType("text/plain");
			    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharesubject));
			    shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.sharetext));
			    mShareActionProvider.setShareIntent(shareIntent);			    
				if (curArticle!= null){
					MenuItem menuItemLike = menu.findItem(R.id.like);
				    if (isCurrentArticleLiked(curArticle.getId())){
				    	menuItemLike.setIcon(R.drawable.ic_menu_liked);
				    }else{
				    	menuItemLike.setIcon(R.drawable.ic_menu_not_liked);
				    }
				}
			}
		}	
	}
	
		
	/**
	 * 
	 * @author Cheb
	 * @param id - id of article
	 * @return true - if chosen article was liked previously 
	 * 		   false - if not	
	 */
	private Boolean isCurrentArticleLiked(Long id){
		Cursor cursor = getContentResolver().query(
				ChebProvider.CONTENT_URI, 
				ArticlesTable.PROJECTION, 
				ArticlesTable.COLUMN_ID +" = " + id.toString() , 
				null, null);
		Log.i("Count", Integer.toString(cursor.getCount()));
		if (cursor.getCount() > 0){
			return  true;	
		}else{
			return false;	
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_show_liked:
			switchContent();		
			break;
		case R.id.like:
			likePressed();				
			break;
		default:
			break;
		}		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * @author Cheb
	 * change content of listview (from favorites or all)
	 */
	private void switchContent(){
		if (isShowingLiked){
			fragment1.setArticlesLocal(mArticles);
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
				fragment1.setArticlesLocal(mArticlesFromDB);
				isShowingLiked = true;
				invalidateOptionsMenu();
			}
		}
	}

	/**
	 * @author Cheb
	 * Called when like menu item pressed.
	 * add/delete article in/from favorites;
	 */
	private void likePressed(){
		if (curArticle != null){	
			if (isCurrentArticleLiked(curArticle.getId())){
				Uri uri = Uri.parse(ChebProvider.CONTENT_URI + "/" + curArticle.getId());
	    		getContentResolver().delete(uri, ArticlesTable.COLUMN_ID + "= ?", new String[]{String.valueOf(curArticle.getId())});			
	    		Cursor c = getDataBaseData();
	    		if (c.getCount()==0){
	    			fragment1.setArticlesLocal(mArticles);
					isShowingLiked = false;
	    		}
			}else{
				ContentValues values = new ContentValues();
	    		values.put(ArticlesTable.COLUMN_ID, curArticle.getId().toString());
	        	values.put(ArticlesTable.COLUMN_TITLE, curArticle.getTitle().toString());
	        	values.put(ArticlesTable.COLUMN_CONTENT, curArticle.getDescription().toString());
	        	values.put(ArticlesTable.COLUMN_URL, curArticle.getLinkURL().toString());
	        	getContentResolver().insert(ChebProvider.CONTENT_URI, values);
			}
			invalidateOptionsMenu();
		}
	}
	
	//TODO make it using broadcast!!
	@Override
	protected void onResume() {
		super.onResume();
		if (isShowingLiked){
			Cursor cursor  = getDataBaseData();
			if (cursor.getCount() == 0){
				Toast.makeText(getApplicationContext(), "There are no any liked articles anymore", Toast.LENGTH_LONG).show();
				isShowingLiked = false;
				fragment1.setArticlesLocal(mArticles);
				invalidateOptionsMenu();
			}else{
				mArticlesFromDB  = new ArrayList<ArticleInfo>();
				while (cursor.moveToNext()) {
					mArticlesFromDB.add(new ArticleInfo(cursor.getString(4), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
			    }
				fragment1.setArticlesLocal(mArticlesFromDB);
			}
		}
	}

	/**
	 * @author Cheb
	 * Open chosen article at second activity
	 * @param article - chosen article
	 */
	public void viewArticle(ArticleInfo article){
		if((fragment2 != null) &&(fragment2.isInLayout())){
			//if (fragment2.isInLayout()){
				fragment2.setUrl(article.getLinkURL());
				curArticle = article;
				invalidateOptionsMenu();
			//}
		}else{
			Intent sendIntent = new Intent(getApplicationContext(), SecondActivity.class);
			sendIntent.putExtra("url", article.getLinkURL());
			sendIntent.putExtra("id", article.getId());
			sendIntent.putExtra("title", article.getTitle());
			sendIntent.putExtra("description", article.getDescription());
			Log.i("Put url in intent", article.getLinkURL());
			startActivity(sendIntent);
		}
	}
	
	public Object onRetainCustomNonConfigurationInstance() {
		downloadRSS.unLink();
	    return downloadRSS;
	}
	
	/**
	 * 
	 * @author Cheb
	 * Download class
	 */
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
	    	res = Helper.downloadJSON(SERVER_URL, activity, mPreferences);
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
	 * @param jsonString - JSON that was downloaded from SERVER_URL
	 */
	private void parseJSON(String jsonString){
		JSONObject jRoot;
		JSONArray jItems;
		mArticles = new ArrayList<ArticleInfo>();
		try {
			jRoot = new JSONObject(jsonString);
		} catch (JSONException e) {
			Toast.makeText(this, "JSON parse error", Toast.LENGTH_LONG).show();
			return;
		}
		jItems = jRoot.optJSONObject("rss").optJSONObject("channel").optJSONArray("item");
		Log.i("Count", Integer.toString(jItems.length()));
		for (int i = 0; i<jItems.length(); i++){
			mArticles.add(new ArticleInfo(jItems.optJSONObject(i)));
		}
		isShowingLiked = false;
		fragment1.setArticlesLocal(mArticles);
		if(pd !=null ){
			pd.dismiss();
		}
	}
}