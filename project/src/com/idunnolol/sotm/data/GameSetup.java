package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.util.Pair;

import com.idunnolol.sotm.data.Card.Type;

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
	 * We might not be able to randomize if there aren't enough options available
	 */
	public boolean canRandomize() {
		return getFirstLackingType() == null;
	}

	/**
	 * @return the first type without enough enabled cards, or null if we're good to go
	 */
	public Type getFirstLackingType() {
		if (Db.getCards(Type.HERO).size() < getHeroCount()) {
			return Type.HERO;
		}
		else if (Db.getCards(Type.VILLAIN).size() == 0) {
			return Type.VILLAIN;
		}
		else if (Db.getCards(Type.ENVIRONMENT).size() == 0) {
			return Type.ENVIRONMENT;
		}

		return null;
	}

	/**
	 * Calculates the points that this setup represents.  If there
	 * are random cards, it counts them as 0.
	 */
	public int getPoints() {
		int points = 0;

		for (Card card : mHeroes) {
			if (card != Card.RANDOM) {
				points += card.getPoints();
			}
		}

		if (mVillain != Card.RANDOM) {
			points += mVillain.getPoints();
		}

		if (mEnvironment != Card.RANDOM) {
			points += mEnvironment.getPoints();
		}

		// Factor in # of players
		points += Db.getPointsForNumPlayers(mHeroes.size());

		return points;
	}

	/**
	 * Gets the possible range of points (if all cards were filled in with
	 * their highest/lowest scoring items).
	 * 
	 * If there are no random cards, the range is 0.
	 */
	public Pair<Integer, Integer> getPointRange() {
		return new Pair<Integer, Integer>(getPoints(true), getPoints(false));
	}

	// Returns either the minimum or maximum points possible with this game setup.
	private int getPoints(boolean minPoints) {
		int points = getPoints();

		List<Card> possibleHeroes = Db.getCards(Type.HERO);
		Collections.sort(possibleHeroes, Card.POINT_COMPARATOR);
		for (Card hero : mHeroes) {
			if (hero == Card.RANDOM) {
				if (minPoints) {
					points += possibleHeroes.get(0).getPoints();
					possibleHeroes.remove(0);
				}
				else {
					points += possibleHeroes.get(possibleHeroes.size() - 1).getPoints();
					possibleHeroes.remove(possibleHeroes.size() - 1);
				}
			}
		}

		if (mVillain == Card.RANDOM) {
			List<Card> possibleVillains = Db.getCards(Type.VILLAIN);
			Collections.sort(possibleVillains, Card.POINT_COMPARATOR);

			if (minPoints) {
				points += possibleVillains.get(0).getPoints();
			}
			else {
				points += possibleVillains.get(possibleVillains.size() - 1).getPoints();
			}
		}

		if (mEnvironment == Card.RANDOM) {
			List<Card> possibleEnvironments = Db.getCards(Type.ENVIRONMENT);
			Collections.sort(possibleEnvironments, Card.POINT_COMPARATOR);

			if (minPoints) {
				points += possibleEnvironments.get(0).getPoints();
			}
			else {
				points += possibleEnvironments.get(possibleEnvironments.size() - 1).getPoints();
			}
		}

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