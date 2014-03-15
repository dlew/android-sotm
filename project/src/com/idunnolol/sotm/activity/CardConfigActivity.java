package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Db;
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

    @Override
    protected void onPause() {
        super.onPause();

        Db.saveCardStates(this);
    }

    //////////////////////////////////////////////////////////////////////////
    // Action bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_card_config, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
