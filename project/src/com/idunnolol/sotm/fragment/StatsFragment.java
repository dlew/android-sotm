package com.idunnolol.sotm.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.utils.Ui;

public class StatsFragment extends Fragment {

	private TextView mStatsTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_stats, container, false);

		mStatsTextView = Ui.findView(root, R.id.stats_text_view);

		return root;
	}

	public void bind(GameSetup gameSetup) {
		mStatsTextView.setText("Points: " + gameSetup.getPoints() + "; Chance of winning: " + gameSetup.getWinPercent()
				+ "%; Estimated=" + gameSetup.hasRandomCards());
	}

}
