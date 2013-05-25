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

	public List<Card> getCards() {
		return mCards;
	}

	public int getCardCount() {
		return mCards.size();
	}

	public void setAllCardsEnabled(boolean enabled, boolean includeAdvanced) {
		for (Card card : mCards) {
			if (includeAdvanced || !card.isAdvanced()) {
				card.setEnabled(enabled);
			}
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
