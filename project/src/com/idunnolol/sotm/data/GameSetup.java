package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a setup of a game (i.e., which heroes are selected,
 * as well as villain and environment).
 */
public class GameSetup {

	private List<Card> mHeroes = new ArrayList<Card>(5);

	private Card mVillain;

	private Card mEnvironment;

	public GameSetup() {
		// Default setup is 2 random heroes, 1 random villain and 1 random environment
		mHeroes.add(Card.RANDOM);
		mHeroes.add(Card.RANDOM);
		mVillain = Card.RANDOM;
		mEnvironment = Card.RANDOM;
	}

	public GameSetup(GameSetup toCopy) {
		mHeroes.addAll(toCopy.mHeroes);
		mVillain = toCopy.mVillain;
		mEnvironment = toCopy.mEnvironment;
	}

	public List<Card> getHeroes() {
		return mHeroes;
	}

	public int getHeroCount() {
		return mHeroes.size();
	}

	public void addHero() {
		mHeroes.add(Card.RANDOM);
	}

	public void setHero(int index, Card card) {
		mHeroes.set(index, card);
	}

	public void removeHero(int index) {
		mHeroes.remove(index);
	}

	public void setVillain(Card card) {
		mVillain = card;
	}

	public Card getVillain() {
		return mVillain;
	}

	public void setEnvironment(Card card) {
		mEnvironment = card;
	}

	public Card getEnvironment() {
		return mEnvironment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Heroes: ");
		int heroCount = mHeroes.size();
		for (int a = 0; a < heroCount; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(mHeroes.get(a).getId());
		}

		sb.append("\nVillain: " + mVillain.getId());

		sb.append("\nEnvironment: " + mEnvironment.getId());

		return sb.toString();
	}

}