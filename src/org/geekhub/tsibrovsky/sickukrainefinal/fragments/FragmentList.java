package org.geekhub.tsibrovsky.sickukrainefinal.fragments;

import java.util.List;

import org.geekhub.tsibrovsky.sickukrainefinal.R;
import org.geekhub.tsibrovsky.sickukrainefinal.adapters.TitlesAdapter;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticleInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentList extends SherlockFragment {
	private List<ArticleInfo> mArticlesLocal;
	private ListView listTitles;
	private TitlesAdapter adapter;
	
	
	



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);
		listTitles = (ListView)v.findViewById(R.id.listViewTitles);
		return v;
	}

	public void setmArticlesLocal(List<ArticleInfo> mArticlesLocal) {
		this.mArticlesLocal = mArticlesLocal;
		adapter = new TitlesAdapter(getSherlockActivity().getApplicationContext(), R.layout.item_list_titles, mArticlesLocal);
		listTitles.setAdapter(adapter);
	}
}
