package org.geekhub.tsibrovsky.sickukrainefinal.adapters;

import java.util.List;

import org.geekhub.tsibrovsky.sickukrainefinal.R;
import org.geekhub.tsibrovsky.sickukrainefinal.db.ArticleInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TitlesAdapter extends ArrayAdapter<ArticleInfo> {
	private LayoutInflater inflater;
	private List<ArticleInfo> items;
	private Context context;
	
	public TitlesAdapter(Context context, int textViewResourceId, List<ArticleInfo> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		inflater =  LayoutInflater.from(context);
		items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_list_titles, null);
		TextView text = (TextView)v.findViewById(R.id.txtArticleName);
		text.setText(items.get(position).getTitle().toString());
		return v;
	}
	
	

	
}
