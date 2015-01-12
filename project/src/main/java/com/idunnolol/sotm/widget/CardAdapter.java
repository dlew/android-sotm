package com.idunnolol.sotm.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.danlew.utils.Ui;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CardAdapter extends BaseAdapter {

    private Context mContext;
    private List<Card> mCards;

    // These cards are disabled (because they are already selected
    // for the game setup).
    private Set<Card> mDisabledCards;

    public CardAdapter(Context context, Collection<Card> cards, Set<Card> disabledCards) {
        mContext = context;

        // Create a copy of the cards and sort them
        mCards = new ArrayList<Card>();
        mCards.addAll(cards);
        Collections.sort(mCards, Card.getNameComparator(context));

        mDisabledCards = disabledCards;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mDisabledCards.contains(getItem(position));
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_card_dialog, parent, false);

            holder = new ViewHolder();
            holder.mIcon = Ui.findView(convertView, R.id.icon_view);
            holder.mLabel = Ui.findView(convertView, R.id.label_text_view);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Card card = getItem(position);
        holder.mIcon.bind(card, card.isAdvanced());
        holder.mLabel.setText(card.getName(mContext));
        holder.mLabel.setEnabled(isEnabled(position));

        return convertView;
    }

    private static class ViewHolder {

        public IconView mIcon;
        public TextView mLabel;
    }
}
