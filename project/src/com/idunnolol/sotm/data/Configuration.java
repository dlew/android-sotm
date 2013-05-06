package com.idunnolol.sotm.data;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

	private static final Configuration sInstance = new Configuration();

	private Configuration() {
		// Singleton
	}

	public static Configuration getInstance() {
		return sInstance;
	}

	public static void init() {
		sInstance.mHeroes.add(Card.RANDOM);
		sInstance.mHeroes.add(Card.RANDOM);
		sInstance.mVillain = Card.RANDOM;
		sInstance.mEnvironment = Card.RANDOM;
	}

	private List<Card> mHeroes = new ArrayList<Card>(5);

	private Card mVillain;

	private Card mEnvironment;

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

}