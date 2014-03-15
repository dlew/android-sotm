package com.idunnolol.sotm;

import android.util.Pair;
import com.danlew.utils.Log;
import com.idunnolol.sotm.data.Card;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.GameSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Randomizer {

    // Plus/minus this value for difficulty
    private static final int DIFFICULTY_FUZZ = 4;

    // Randomization timeout in nanoseconds (200 ms)
    private static final long RANDOMIZE_TIMEOUT = 200 * 1000000;

    private Random mRand;

    // The base game setup; when we randomize, we fill in any cards set to RANDOM
    private GameSetup mBaseGameSetup;

    // All cards that can be picked by the randomizer for a given type
    private Map<Type, Set<Card>> mValidCards;

    // These are the cards, minus alts, to give each card equal weight
    private Map<Type, List<Card>> mCardBanks;

    public Randomizer(GameSetup baseGameSetup) {
        mBaseGameSetup = baseGameSetup;
        mRand = new Random();
        mValidCards = new HashMap<Type, Set<Card>>();
        mCardBanks = new HashMap<Type, List<Card>>();
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
            usedCards.addAll(Db.getCardAndAlternates(hero));
        }

        for (int a = 0; a < size; a++) {
            Card hero = heroes.get(a);
            if (hero.isRandom()) {
                Card card;
                do {
                    card = getRandomCard(hero);
                }
                while (usedCards.contains(card));

                gameSetup.setHero(a, card);
                usedCards.addAll(Db.getCardAndAlternates(card));
            }
        }

        Card villain = gameSetup.getVillain();
        if (villain.isRandom()) {
            gameSetup.setVillain(getRandomCard(villain));
        }

        Card environment = gameSetup.getEnvironment();
        if (environment.isRandom()) {
            gameSetup.setEnvironment(getRandomCard(environment));
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

    private Card getRandomCard(Card baseCard) {
        Type type = baseCard.getType();

        if (!mValidCards.containsKey(type)) {
            List<Card> validCards = Db.getCards(type);
            mValidCards.put(type, new HashSet<Card>(validCards));

            Set<Card> cardBank = new HashSet<Card>(validCards);

            // Remove alternates so that all cards have equal weight
            Iterator<Card> iterator = cardBank.iterator();
            while (iterator.hasNext()) {
                Card card = iterator.next();

                Set<Card> alts = Db.getCardAndAlternates(card);
                for (Card alt : alts) {
                    if (!card.equals(alt) && cardBank.contains(alt)) {
                        iterator.remove();
                        break;
                    }
                }
            }

            // Add to card banks
            mCardBanks.put(type, new ArrayList<Card>(cardBank));
        }

        List<Card> cards = mCardBanks.get(type);
        int index = mRand.nextInt(cards.size());
        Card card = cards.get(index);

        // Check if there are alternates; if there are, pick one of them
        List<Card> alts = new ArrayList<Card>(Db.getCardAndAlternates(card));
        int size = alts.size();
        if (size > 1) {
            Set<Card> validCards = mValidCards.get(type);
            index = mRand.nextInt(size);
            while (true) {
                card = alts.get(index);
                if (validCards.contains(card)) {
                    break;
                }
                else {
                    index = (index + 1) % size;
                }
            }
        }

        return card;
    }

}
