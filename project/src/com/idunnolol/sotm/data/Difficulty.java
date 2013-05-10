package com.idunnolol.sotm.data;

import com.idunnolol.sotm.R;

public enum Difficulty {

	RANDOM(-1, R.string.difficulty_random),
	EASY(90, R.string.difficulty_easy),
	MEDIUM(75, R.string.difficulty_medium),
	HARD(50, R.string.difficulty_hard),
	VERY_HARD(30, R.string.difficulty_very_hard),
	GOOD_LUCK(15, R.string.difficulty_good_luck),
	PICK_YOUR_OWN(-1, R.string.difficulty_specify);

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