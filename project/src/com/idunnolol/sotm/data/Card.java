package com.idunnolol.sotm.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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

	// Alternate promo cards, use same set but different cover cards (both cards will reference each other)
	private Set<Card> mAlternates = new HashSet<Card>();

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

	public void addAlternate(Card card) {
		// We want all cards to be listed as alternates of each other,
		// but don't want an infinite loop

		// Add all of the new cards' alternates to this one
		mAlternates.add(card);
		mAlternates.addAll(card.mAlternates);

		// Go through all alternates and add this as an alternate
		for (Card alt : mAlternates) {
			alt.mAlternates.add(this);
		}
	}

	public boolean hasAlternates() {
		return mAlternates.size() != 0;
	}

	public Collection<Card> getAlternates() {
		return mAlternates;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mId);
		sb.append(" points=");
		sb.append(mPoints);
		sb.append(" enabled=");
		
		if (mAlternates.size() != 0) {
			sb.append(" alts=[");
			boolean first = true;
			for (Card alt : mAlternates) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(alt.getId());
				first = false;
			}
			sb.append("]");
		}

		return sb.toString();
	}

}
