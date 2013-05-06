package com.idunnolol.sotm.data;

import java.util.Collection;
import java.util.HashSet;

public class CardSet {

	private String mId;

	private int mNameResId;

	private Collection<Card> mCards = new HashSet<Card>();

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

	public void addCard(Card card) {
		mCards.add(card);
	}

	public Collection<Card> getCards() {
		return mCards;
	}
}
