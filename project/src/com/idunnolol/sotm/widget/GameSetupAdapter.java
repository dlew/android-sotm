package com.idunnolol.sotm.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.GameSetup;

public class GameSetupAdapter extends BaseAdapter {

	private enum RowType {
		HEADER,
		CARD
	}

	private Context mContext;

	private GameSetup mGameSetup;

	private boolean mAllowEditing;

	public GameSetupAdapter(Context context, GameSetup gameSetup, boolean allowEditing) {
		mGameSetup = gameSetup;
		mContext = context;
		mAllowEditing = allowEditing;
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		return getItemRowType(position).ordinal();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return mAllowEditing && getItemRowType(position) == RowType.CARD;
	}

	public RowType getItemRowType(int position) {
		Type type = getType(position);
		int typeStart = getTypeStart(type);

		if (position == typeStart) {
			return RowType.HEADER;
		}
		else {
			return RowType.CARD;
		}
	}

	public int getTypeStart(Type type) {
		switch (type) {
		case HERO:
			return 0;
		case VILLAIN:
			return mGameSetup.getHeroCount() + 1;
		case ENVIRONMENT:
			return mGameSetup.getHeroCount() + 3;
		default:
			throw new RuntimeException();
		}
	}

	public Type getType(int position) {
		if (position < getTypeStart(Type.VILLAIN)) {
			return Type.HERO;
		}
		else if (position < getTypeStart(Type.ENVIRONMENT)) {
			return Type.VILLAIN;
		}
		else {
			return Type.ENVIRONMENT;
		}
	}

	@Override
	public int getCount() {
		// 3 headers, 1 villain, 1 environment, and N heroes
		return 5 + mGameSetup.getHeroCount();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object getItem(int position) {
		RowType rowType = getItemRowType(position);
		Type type = getType(position);

		switch (rowType) {
		case HEADER:
			switch (type) {
			case HERO:
				return R.string.header_heroes;
			case VILLAIN:
				return R.string.header_villain;
			case ENVIRONMENT:
				return R.string.header_environment;
			}
			break;
		case CARD:
			switch (type) {
			case HERO:
				int typeStart = getTypeStart(type);
				int heroIndex = position - typeStart - 1;
				return mGameSetup.getHeroes().get(heroIndex);
			case VILLAIN:
				return mGameSetup.getVillain();
			case ENVIRONMENT:
				return mGameSetup.getEnvironment();
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
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView textView = (TextView) convertView;
		textView.setText((Integer) getItem(position));

		return convertView;
	}

	@SuppressWarnings("incomplete-switch")
	private View getCardView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView textView = (TextView) convertView;
		Card card = (Card) getItem(position);
		int resId = card.getNameResId();
		if (card == Card.RANDOM) {
			switch (getType(position)) {
			case HERO:
				resId = R.string.card_random_hero;
				break;
			case VILLAIN:
				resId = R.string.card_random_villain;
				break;
			case ENVIRONMENT:
				resId = R.string.card_random_environment;
				break;
			}
		}

		textView.setText(resId);

		return convertView;
	}

}
