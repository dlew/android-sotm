package com.idunnolol.sotm.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.idunnolol.sotm.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Card implements Parcelable {

    private static final String ADVANCED_POSTFIX = " (Advanced)";

    private static final String RANDOM_ID = "Random";

    // Minimum number of advanced games counted before we will use it
    private static final int ADVANCED_COUNT_MINIMUM = 50;

    public static final Card RANDOM_HERO =
        new Card(Type.HERO, RANDOM_ID, R.string.card_random_hero, 0);

    public static final Card RANDOM_VILLAIN =
        new Card(Type.VILLAIN, RANDOM_ID, R.string.card_random_villain, 0);

    public static final Card RANDOM_ENVIRONMENT =
        new Card(Type.ENVIRONMENT, RANDOM_ID, R.string.card_random_environment, 0);

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

    private int mAdvancedPoints;

    // If this is an advanced card, this is the # of advanced games logged
    // with the card.  We want to cut off a low # because of how unreliable
    // the data can be.
    private int mAdvancedCount;

    /**
     * Some "cards" are actually a set of cards, i.e. the Vengeance Five.
     */
    private List<Card> mTeam;

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
        mAdvancedPoints = other.mAdvancedPoints;
        mAdvancedCount = other.mAdvancedCount;
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
        return mEnabled && (!isAdvanced() || canBeAdvanced());
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isAdvanced() {
        return mAdvanced;
    }

    public void makeAdvanced() {
        mAdvanced = true;
        mId = getAdvancedId();
    }

    public void setAdvancedPoints(int advancedPoints) {
        mAdvancedPoints = advancedPoints;
    }

    public int getAdvancedPoints() {
        if (mAdvancedPoints == 0) {
            return mPoints;
        }
        return mAdvancedPoints;
    }

    public void setAdvancedCount(int count) {
        mAdvancedCount = count;
    }

    public int getAdvancedCount() {
        return mAdvancedCount;
    }

    public boolean canBeAdvanced() {
        return mAdvancedCount >= ADVANCED_COUNT_MINIMUM && Prefs.isAdvancedAllowed();
    }

    public void addTeamMember(Card teamMember) {
        if (mTeam == null) {
            mTeam = new ArrayList<Card>();
        }
        mTeam.add(teamMember);
    }

    public boolean isTeam() {
        return mTeam != null;
    }

    public List<Card> getTeamMembers() {
        return mTeam;
    }

    public boolean isRandom() {
        return mId.equals(RANDOM_ID) || isTeam();
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

    public static final Comparator<Card> POINT_ADVANCED_COMPARATOR = new Comparator<Card>() {
        @Override
        public int compare(Card lhs, Card rhs) {
            return lhs.getAdvancedPoints() - rhs.getAdvancedPoints();
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
        mAdvancedPoints = in.readInt();
        mAdvancedCount = in.readInt();
        mTeam = in.readArrayList(getClass().getClassLoader());
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
        dest.writeInt(mAdvancedPoints);
        dest.writeInt(mAdvancedCount);
        dest.writeList(mTeam);
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
