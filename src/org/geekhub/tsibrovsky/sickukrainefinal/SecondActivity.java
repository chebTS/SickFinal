package org.geekhub.tsibrovsky.sickukrainefinal;

import org.geekhub.tsibrovsky.sickukrainefinal.fragments.FragmentWeb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

public class SecondActivity extends SherlockFragmentActivity {
	private static String url;
	private FragmentWeb frag2;

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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.second_active, menu);
		//return true;
		MenuItem menuItem = menu.findItem(R.id.share);
		ShareActionProvider mShareActionProvider =  (ShareActionProvider) menuItem.getActionProvider();  
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharesubject));
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.sharetext));
	    mShareActionProvider.setShareIntent(shareIntent);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	
}
