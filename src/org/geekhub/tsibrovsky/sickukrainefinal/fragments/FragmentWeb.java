package org.geekhub.tsibrovsky.sickukrainefinal.fragments;

import org.geekhub.tsibrovsky.sickukrainefinal.R;
import org.geekhub.tsibrovsky.sickukrainefinal.helpers.Helper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * 
 * @author Cheb
 * Fragment for showing articles
 */
public class FragmentWeb extends SherlockFragment {
	private WebView web;
	
	public void setUrl(String url){
		if (Helper.isConnectingToInternet(getSherlockActivity())){
			//TODO save webview content
			web.loadUrl(url);
		}else{
			//TODO try to load saved content
		}
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setDisplayZoomControls(false);
		web.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getSherlockActivity(), description, Toast.LENGTH_SHORT).show();
            }
        });	
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_web, null);
		web = (WebView)v.findViewById(R.id.webView1);		
		return v;
	}
}