package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.fragment.CardConfigListFragment;

public class CardConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            CardConfigListFragment fragment = new CardConfigListFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment, CardConfigListFragment.TAG)
                .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Db.saveCardStates(this);
    }

    //////////////////////////////////////////////////////////////////////////
    // Action bar

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
