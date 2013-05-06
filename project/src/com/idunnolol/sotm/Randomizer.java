package com.idunnolol.sotm;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.GameSetup;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.Card.Type;

public class Randomizer {

	private GameSetup mBaseGameSetup;

	private Random mRand;

	private Card[] mAllCards = Db.getCards().toArray(new Card[0]);

	public Randomizer(GameSetup baseGameSetup) {
		mBaseGameSetup = baseGameSetup;
		mRand = new Random();
	}

	public GameSetup randomize() {
		GameSetup gameSetup = new GameSetup(mBaseGameSetup);

		// Go through all heroes, villains and environments
		// and randomly select cards to fill in when the
		// card is "random"
		Set<Card> usedCards = new HashSet<Card>();
		List<Card> heroes = gameSetup.getHeroes();
		int size = heroes.size();
		for (int a = 0; a < size; a++) {
			Card hero = heroes.get(a);
			if (hero != Card.RANDOM) {
				usedCards.add(hero);
			}
			else {
				Card card;
				do {
					card = getRandomCard(Type.HERO);
				}
				while (usedCards.contains(card));

				gameSetup.setHero(a, card);
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

	// We theorize that the random selection process is fast, so we
	// don't bother to separate card types when we randomize.
	// This will probably change as the randomizer gets more
	// intelligent.
	private Card getRandomCard(Type type) {
		Card card;
		do {
			int index = mRand.nextInt(mAllCards.length);
			card = mAllCards[index];
		}
		while (card.getType() != type);

		return card;
	}
}
