package com.idunnolol.sotm.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.Randomizer;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.widget.GameSetupAdapter;
import com.idunnolol.sotm.widget.GameSetupAdapter.GameSetupAdapterListener;

public class RandomizerListFragment extends ListFragment implements GameSetupAdapterListener,
		CardPickerDialogFragmentListener {

	public static final String TAG = RandomizerListFragment.class.getName();

	private static final String INSTANCE_SELECTED_CARD_TYPE = "INSTANCE_SELECTED_CARD_TYPE";
	private static final String INSTANCE_SELECTED_CARD_INDEX = "INSTANCE_SELECTED_CARD_INDEX";
	private static final String INSTANCE_GAME_SETUP = "INSTANCE_GAME_SETUP";
	private static final String INSTANCE_BASE_GAME_SETUP = "INSTANCE_BASE_GAME_SETUP";

	private GameSetupAdapter mAdapter;

	private GameSetup mGameSetup;

	// We keep the base GameSetup around, in case we want to randomize multiple times in a row
	private GameSetup mBaseGameSetup;

	// Which index we're currently selecting for the card dialog fragment
	private Type mSelectCardType;
	private int mSelectCardIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		if (savedInstanceState == null) {
			mGameSetup = new GameSetup();
		}
		else {
			mGameSetup = new GameSetup(savedInstanceState.getBundle(INSTANCE_GAME_SETUP));

			if (savedInstanceState.containsKey(INSTANCE_BASE_GAME_SETUP)) {
				mBaseGameSetup = new GameSetup(savedInstanceState.getBundle(INSTANCE_BASE_GAME_SETUP));
			}

			if (savedInstanceState.containsKey(INSTANCE_SELECTED_CARD_TYPE)) {
				mSelectCardType = Type.values()[savedInstanceState.getInt(INSTANCE_SELECTED_CARD_TYPE)];
				mSelectCardIndex = savedInstanceState.getInt(INSTANCE_SELECTED_CARD_INDEX);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBundle(INSTANCE_GAME_SETUP, mGameSetup.toBundle());

		if (mBaseGameSetup != null) {
			outState.putBundle(INSTANCE_BASE_GAME_SETUP, mBaseGameSetup.toBundle());
		}

		if (mSelectCardType != null) {
			outState.putInt(INSTANCE_SELECTED_CARD_TYPE, mSelectCardType.ordinal());
		}
		outState.putInt(INSTANCE_SELECTED_CARD_INDEX, mSelectCardIndex);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new GameSetupAdapter(getActivity(), mGameSetup, this);
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
		inflater.inflate(R.menu.fragment_randomizer, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_randomize:
			if (mBaseGameSetup == null) {
				mBaseGameSetup = new GameSetup(mGameSetup);
			}

			Randomizer randomizer = new Randomizer(mBaseGameSetup);
			mGameSetup.updateFrom(randomizer.randomize());
			mAdapter.notifyDataSetChanged();
			return true;
		case R.id.action_reset:
			mGameSetup.reset();
			mBaseGameSetup = null;
			mAdapter.notifyDataSetChanged();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//////////////////////////////////////////////////////////////////////////
	// GameSetupAdapterListener

	@Override
	public void onAdd(Type type) {
		mGameSetup.addHero();
		mBaseGameSetup = null;
		mAdapter.notifyDataSetChanged();
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

		// We assume something has changed; reset the base game setup
		mBaseGameSetup = null;

		mAdapter.notifyDataSetChanged();
	}

}
