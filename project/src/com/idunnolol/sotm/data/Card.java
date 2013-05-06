package com.idunnolol.sotm.data;

import com.idunnolol.sotm.R;

public class Card {

	/**
	 * Represents the special "random" card
	 */
	public static final Card RANDOM = new Card("Random", R.string.card_random, 0);

	private String mId;

	private int mNameResId;

	private int mPoints;

	public Card() {
		// Default constructor
	}

	public Card(String id, int nameResId, int points) {
		mId = id;
		mNameResId = nameResId;
		mPoints = points;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public void setNameResId(int resId) {
		mNameResId = resId;
	}

	public int getNameResId() {
		return mNameResId;
	}

	public int getPoints() {
		return mPoints;
	}

	public void setPoints(int points) {
		mPoints = points;
	}

}
