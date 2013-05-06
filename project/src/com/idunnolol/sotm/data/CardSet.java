package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CardSet {

	private String mId;

	private int mNameResId;

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

	public void addCard(Card card) {
		mCards.add(card);
	}

	public List<Card> getCards() {
		return mCards;
	}

	public int getCardCount() {
		return mCards.size();
	}
}
