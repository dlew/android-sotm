package com.idunnolol.sotm.data;

import java.util.Comparator;

import android.content.Context;

import com.idunnolol.sotm.R;

public class Card {

	/**
	 * Represents the special "random" card
	 */
	public static final Card RANDOM = new Card(null, "Random", R.string.card_random, 0);

	public static enum Type {
		HERO,
		VILLAIN,
		ENVIRONMENT,
	}

	private Type mType;

	private String mId;

	private int mNameResId;

	private int mPoints;

	private boolean mEnabled;

	public Card() {
		// Default constructor
	}

	public Card(Type type, String id, int nameResId, int points) {
		mType = type;
		mId = id;
		mNameResId = nameResId;
		mPoints = points;
		mEnabled = true;
	}

	public Type getType() {
		return mType;
	}

	public void setType(Type type) {
		mType = type;
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

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Card)) {
			return false;
		}

		Card other = (Card) o;
		return mId.equals(other.mId);
	}

	@Override
	public String toString() {
		return mId;
	}

	//////////////////////////////////////////////////////////////////////////
	// Comparators

	public static final Comparator<Card> POINT_COMPARATOR = new Comparator<Card>() {
		@Override
		public int compare(Card lhs, Card rhs) {
			return lhs.getPoints() - rhs.getPoints();
		}
	};

	public static Comparator<Card> getNameComparator(final Context context) {
		return new Comparator<Card>() {
			public int compare(Card lhs, Card rhs) {
				String lhName = context.getString(lhs.getNameResId());
				String rhName = context.getString(rhs.getNameResId());
				return lhName.compareTo(rhName);
			}
		};
	}

}
