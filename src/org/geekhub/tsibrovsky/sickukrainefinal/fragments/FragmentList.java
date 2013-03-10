package org.geekhub.tsibrovsky.sickukrainefinal.fragments;

import java.util.List;

import org.geekhub.tsibrovsky.sickukrainefinal.FirstActivity;
import org.geekhub.tsibrovsky.sickukrainefinal.R;
import org.geekhub.tsibrovsky.sickukrainefinal.adapters.TitlesAdapter;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticleInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentList extends SherlockFragment {
	private List<ArticleInfo> mArticlesLocal;
	private ListView listTitles;
	private TitlesAdapter adapter;
	private FirstActivity activity;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FirstActivity)activity;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);
		listTitles = (ListView)v.findViewById(R.id.listViewTitles);
		listTitles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.viewArticle(mArticlesLocal.get(position));
			}
		});
		return v;
	}

	public void setmArticlesLocal(List<ArticleInfo> mArticlesLocal) {
		this.mArticlesLocal = mArticlesLocal;
		adapter = new TitlesAdapter(getSherlockActivity().getApplicationContext(), R.layout.item_list_titles, mArticlesLocal);
		listTitles.setAdapter(adapter);
	}
	
}
