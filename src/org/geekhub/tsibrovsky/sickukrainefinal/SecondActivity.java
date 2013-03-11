package org.geekhub.tsibrovsky.sickukrainefinal;

import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticlesTable;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ChebProvider;
import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentWeb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

public class SecondActivity extends SherlockFragmentActivity {
	private static String url;
	private FragmentWeb frag2;
	private Long id;
	private Boolean bIsCurrentLiked;
	private String title, description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		frag2 = (FragmentWeb)getSupportFragmentManager().findFragmentById(R.id.fragment2);
		if (getIntent().hasExtra("url")){			
			url = getIntent().getStringExtra("url");
			Log.i("Get url from intent", url);
			frag2.setUrl(url);
		}
		if (getIntent().hasExtra("id")){			
			id = getIntent().getLongExtra("id",0);
			Log.i("Get id", Long.toString(id));
		}
		if (getIntent().hasExtra("title")){			
			title = getIntent().getStringExtra("title");
		}
		if (getIntent().hasExtra("description")){			
			description = getIntent().getStringExtra("description");
		}
		
		bIsCurrentLiked = isCurrentArticleLiked();
	}
	
	
	private Boolean isCurrentArticleLiked(){
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
		if (item.getItemId() == R.id.like){
			if (bIsCurrentLiked){
				Uri uri = Uri.parse(ChebProvider.CONTENT_URI + "/" + id);
	    		getContentResolver().delete(uri, ArticlesTable.COLUMN_ID + "= ?", new String[]{String.valueOf(id)});
	    		bIsCurrentLiked = false;
			}else{
				ContentValues values = new ContentValues();
	    		values.put(ArticlesTable.COLUMN_ID, id.toString());
	        	values.put(ArticlesTable.COLUMN_TITLE, title.toString());
	        	values.put(ArticlesTable.COLUMN_CONTENT, description.toString());
	        	values.put(ArticlesTable.COLUMN_URL, url.toString());
	        	getContentResolver().insert(ChebProvider.CONTENT_URI, values);
	        	bIsCurrentLiked = true;	
			}
			invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.i("onCreateOptionsMenu", "onCreateOptionsMenu");
		getSupportMenuInflater().inflate(R.menu.second_active, menu);
		MenuItem menuItem = menu.findItem(R.id.share);
		ShareActionProvider mShareActionProvider =  (ShareActionProvider) menuItem.getActionProvider();  
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharesubject));
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.sharetext));
	    mShareActionProvider.setShareIntent(shareIntent);
	    //Like
	    MenuItem menuItemLike = menu.findItem(R.id.like);
	    if (bIsCurrentLiked){
	    	menuItemLike.setIcon(R.drawable.ic_menu_liked);
	    }else{
	    	menuItemLike.setIcon(R.drawable.ic_menu_not_liked);
	    }
		return super.onCreateOptionsMenu(menu);
	}
}
