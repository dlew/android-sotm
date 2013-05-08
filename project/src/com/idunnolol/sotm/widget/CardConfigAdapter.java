package com.idunnolol.sotm.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.CardSet;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.utils.Ui;

public class CardConfigAdapter extends BaseAdapter {

	private enum RowType {
		HEADER,
		CARD
	}

	private Context mContext;

	private List<Object> mItems;

	private int mCachedHeaderBgColor;
	private float mCachedHeaderTextSize;

	public CardConfigAdapter(Context context) {
		mContext = context;

		// Construct the items list - each card set, followed by all cards in it
		mItems = new ArrayList<Object>();
		for (CardSet cardSet : Db.getCardSets()) {
			mItems.add(cardSet);
			mItems.addAll(cardSet.getCards());
		}

		Resources res = context.getResources();
		mCachedHeaderBgColor = res.getColor(android.R.color.holo_blue_light);
		mCachedHeaderTextSize = res.getDimensionPixelSize(R.dimen.text_size_row_header);
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
		if (mItems.get(position) instanceof CardSet) {
			return RowType.HEADER;
		}
		else {
			return RowType.CARD;
		}
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RowType rowType = getItemRowType(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.row_card_checkable, parent, false);

			holder = new ViewHolder();
			holder.mLabel = Ui.findView(convertView, R.id.label_text_view);
			convertView.setTag(holder);

			// Set different backgrounds based on the type
			if (rowType == RowType.HEADER) {
				convertView.setBackgroundColor(mCachedHeaderBgColor);
				holder.mLabel.setTextAppearance(mContext, android.R.style.TextAppearance_Inverse);
				holder.mLabel.setTypeface(Typeface.DEFAULT_BOLD);
				holder.mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCachedHeaderTextSize);
			}
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		int labelResId;
		if (rowType == RowType.HEADER) {
			CardSet cardSet = (CardSet) getItem(position);
			labelResId = cardSet.getNameResId();
		}
		else {
			Card card = (Card) getItem(position);
			labelResId = card.getNameResId();
		}
		holder.mLabel.setText(labelResId);

		return convertView;
	}

	private static class ViewHolder {
		public TextView mLabel;
	}
}
