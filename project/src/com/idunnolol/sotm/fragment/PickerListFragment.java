package com.idunnolol.sotm.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.widget.PickerAdapter;

public class PickerListFragment extends ListFragment {

	public static final String TAG = PickerListFragment.class.getName();

	private PickerListFragmentListener mListener;

	private PickerAdapter mAdapter;

	private GameSetup mGameSetup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		// TODO: Save this across instance changes/runs
		mGameSetup = new GameSetup();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListener = (PickerListFragmentListener) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new PickerAdapter(getActivity(), mGameSetup);
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
			mListener.onRandomize(mGameSetup);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//////////////////////////////////////////////////////////////////////////	
	// Interface

	public interface PickerListFragmentListener {
		public void onRandomize(GameSetup gameSetup);
	}
}
