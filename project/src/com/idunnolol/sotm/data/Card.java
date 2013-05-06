package com.idunnolol.sotm.data;

public class Card {

	/**
	 * Represents the special "random" card
	 */
	public static final Card RANDOM = new Card();

	private String mName;

	private int mPoints;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getPoints() {
		return mPoints;
	}

	public void setPoints(int points) {
		mPoints = points;
	}

}
