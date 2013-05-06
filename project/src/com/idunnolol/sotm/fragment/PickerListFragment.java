package com.idunnolol.sotm.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.widget.PickerAdapter;

public class PickerListFragment extends ListFragment {

	public static final String TAG = PickerListFragment.class.getName();

	private PickerAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new PickerAdapter(getActivity());
		setListAdapter(mAdapter);
	}

	//////////////////////////////////////////////////////////////////////////
	// Action bar

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_picker, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_randomize:
			// TODO: IMPLEMENT
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
