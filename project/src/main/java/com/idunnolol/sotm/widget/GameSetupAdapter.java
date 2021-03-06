package com.idunnolol.sotm.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.danlew.utils.Ui;
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

    private GameSetupAdapterListener mListener;

    public GameSetupAdapter(Context context, GameSetup gameSetup, GameSetupAdapterListener listener) {
        mContext = context;
        mGameSetup = gameSetup;
        mListener = listener;
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
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemRowType(position) == RowType.CARD;
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
                return mGameSetup.getHeroCount() + mGameSetup.getVillainCount() + 2;
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
        // 3 headers, 1 environment, N heroes, M villains
        return 4 + mGameSetup.getHeroCount() + mGameSetup.getVillainCount();
    }

    @Override
    public Object getItem(int position) {
        RowType rowType = getItemRowType(position);
        Type type = getType(position);

        switch (rowType) {
            case HEADER:
                switch (type) {
                    case HERO:
                        return mContext.getString(R.string.header_heroes);
                    case VILLAIN:
                        int numVillains = mGameSetup.getVillainCount();
                        return mContext.getResources().getQuantityString(R.plurals.header_villains, numVillains);
                    case ENVIRONMENT:
                        return mContext.getString(R.string.header_environment);
                }
                break;
            case CARD:
                int typeStart = getTypeStart(type);
                switch (type) {
                    case HERO:
                        return mGameSetup.getHeroes().get(position - typeStart - 1);
                    case VILLAIN:
                        return mGameSetup.getVillainAt(position - typeStart - 1);
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
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_header, parent, false);

            holder = new HeaderViewHolder();
            holder.mLabel = Ui.findView(convertView, R.id.label_text_view);
            holder.mAddButton = Ui.findView(convertView, R.id.add_button);
            convertView.setTag(holder);

            // For now, you can only add heroes; so assume that is what will happen
            // if this button is visible and enabled.
            holder.mAddButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAdd(Type.HERO);
                }
            });
        }
        else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.mAddButton.setVisibility(getType(position) == Type.HERO && mGameSetup.canAddHero() ? View.VISIBLE
            : View.GONE);

        holder.mLabel.setText((CharSequence) getItem(position));

        return convertView;
    }

    private static class HeaderViewHolder {

        private TextView mLabel;
        private View mAddButton;
    }

    private View getCardView(final int position, View convertView, ViewGroup parent) {
        CardViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_card, parent, false);

            holder = new CardViewHolder();
            holder.mIcon = Ui.findView(convertView, R.id.icon_view);
            holder.mLabel = Ui.findView(convertView, R.id.label_text_view);
            holder.mRemoveButton = Ui.findView(convertView, R.id.remove_button);
            convertView.setTag(holder);
        }
        else {
            holder = (CardViewHolder) convertView.getTag();
        }

        Card card = (Card) getItem(position);

        boolean isAdvanced = card.getType() == Type.VILLAIN && mGameSetup.isAdvancedVillain();
        holder.mIcon.bind(card, isAdvanced);

        holder.mLabel.setText(card.getName(mContext));

        int removeVisibility = getType(position) == Type.HERO && mGameSetup.canRemoveHero() ? View.VISIBLE : View.GONE;
        holder.mRemoveButton.setVisibility(removeVisibility);
        if (removeVisibility == View.VISIBLE) {
            // For now, you can only remove heroes; so assume that is what will happen
            // if this button is visible and enabled.
            holder.mRemoveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int start = getTypeStart(Type.HERO);
                    mListener.onRemove(Type.HERO, position - start - 1);
                }
            });
        }

        return convertView;
    }

    private static class CardViewHolder {

        private IconView mIcon;
        private TextView mLabel;
        private View mRemoveButton;
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener interface

    public interface GameSetupAdapterListener {

        public void onAdd(Type type);

        public void onRemove(Type type, int index);
    }

}
