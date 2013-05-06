package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.RandomizerListFragment;
import com.idunnolol.utils.Ui;

public class MainActivity extends Activity implements CardPickerDialogFragmentListener {

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//////////////////////////////////////////////////////////////////////////
	// CardPickerDialogFragmentListener

	@Override
	public void onCardSelected(Card card) {
		mRandomizerListFragment.onCardSelected(card);
	}

}
