package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.List;

public class CardSet {

    private String mId;

    private int mNameResId;

    // Only really matters on first run, but whether we
    // enable the cards in here by default.
    private boolean mEnabledByDefault;

    private List<Card> mCards = new ArrayList<Card>();

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

    public void setEnabledByDefault(boolean enabled) {
        mEnabledByDefault = enabled;
    }

    public boolean isEnabledByDefault() {
        return mEnabledByDefault;
    }

    public void addCard(Card card) {
        mCards.add(card);
    }

    /**
     * Like addCard(), but makes sure to add the advanced card at
     * the correct spot below the current non-advanced card.
     */
    public void addAdvancedCard(Card baseCard, Card advancedCard) {
        int size = mCards.size();
        for (int a = 0; a < size; a++) {
            if (mCards.get(a).equals(baseCard)) {
                mCards.add(a + 1, advancedCard);
            }
        }
    }

    public List<Card> getCards() {
        return mCards;
    }

    public int getCardCount() {
        return mCards.size();
    }

    public void setAllCardsEnabled(boolean enabled) {
        for (Card card : mCards) {
            card.setEnabled(enabled);
        }
    }

    public boolean areAllCardsEnabled() {
        for (Card card : mCards) {
            if (!card.isEnabled()) {
                return false;
            }
        }

        return true;
    }
}
