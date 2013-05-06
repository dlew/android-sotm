package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.fragment.PickerListFragment;
import com.idunnolol.utils.Ui;

public class MainActivity extends Activity {

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
