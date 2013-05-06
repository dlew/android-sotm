package com.idunnolol.sotm.data;

import java.util.Collection;
import java.util.HashSet;

public class CardSet {

	private String mName;

	private Collection<Card> mCards = new HashSet<Card>();

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public void addCard(Card card) {
		mCards.add(card);
	}

	public Collection<Card> getCards() {
		return mCards;
	}
}
