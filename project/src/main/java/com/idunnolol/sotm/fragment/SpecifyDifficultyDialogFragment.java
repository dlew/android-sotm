package com.idunnolol.sotm.fragment;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.NumberPicker;
import com.idunnolol.sotm.R;

public class SpecifyDifficultyDialogFragment extends DialogFragment {

    public static final String TAG = SpecifyDifficultyDialogFragment.class.getName();

    private SpecifyDifficultyDialogFragmentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (SpecifyDifficultyDialogFragmentListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Construct number picker
        final NumberPicker numPicker = new NumberPicker(getActivity());
        numPicker.setMinValue(0);
        numPicker.setMaxValue(100);
        numPicker.setValue(50);
        numPicker.setWrapSelectorWheel(false);

        // Build and return dialog
        return new Builder(getActivity())
            .setTitle(R.string.title_specify_difficulty)
            .setView(numPicker)
            .setPositiveButton(R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    numPicker.clearFocus();
                    mListener.onSpecificDifficultyChosen(numPicker.getValue());
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .create();
    }

    //////////////////////////////////////////////////////////////////////////
    // Listener

    public interface SpecifyDifficultyDialogFragmentListener {

        public void onSpecificDifficultyChosen(int targetWinPercent);
    }

}
