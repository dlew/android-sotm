package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.RandomizerListFragment;
import com.idunnolol.utils.Ui;

public class RandomizerActivity extends Activity implements CardPickerDialogFragmentListener {

	private RandomizerListFragment mRandomizerListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			mRandomizerListFragment = new RandomizerListFragment();
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, mRandomizerListFragment, RandomizerListFragment.TAG).commit();
		}
		else {
			mRandomizerListFragment = Ui.findFragment(this, RandomizerListFragment.TAG);
		}
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

}
