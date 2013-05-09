package com.idunnolol.sotm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.idunnolol.sotm.R;

public class DifficultyDialogFragment extends DialogFragment {

	public static final String TAG = DifficultyDialogFragment.class.getName();

	public enum Difficulty {
		RANDOM(0, R.string.difficulty_random),
		EASY(90, R.string.difficulty_easy),
		MEDIUM(75, R.string.difficulty_medium),
		HARD(50, R.string.difficulty_hard),
		VERY_HARD(30, R.string.difficulty_very_hard),
		IMPOSSIBLE(15, R.string.difficulty_impossible);

		private int mTargetWinPercent;
		private int mStrResId;

		private Difficulty(int targetWinPercent, int strResId) {
			mTargetWinPercent = targetWinPercent;
			mStrResId = strResId;
		}

		public int getTargetWinPercent() {
			return mTargetWinPercent;
		}

		public int getStrResId() {
			return mStrResId;
		}
	}

	private DifficultyDialogFragmentListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListener = (DifficultyDialogFragmentListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Construct items from Difficulty enum
		List<CharSequence> items = new ArrayList<CharSequence>();

		for (Difficulty difficulty : Difficulty.values()) {
			if (difficulty == Difficulty.RANDOM) {
				items.add(getString(difficulty.getStrResId()));
			}
			else {
				items.add(getString(R.string.template_win_rate, getString(difficulty.getStrResId()),
						difficulty.getTargetWinPercent()));
			}
		}

		builder.setItems(items.toArray(new CharSequence[0]), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mListener.onDifficultyChosen(Difficulty.values()[which]);
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		return builder.create();
	}

	//////////////////////////////////////////////////////////////////////////
	// Listener

	public interface DifficultyDialogFragmentListener {
		public void onDifficultyChosen(Difficulty difficulty);
	}
}
