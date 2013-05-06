package com.idunnolol.sotm.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.widget.PickerAdapter;

public class PickerListFragment extends ListFragment implements CardPickerDialogFragmentListener {

	public static final String TAG = PickerListFragment.class.getName();

	private PickerListFragmentListener mListener;

	private PickerAdapter mAdapter;

	private GameSetup mGameSetup;

	// Which index we're currently selecting for the card dialog fragment
	// TODO: onSaveInstanceState()
	private Type mSelectCardType;
	private int mSelectCardIndex;

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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		mSelectCardType = mAdapter.getType(position);
		int start = mAdapter.getTypeStart(mSelectCardType);
		mSelectCardIndex = position - start - 1;

		CardPickerDialogFragment dialogFragment = CardPickerDialogFragment.newInstance(mSelectCardType);
		dialogFragment.show(getActivity().getFragmentManager(), CardPickerDialogFragment.TAG);
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
	// CardPickerDialogFragmentListener

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onCardSelected(Card card) {
		switch (mSelectCardType) {
		case HERO:
			mGameSetup.setHero(mSelectCardIndex, card);
			break;
		case VILLAIN:
			mGameSetup.setVillain(card);
			break;
		case ENVIRONMENT:
			mGameSetup.setEnvironment(card);
			break;
		}

		mAdapter.notifyDataSetChanged();
	}

	//////////////////////////////////////////////////////////////////////////	
	// Interface

	public interface PickerListFragmentListener {
		public void onRandomize(GameSetup gameSetup);
	}

}
