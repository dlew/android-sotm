package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;

import com.idunnolol.sotm.fragment.CardConfigListFragment;

public class CardConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			CardConfigListFragment fragment = new CardConfigListFragment();
			getFragmentManager().beginTransaction().add(android.R.id.content, fragment, CardConfigListFragment.TAG)
					.commit();
		}
	}

}
