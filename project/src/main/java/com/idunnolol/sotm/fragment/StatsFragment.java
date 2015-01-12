package com.idunnolol.sotm.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.danlew.utils.Ui;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.GameSetup;

public class StatsFragment extends Fragment {

    private StatsFragmentListener mListener;

    private TextView mStatsTextView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (StatsFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        mStatsTextView = Ui.findView(root, R.id.stats_text_view);

        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStatsClick();
            }
        });

        return root;
    }

    public void bind(GameSetup gameSetup) {
        String str;
        if (!gameSetup.canRandomize()) {
            str = getString(NotEnoughCardsDialogFragment.getErrorResId(gameSetup.getFirstLackingType()));
        }
        else {
            if (gameSetup.hasRandomCards()) {
                Pair<Integer, Integer> winPointRange = gameSetup.getPointRange();
                int low = Db.getWinPercent(winPointRange.second);
                int high = Db.getWinPercent(winPointRange.first);
                str = getString(R.string.template_win_range, low, high);
            }
            else {
                str = getString(R.string.template_win_chance, gameSetup.getWinPercent());
            }
        }

        mStatsTextView.setText(Html.fromHtml(str));
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener

    public interface StatsFragmentListener {

        public void onStatsClick();
    }
}
