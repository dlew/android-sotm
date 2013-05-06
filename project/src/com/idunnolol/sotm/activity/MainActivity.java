package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.Randomizer;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.fragment.PickerListFragment;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.PickerListFragment.PickerListFragmentListener;
import com.idunnolol.utils.Log;
import com.idunnolol.utils.Ui;

public class MainActivity extends Activity implements PickerListFragmentListener, CardPickerDialogFragmentListener {

	private PickerListFragment mPickerListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			mPickerListFragment = new PickerListFragment();
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, mPickerListFragment, PickerListFragment.TAG).commit();
		}
		else {
			mPickerListFragment = Ui.findFragment(this, PickerListFragment.TAG);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//////////////////////////////////////////////////////////////////////////
	// PickerListFragmentListener

	@Override
	public void onRandomize(GameSetup gameSetup) {
		Randomizer randomizer = new Randomizer(gameSetup);
		randomizer.setIncludeAlternatives(false);
		GameSetup finalGameSetup = randomizer.randomize();
		Log.i("Randomized game setup:\n" + finalGameSetup);
	}

	//////////////////////////////////////////////////////////////////////////
	// CardPickerDialogFragmentListener

	@Override
	public void onCardSelected(Card card) {
		mPickerListFragment.onCardSelected(card);
	}

}
