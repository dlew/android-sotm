package com.idunnolol.sotm.fragment;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
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
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.DifficultyDialogFragment.Difficulty;
import com.idunnolol.sotm.fragment.DifficultyDialogFragment.DifficultyDialogFragmentListener;
import com.idunnolol.sotm.fragment.SpecifyDifficultyDialogFragment.SpecifyDifficultyDialogFragmentListener;
import com.idunnolol.sotm.widget.GameSetupAdapter;
import com.idunnolol.sotm.widget.GameSetupAdapter.GameSetupAdapterListener;

public class RandomizerListFragment extends ListFragment implements GameSetupAdapterListener,
		CardPickerDialogFragmentListener, DifficultyDialogFragmentListener, SpecifyDifficultyDialogFragmentListener {

	public static final String TAG = RandomizerListFragment.class.getName();

	private static final String INSTANCE_SELECTED_CARD_TYPE = "INSTANCE_SELECTED_CARD_TYPE";
	private static final String INSTANCE_SELECTED_CARD_INDEX = "INSTANCE_SELECTED_CARD_INDEX";
	private static final String INSTANCE_GAME_SETUP = "INSTANCE_GAME_SETUP";
	private static final String INSTANCE_BASE_GAME_SETUP = "INSTANCE_BASE_GAME_SETUP";

	private RandomizerListFragmentListener mListener;

	private GameSetupAdapter mAdapter;

	private GameSetup mGameSetup;

	// We keep the base GameSetup around, in case we want to randomize multiple times in a row
	private int mTargetWinPercent;
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListener = (RandomizerListFragmentListener) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new GameSetupAdapter(getActivity(), mGameSetup, this);
		setListAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Check that all selected cards are still enabled
		// (May have been disabled in card config activity)
		boolean hasChanged = false;
		List<Card> heroes = mGameSetup.getHeroes();
		for (int a = 0; a < heroes.size(); a++) {
			if (!heroes.get(a).isEnabled()) {
				mGameSetup.setHero(a, Card.RANDOM);
				hasChanged = true;
			}
		}
		if (!mGameSetup.getVillain().isEnabled()) {
			mGameSetup.setVillain(Card.RANDOM);
			hasChanged = true;
		}
		if (!mGameSetup.getEnvironment().isEnabled()) {
			mGameSetup.setEnvironment(Card.RANDOM);
			hasChanged = true;
		}

		if (hasChanged) {
			onGameSetupChanged();
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		mSelectCardType = mAdapter.getType(position);

		// Check that we even have something to select from
		if (Db.getCards(mSelectCardType).size() == 0) {
			DialogFragment df = NotEnoughCardsDialogFragment.newInstance(mSelectCardType);
			df.show(getFragmentManager(), NotEnoughCardsDialogFragment.TAG);
		}
		else {
			int start = mAdapter.getTypeStart(mSelectCardType);
			mSelectCardIndex = position - start - 1;

			CardPickerDialogFragment dialogFragment = CardPickerDialogFragment.newInstance(mSelectCardType, mGameSetup);
			dialogFragment.show(getFragmentManager(), CardPickerDialogFragment.TAG);
		}
	}

	public GameSetup getGameSetup() {
		return mGameSetup;
	}

	private void onGameSetupChanged() {
		mBaseGameSetup = null;
		mAdapter.notifyDataSetChanged();
		mListener.onGameSetupChanged(mGameSetup);
		getActivity().invalidateOptionsMenu();
	}

	private void randomize(int targetWinPercent) {
		GameSetup baseGameSetup = mBaseGameSetup;
		if (baseGameSetup == null) {
			baseGameSetup = new GameSetup(mGameSetup);
		}

		Randomizer randomizer = new Randomizer(baseGameSetup);
		if (baseGameSetup.canRandomize()) {
			if (targetWinPercent == Difficulty.RANDOM.getTargetWinPercent()) {
				mGameSetup.updateFrom(randomizer.randomize());
			}
			else {
				mGameSetup.updateFrom(randomizer.randomize(targetWinPercent));
			}

			onGameSetupChanged();
			mBaseGameSetup = baseGameSetup;
			mTargetWinPercent = targetWinPercent;
		}
		else {
			DialogFragment df = NotEnoughCardsDialogFragment.newInstance(baseGameSetup.getFirstLackingType());
			df.show(getFragmentManager(), NotEnoughCardsDialogFragment.TAG);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Action bar

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_randomizer, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.findItem(R.id.action_reroll).setVisible(mBaseGameSetup != null);
		menu.findItem(R.id.action_reset).setVisible(mBaseGameSetup != null);
		menu.findItem(R.id.action_randomize).setVisible(mBaseGameSetup == null && mGameSetup.hasRandomCards());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_randomize:
			if (!mGameSetup.canRandomize()) {
				DialogFragment df = NotEnoughCardsDialogFragment.newInstance(mGameSetup.getFirstLackingType());
				df.show(getFragmentManager(), NotEnoughCardsDialogFragment.TAG);
			}
			else {
				DifficultyDialogFragment df = new DifficultyDialogFragment();
				df.show(getFragmentManager(), DifficultyDialogFragment.TAG);
			}
			return true;
		case R.id.action_reroll:
			randomize(mTargetWinPercent);
			return true;
		case R.id.action_reset:
			mGameSetup.reset();
			onGameSetupChanged();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//////////////////////////////////////////////////////////////////////////
	// GameSetupAdapterListener

	@Override
	public void onAdd(Type type) {
		mGameSetup.addHero();
		onGameSetupChanged();
	}

	@Override
	public void onRemove(Type type, int index) {
		mGameSetup.removeHero(index);
		onGameSetupChanged();
	}

	//////////////////////////////////////////////////////////////////////////
	// CardPickerDialogFragmentListener

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

		onGameSetupChanged();
	}

	//////////////////////////////////////////////////////////////////////////
	// DifficultyDialogFragmentListener

	@Override
	public void onDifficultyChosen(Difficulty difficulty) {
		if (difficulty == Difficulty.PICK_YOUR_OWN) {
			SpecifyDifficultyDialogFragment df = new SpecifyDifficultyDialogFragment();
			df.show(getFragmentManager(), SpecifyDifficultyDialogFragment.TAG);
		}
		else {
			randomize(difficulty.getTargetWinPercent());
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// SpecifyDifficultyDialogFragmentListener

	@Override
	public void onSpecificDifficultyChosen(int targetWinPercent) {
		randomize(targetWinPercent);
	}

	//////////////////////////////////////////////////////////////////////////
	// Interface

	public interface RandomizerListFragmentListener {
		public void onGameSetupChanged(GameSetup gameSetup);
	}

}
