package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.List;

import com.idunnolol.sotm.data.Card.Type;

import android.os.Bundle;

/**
 * Represents a setup of a game (i.e., which heroes are selected,
 * as well as villain and environment).
 */
public class GameSetup {

	private static final String KEY_HEROES = "KEY_HEROES";
	private static final String KEY_VILLAIN = "KEY_VILLAIN";
	private static final String KEY_ENVIRONMENT = "KEY_ENVIRONMENT";

	private static final int MIN_HEROES = 3;
	private static final int MAX_HEROES = 5;

	private List<Card> mHeroes = new ArrayList<Card>(5);

	private Card mVillain;

	private Card mEnvironment;

	public GameSetup() {
		// Default setup is 3 random heroes, 1 random villain and 1 random environment
		mHeroes.add(Card.RANDOM);
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

	public GameSetup(Bundle bundle) {
		fromBundle(bundle);
	}

	public void reset() {
		for (int a = 0; a < mHeroes.size(); a++) {
			mHeroes.set(a, Card.RANDOM);
		}
		mVillain = Card.RANDOM;
		mEnvironment = Card.RANDOM;
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

	public boolean canRemoveHero() {
		return mHeroes.size() > MIN_HEROES;
	}

	public boolean canAddHero() {
		return mHeroes.size() < MAX_HEROES;
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

	/**
	 * @return true if there is a random card in the setup, false if all cards are filled out
	 */
	public boolean hasRandomCards() {
		for (Card card : mHeroes) {
			if (card == Card.RANDOM) {
				return true;
			}
		}

		return mVillain == Card.RANDOM || mEnvironment == Card.RANDOM;
	}

	/**
	 * Calculates the points that this setup represents.  If there
	 * are random cards, it's just a rough estimate.
	 * 
	 * Random cards are calculated as the average of all their types
	 * (as any one of them could be returned).
	 */
	public int getPoints() {
		int points = 0;

		int avgHero = Db.getAvgPoints(Type.HERO);

		for (Card card : mHeroes) {
			if (card == Card.RANDOM) {
				points += avgHero;
			}
			else {
				points += card.getPoints();
			}
		}

		if (mVillain == Card.RANDOM) {
			points += Db.getAvgPoints(Type.VILLAIN);
		}
		else {
			points += mVillain.getPoints();
		}

		if (mEnvironment == Card.RANDOM) {
			points += Db.getAvgPoints(Type.ENVIRONMENT);
		}
		else {
			points += mEnvironment.getPoints();
		}

		// Factor in # of players
		points += Db.getPointsForNumPlayers(mHeroes.size());

		return points;
	}

	/**
	 * @return The estimated chance of winning, as an int 0-100
	 */
	public int getWinPercent() {
		return Db.getWinPercent(getPoints());
	}

	public void updateFrom(GameSetup other) {
		mHeroes.clear();
		mHeroes.addAll(other.mHeroes);
		mVillain = other.mVillain;
		mEnvironment = other.mEnvironment;
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();

		ArrayList<String> heroes = new ArrayList<String>();
		for (Card card : mHeroes) {
			heroes.add(card.getId());
		}

		bundle.putStringArrayList(KEY_HEROES, heroes);
		bundle.putString(KEY_VILLAIN, mVillain.getId());
		bundle.putString(KEY_ENVIRONMENT, mEnvironment.getId());

		return bundle;
	}

	public void fromBundle(Bundle bundle) {
		mHeroes.clear();
		for (String hero : bundle.getStringArrayList(KEY_HEROES)) {
			mHeroes.add(Db.getCard(hero));
		}

		mVillain = Db.getCard(bundle.getString(KEY_VILLAIN));
		mEnvironment = Db.getCard(bundle.getString(KEY_ENVIRONMENT));
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