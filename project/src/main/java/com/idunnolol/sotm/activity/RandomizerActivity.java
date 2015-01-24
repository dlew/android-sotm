package com.idunnolol.sotm.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.danlew.utils.Log;
import com.danlew.utils.Ui;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Difficulty;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.data.Prefs;
import com.idunnolol.sotm.fragment.AboutDialogFragment;
import com.idunnolol.sotm.fragment.CardPickerDialogFragment.CardPickerDialogFragmentListener;
import com.idunnolol.sotm.fragment.DifficultyDialogFragment.DifficultyDialogFragmentListener;
import com.idunnolol.sotm.fragment.RandomizerListFragment;
import com.idunnolol.sotm.fragment.RandomizerListFragment.RandomizerListFragmentListener;
import com.idunnolol.sotm.fragment.SpecifyDifficultyDialogFragment.SpecifyDifficultyDialogFragmentListener;
import com.idunnolol.sotm.fragment.StatsFragment;
import com.idunnolol.sotm.fragment.StatsFragment.StatsFragmentListener;
import com.idunnolol.sotm.sync.AccountUtils;

public class RandomizerActivity extends Activity implements RandomizerListFragmentListener,
    CardPickerDialogFragmentListener, DifficultyDialogFragmentListener, SpecifyDifficultyDialogFragmentListener,
    StatsFragmentListener {

    private RandomizerListFragment mRandomizerListFragment;
    private StatsFragment mStatsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_randomizer);

        setContentView(R.layout.activity_randomizer);

        mRandomizerListFragment = Ui.findFragment(this, R.id.randomizer_list_fragment);
        mStatsFragment = Ui.findFragment(this, R.id.stats_fragment);

        if (savedInstanceState == null) {
            // Make sure we have a sync account setup and that it's set to sync
            AccountUtils.addSyncAccount(this);
            AccountUtils.startPeriodicSync();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getContentResolver().registerContentObserver(AccountUtils.SYNC_URI, false, mSyncObserver);

        mStatsFragment.bind(mRandomizerListFragment.getGameSetup());
    }

    @Override
    protected void onPause() {
        super.onPause();

        getContentResolver().unregisterContentObserver(mSyncObserver);
    }

    //////////////////////////////////////////////////////////////////////////
    // Action bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_randomizer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_advanced).setChecked(Prefs.isAdvancedAllowed());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configure:
                startActivity(new Intent(this, CardConfigActivity.class));
                return true;
            case R.id.action_advanced:
                Prefs.setAdvancedAllowed(!item.isChecked());
                mRandomizerListFragment.validateGameSetup();
                return true;
            case R.id.action_about:
                AboutDialogFragment df = new AboutDialogFragment();
                df.show(getFragmentManager(), AboutDialogFragment.TAG);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////////////////////
    // Syncing

    private ContentObserver mSyncObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.i("Points info updated, re-binding data");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatsFragment.bind(mRandomizerListFragment.getGameSetup());
                }
            });
        }
    };

    //////////////////////////////////////////////////////////////////////////
    // CardPickerDialogFragmentListener

    @Override
    public void onCardSelected(Card card) {
        mRandomizerListFragment.onCardSelected(card);
    }

    //////////////////////////////////////////////////////////////////////////
    // DifficultyDialogFragmentListener

    @Override
    public void onDifficultyChosen(Difficulty difficulty) {
        mRandomizerListFragment.onDifficultyChosen(difficulty);
    }

    //////////////////////////////////////////////////////////////////////////
    // SpecifyDifficultyDialogFragmentListener

    @Override
    public void onSpecificDifficultyChosen(int targetWinPercent) {
        mRandomizerListFragment.onSpecificDifficultyChosen(targetWinPercent);
    }

    //////////////////////////////////////////////////////////////////////////
    // RandomizerListFragmentListener

    @Override
    public void onGameSetupChanged(GameSetup gameSetup) {
        mStatsFragment.bind(gameSetup);
    }

    //////////////////////////////////////////////////////////////////////////
    // StatsFragmentListener

    @Override
    public void onStatsClick() {
        mRandomizerListFragment.launchRandomizerDialog(true);
    }

}
