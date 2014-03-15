package com.idunnolol.sotm.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.CardSet;
import com.idunnolol.sotm.widget.CardConfigAdapter;

public class CardConfigListFragment extends ListFragment {

    public static final String TAG = CardConfigListFragment.class.getName();

    private CardConfigAdapter mAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new CardConfigAdapter(getActivity());
        setListAdapter(mAdapter);

        ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        syncCheckedItems();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        SparseBooleanArray checkedItems = l.getCheckedItemPositions();
        boolean enabled = checkedItems.get(position);
        Object item = mAdapter.getItem(position);
        if (item instanceof CardSet) {
            ((CardSet) item).setAllCardsEnabled(enabled, true);
        }
        else {
            ((Card) item).setEnabled(enabled);
        }

        syncCheckedItems();
    }

    private void syncCheckedItems() {
        ListView listView = getListView();

        int count = mAdapter.getCount();
        for (int position = 0; position < count; position++) {
            Object item = mAdapter.getItem(position);
            if (item instanceof CardSet) {
                CardSet cardSet = (CardSet) item;
                listView.setItemChecked(position, cardSet.areAllCardsEnabled());
            }
            else {
                Card card = (Card) item;
                listView.setItemChecked(position, card.isEnabled());
            }
        }
    }

}
