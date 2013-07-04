package com.idunnolol.sotm.data;

import java.util.Comparator;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.idunnolol.sotm.R;

public class Card implements Parcelable {

	private static final String ADVANCED_POSTFIX = " (Advanced)";

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

	private int mIconResId;

	private int mPoints;

	private boolean mEnabled;

	private boolean mAdvanced;

	// If this is an advanced card, this is the # of advanced games logged
	// with the card.  We want to cut off a low # because of how unreliable
	// the data can be.
	private int mAdvancedCount;

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

	public Card(Card other) {
		mType = other.mType;
		mId = other.mId;
		mNameResId = other.mNameResId;
		mIconResId = other.mIconResId;
		mPoints = other.mPoints;
		mEnabled = other.mEnabled;
		mAdvanced = other.mAdvanced;
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

	public CharSequence getName(Context context) {
		CharSequence name = context.getString(mNameResId);
		return mAdvanced ? context.getString(R.string.advanced_TEMPLATE, name) : name;
	}

	public void setIconResId(int resId) {
		mIconResId = resId;
	}

	public int getIconResId() {
		return mIconResId;
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

	public boolean isAdvanced() {
		return mAdvanced;
	}

	public void makeAdvanced() {
		if (!mAdvanced) {
			mAdvanced = true;
			mId = getAdvancedId();
		}
	}

	public void setAdvanced(boolean advanced) {
		mAdvanced = advanced;
	}

	public void setAdvancedCount(int count) {
		mAdvancedCount = count;
	}

	public int getAdvancedCount() {
		return mAdvancedCount;
	}

	public boolean isRandom() {
		return this.equals(Card.RANDOM);
	}

	/**
	 * @return the advanced version of this card
	 */
	public String getAdvancedId() {
		if (mId.endsWith(ADVANCED_POSTFIX)) {
			return mId;
		}
		else {
			return mId + ADVANCED_POSTFIX;
		}
	}

	@Override
	public int hashCode() {
		return mId.hashCode();
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
				int comp = lhName.compareTo(rhName);

				// When comparing names, advanced always comes below the
				// non-advanced version of the card.
				if (comp == 0) {
					if (lhs.isAdvanced()) {
						return 1;
					}
					else if (rhs.isAdvanced()) {
						return -1;
					}
				}

				return comp;
			}
		};
	}

	//////////////////////////////////////////////////////////////////////////
	// Parcelable

	private Card(Parcel in) {
		boolean hasType = in.readByte() == 1;
		if (hasType) {
			mType = Type.values()[in.readInt()];
		}
		mId = in.readString();
		mNameResId = in.readInt();
		mIconResId = in.readInt();
		mPoints = in.readInt();
		mEnabled = in.readByte() == 1;
		mAdvanced = in.readByte() == 1;
		mAdvancedCount = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (mType != null) {
			dest.writeByte((byte) 1);
			dest.writeInt(mType.ordinal());
		}
		else {
			dest.writeByte((byte) 0);
		}
		dest.writeString(mId);
		dest.writeInt(mNameResId);
		dest.writeInt(mIconResId);
		dest.writeInt(mPoints);
		dest.writeByte((byte) (mEnabled ? 1 : 0));
		dest.writeByte((byte) (mAdvanced ? 1 : 0));
		dest.writeInt(mAdvancedCount);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
		public Card createFromParcel(Parcel in) {
			return new Card(in);
		}

		public Card[] newArray(int size) {
			return new Card[size];
		}
	};

}
