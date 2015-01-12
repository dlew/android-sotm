package com.idunnolol.sotm.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Difficulty;
import com.idunnolol.sotm.widget.DifficultyAdapter;

public class DifficultyDialogFragment extends DialogFragment {

    public static final String TAG = DifficultyDialogFragment.class.getName();

    private DifficultyDialogFragmentListener mListener;

    private DifficultyAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (DifficultyDialogFragmentListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAdapter = new DifficultyAdapter(getActivity());

        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.title_difficulty)
            .setSingleChoiceItems(mAdapter, 0, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissAllowingStateLoss();
                    mListener.onDifficultyChosen(mAdapter.getItem(which));
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .create();
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener

    public interface DifficultyDialogFragmentListener {

        public void onDifficultyChosen(Difficulty difficulty);
    }
}
