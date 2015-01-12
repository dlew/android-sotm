package com.idunnolol.sotm.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.danlew.utils.FontCache;
import com.danlew.utils.Ui;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.CardSet;
import com.idunnolol.sotm.data.Db;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardConfigAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context mContext;

    private List<CardSet> mHeaders;
    private List<Card> mCards;
    private Map<Card, Integer> mCardToHeader;

    private int mCachedHeaderBgColor;
    private float mCachedHeaderTextSize;

    public CardConfigAdapter(Context context) {
        mContext = context;

        mHeaders = Db.getCardSets();
        mCards = new ArrayList<Card>();
        mCardToHeader = new HashMap<Card, Integer>();

        // Construct the items list - each card set, followed by all cards in it
        for (int a = 0; a < mHeaders.size(); a++) {
            CardSet cardSet = mHeaders.get(a);
            mCards.addAll(cardSet.getCards());
            for (Card card : cardSet.getCards()) {
                mCardToHeader.put(card, a);
            }
        }

        Resources res = context.getResources();
        mCachedHeaderBgColor = res.getColor(android.R.color.holo_blue_light);
        mCachedHeaderTextSize = res.getDimensionPixelSize(R.dimen.text_size_row_header);
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public Card getItem(int position) {
        return mCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflateRow(parent);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        Card card = getItem(position);
        holder.mIcon.bind(card, false);
        holder.mLabel.setText(card.getName(mContext));

        return convertView;
    }

    private static class ViewHolder {

        public IconView mIcon;
        public TextView mLabel;
        public CheckBox mCheckBox;

    }

    private View inflateRow(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_card_checkable, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.mIcon = Ui.findView(view, R.id.icon_view);
        holder.mLabel = Ui.findView(view, R.id.label_text_view);
        holder.mCheckBox = Ui.findView(view, R.id.checkbox);
        view.setTag(holder);

        return view;
    }

    //////////////////////////////////////////////////////////////////////////
    // StickyListHeadersAdapter

    public CardSet getHeaderItem(int position) {
        return mHeaders.get(mCardToHeader.get(mCards.get(position)));
    }

    @Override
    public long getHeaderId(int position) {
        return mCardToHeader.get(mCards.get(position));
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflateRow(parent);

            holder = (ViewHolder) convertView.getTag();

            convertView.setBackgroundColor(mCachedHeaderBgColor);
            holder.mIcon.setVisibility(View.GONE);
            holder.mLabel.setTextAppearance(mContext, android.R.style.TextAppearance_Inverse);
            holder.mLabel.setTypeface(FontCache.getTypeface(mContext,
                mContext.getString(R.string.font_crash_landing)));
            holder.mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCachedHeaderTextSize);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardSet cardSet = getHeaderItem(position);
        holder.mLabel.setText(cardSet.getNameResId());
        holder.mCheckBox.setChecked(cardSet.areAllCardsEnabled());

        return convertView;
    }
}
