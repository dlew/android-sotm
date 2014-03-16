package com.idunnolol.sotm.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.danlew.utils.Ui;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.CardSet;
import com.idunnolol.sotm.widget.CardConfigAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CardConfigListFragment extends Fragment {

    public static final String TAG = CardConfigListFragment.class.getName();

    private StickyListHeadersListView mListView;

    private CardConfigAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_config, container, false);

        mListView = Ui.findView(view, R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListView.setOnHeaderClickListener(mOnHeaderClickListener);

        mAdapter = new CardConfigAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        syncCheckedCards();

        return view;
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
            boolean enabled = checkedItems.get(position);
            Card card = mAdapter.getItem(position);
            card.setEnabled(enabled);
            syncCheckedCards();
        }
    };

    private StickyListHeadersListView.OnHeaderClickListener mOnHeaderClickListener =
        new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(
                StickyListHeadersListView stickyListHeadersListView, View view, int position, long headerId,
                boolean currentlySticky) {
                CardSet cardSet = mAdapter.getHeaderItem(position);
                cardSet.setAllCardsEnabled(!cardSet.areAllCardsEnabled());
                syncCheckedCards();
            }
        };

    private void syncCheckedCards() {
        int count = mAdapter.getCount();
        for (int position = 0; position < count; position++) {
            Card card = mAdapter.getItem(position);
            mListView.setItemChecked(position, card.isEnabled());
        }

        mAdapter.notifyDataSetChanged();
    }

}
