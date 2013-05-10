package com.idunnolol.sotm.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Difficulty;

public class DifficultyAdapter extends BaseAdapter {

	private Context mContext;

	public DifficultyAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return Difficulty.values().length;
	}

	@Override
	public Difficulty getItem(int position) {
		return Difficulty.values()[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.row_dialog, parent, false);
		}

		TextView textView = (TextView) convertView;
		Difficulty difficulty = getItem(position);

		CharSequence text;
		if (difficulty.getTargetWinPercent() == -1) {
			text = mContext.getString(difficulty.getStrResId());
		}
		else {
			text = mContext.getString(R.string.template_win_rate, mContext.getString(difficulty.getStrResId()),
					difficulty.getTargetWinPercent());
		}
		textView.setText(text);

		return convertView;
	}

}
