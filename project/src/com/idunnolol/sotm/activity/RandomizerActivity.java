package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.RandomizerListFragment;
import com.idunnolol.sotm.fragment.RandomizerListFragment.RandomizerListFragmentListener;
import com.idunnolol.sotm.fragment.StatsFragment;
import com.idunnolol.utils.Ui;

public class RandomizerActivity extends Activity implements RandomizerListFragmentListener,
		CardPickerDialogFragmentListener {

	private RandomizerListFragment mRandomizerListFragment;
	private StatsFragment mStatsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_randomizer);

		mRandomizerListFragment = Ui.findFragment(this, R.id.randomizer_list_fragment);
		mStatsFragment = Ui.findFragment(this, R.id.stats_fragment);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mStatsFragment.bind(mRandomizerListFragment.getGameSetup());
	}

	//////////////////////////////////////////////////////////////////////////
	// Action bar

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_randomizer, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_configure:
			startActivity(new Intent(this, CardConfigActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//////////////////////////////////////////////////////////////////////////
	// CardPickerDialogFragmentListener

	@Override
	public void onCardSelected(Card card) {
		mRandomizerListFragment.onCardSelected(card);
	}

	//////////////////////////////////////////////////////////////////////////
	// RandomizerListFragmentListener

	@Override
	public void onGameSetupChanged(GameSetup gameSetup) {
		mStatsFragment.bind(gameSetup);
	}

}
