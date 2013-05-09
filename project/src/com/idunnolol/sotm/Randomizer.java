package com.idunnolol.sotm;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.util.Pair;

import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.utils.Log;

public class Randomizer {

	// Plus/minus this value for difficulty
	private static final int DIFFICULTY_FUZZ = 4;

	// Randomization timeout in nanoseconds (200 ms)
	private static final long RANDOMIZE_TIMEOUT = 200 * 1000000;

	private Random mRand;

	// The base game setup; when we randomize, we fill in any cards set to RANDOM
	private GameSetup mBaseGameSetup;

	public Randomizer(GameSetup baseGameSetup) {
		mBaseGameSetup = baseGameSetup;
		mRand = new Random();
	}

	public GameSetup randomize() {
		GameSetup gameSetup = new GameSetup(mBaseGameSetup);

		// Go through all heroes, villains and environments
		// and randomly select cards to fill in when the
		// card is "random"
		List<Card> heroes = gameSetup.getHeroes();
		Set<Card> usedCards = new HashSet<Card>();
		int size = heroes.size();

		// Fill the initial used cards
		for (Card hero : heroes) {
			if (hero != Card.RANDOM) {
				usedCards.add(hero);
				usedCards.addAll(hero.getAlternates());
			}
		}

		for (int a = 0; a < size; a++) {
			Card hero = heroes.get(a);
			if (hero == Card.RANDOM) {
				Card card;
				do {
					card = getRandomCard(Type.HERO);
				}
				while (usedCards.contains(card));

				gameSetup.setHero(a, card);
				usedCards.add(card);
				usedCards.addAll(card.getAlternates());
			}
		}

		if (gameSetup.getVillain() == Card.RANDOM) {
			gameSetup.setVillain(getRandomCard(Type.VILLAIN));
		}

		if (gameSetup.getEnvironment() == Card.RANDOM) {
			gameSetup.setEnvironment(getRandomCard(Type.ENVIRONMENT));
		}

		return gameSetup;
	}

	/**
	 * Here's how we do randomization with a target win %.  It is extremely
	 * lazy and cheap.
	 * 
	 * First, we have a fuzzing range for the acceptable points.  This
	 * is to introduce a bit of variety, otherwise you will commonly
	 * end up with the same setups for various difficulties.
	 * 
	 * We randomly fill in the base set until we hit an acceptable point
	 * value.  We keep retrying for a certain amount of time.  This method
	 * is used because it's easy and I'm lazy.
	 * 
	 * If we fail to fill the random slots, then we fallback to the closest
	 * game setup we found before failing.
	 * 
	 * @param targetWinPercent
	 * @return
	 */
	public GameSetup randomize(int targetWinPercent) {
		int minAcceptableWinPercent = targetWinPercent - DIFFICULTY_FUZZ;
		int maxAcceptableWinPercent = targetWinPercent + DIFFICULTY_FUZZ;

		Pair<Integer, Integer> pair = Db.getPointRange(minAcceptableWinPercent, maxAcceptableWinPercent);
		int minPoints = pair.first;
		int maxPoints = pair.second;

		long start = System.nanoTime();
		GameSetup currGameSetup;
		int minDistance = Integer.MAX_VALUE;
		GameSetup bestGameSetup = null;

		int numTimes = 0;
		do {
			numTimes++;

			currGameSetup = randomize();
			int currPoints = currGameSetup.getPoints();

			if (currPoints > maxPoints) {
				if (currPoints - maxPoints < minDistance) {
					minDistance = currPoints - maxPoints;
					bestGameSetup = currGameSetup;
				}
			}
			else if (currPoints < minPoints) {
				if (minPoints - currPoints < minDistance) {
					minDistance = minPoints - currPoints;
					bestGameSetup = currGameSetup;
				}
			}
			else {
				bestGameSetup = currGameSetup;
				break;
			}
		}
		while (System.nanoTime() - start < RANDOMIZE_TIMEOUT);

		Log.i("Range[" + minPoints + ", " + maxPoints + "] - found " + bestGameSetup.getPoints() + " (after "
				+ numTimes + " tries)");

		return bestGameSetup;
	}

	private Card getRandomCard(Type type) {
		List<Card> cards = Db.getCards(type);
		int index = mRand.nextInt(cards.size());
		return cards.get(index);
	}
}
