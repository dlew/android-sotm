package com.idunnolol.sotm.fragment;

import com.idunnolol.sotm.widget.CardConfigAdapter;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

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
	}

}
