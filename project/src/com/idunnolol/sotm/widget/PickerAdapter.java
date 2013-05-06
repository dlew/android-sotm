package com.idunnolol.sotm.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Configuration;

public class PickerAdapter extends BaseAdapter {

	private enum RowType {
		HEADER,
		CARD
	}

	private enum Section {
		HEROES,
		VILLAIN,
		ENVIRONMENT
	}

	private Configuration mConfiguration = Configuration.getInstance();

	private Context mContext;
	private LayoutInflater mInflater;

	public PickerAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		return getItemRowType(position).ordinal();
	}

	public RowType getItemRowType(int position) {
		Section section = getSection(position);
		int sectionStart = getSectionStart(section);

		if (position == sectionStart) {
			return RowType.HEADER;
		}
		else {
			return RowType.CARD;
		}
	}

	private int getSectionStart(Section section) {
		switch (section) {
		case HEROES:
			return 0;
		case VILLAIN:
			return mConfiguration.getHeroCount() + 1;
		case ENVIRONMENT:
			return mConfiguration.getHeroCount() + 3;
		default:
			throw new RuntimeException();
		}
	}

	private Section getSection(int position) {
		if (position < getSectionStart(Section.VILLAIN)) {
			return Section.HEROES;
		}
		else if (position < getSectionStart(Section.ENVIRONMENT)) {
			return Section.VILLAIN;
		}
		else {
			return Section.ENVIRONMENT;
		}
	}

	@Override
	public int getCount() {
		// 3 headers, 1 villain, 1 environment, and N heroes
		return 5 + mConfiguration.getHeroCount();
	}

	@Override
	public Object getItem(int position) {
		RowType rowType = getItemRowType(position);
		Section section = getSection(position);

		switch (rowType) {
		case HEADER:
			switch (section) {
			case HEROES:
				return mContext.getString(R.string.header_heroes);
			case VILLAIN:
				return mContext.getString(R.string.header_villain);
			case ENVIRONMENT:
				return mContext.getString(R.string.header_environment);
			}
			break;
		case CARD:
			switch (section) {
			case HEROES:
				int sectionStart = getSectionStart(section);
				int heroIndex = position - sectionStart - 1;
				return mConfiguration.getHeroes().get(heroIndex);
			case VILLAIN:
				return mConfiguration.getVillain();
			case ENVIRONMENT:
				return mConfiguration.getEnvironment();
			}
			break;
		}

		throw new RuntimeException();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RowType rowType = getItemRowType(position);

		switch (rowType) {
		case HEADER:
			return getHeaderView(position, convertView, parent);
		case CARD:
			return getCardView(position, convertView, parent);
		}

		throw new RuntimeException();
	}

	private View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView textView = (TextView) convertView;
		String text = (String) getItem(position);
		textView.setText(text);

		return convertView;
	}

	private View getCardView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView textView = (TextView) convertView;
		Card card = (Card) getItem(position);
		String text = mContext.getString(card.getNameResId());
		textView.setText(text);

		return convertView;
	}

}
