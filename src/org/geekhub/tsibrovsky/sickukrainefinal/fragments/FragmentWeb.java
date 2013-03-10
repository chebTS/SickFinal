package org.geekhub.tsibrovsky.sickukrainefinal.fragments;

import org.geekhub.tsibrovsky.sickukrainefinal.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentWeb extends SherlockFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_web, null);
		return v;
	}
}
