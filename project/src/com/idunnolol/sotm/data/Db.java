package com.idunnolol.sotm.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.SparseIntArray;

import com.idunnolol.utils.Log;

public class Db {

	private static final Db sInstance = new Db();

	private Db() {
		// Singleton
	}

	public static void init(Context context) {
		try {
			long start = System.nanoTime();
			sInstance.initCards(context);
			sInstance.initNameConversions(context);
			sInstance.initPoints(context);
			long end = System.nanoTime();
			Log.i("Initialized db in " + ((end - start) / 100000) + " ms");
		}
		catch (IOException e) {
			Log.e("Error while reading data", e);
			throw new RuntimeException(e);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Data

	private Map<String, Card> mCards = new HashMap<String, Card>();

	private Collection<CardSet> mCardSets = new HashSet<CardSet>();

	private SparseIntArray mNumPlayerPoints = new SparseIntArray();

	private SparseIntArray mDifficultyScale = new SparseIntArray();

	private Map<String, String> mNameConversions = new HashMap<String, String>();

	//////////////////////////////////////////////////////////////////////////
	// Read data

	private static final String CARD_FILE = "cards.json";
	private static final String NAME_FILE = "names.json";
	private static final String POINT_FILE = "points.json";

	private void initCards(Context context) throws IOException {
		InputStream in = null;
		try {
			in = context.getAssets().open(CARD_FILE);
			JsonReader reader = new JsonReader(new InputStreamReader(in));

			// Read through top level objects
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("sets")) {
					reader.beginArray();
					while (reader.hasNext()) {
						CardSet set = readSet(reader);
						mCardSets.add(set);
						for (Card card : set.getCards()) {
							mCards.put(card.getName(), card);
						}
					}
					reader.endArray();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		finally {
			in.close();
		}
	}

	private CardSet readSet(JsonReader reader) throws IOException {
		CardSet set = new CardSet();

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("name")) {
				set.setName(reader.nextString());
			}
			else if (name.equals("cards")) {
				reader.beginArray();
				while (reader.hasNext()) {
					set.addCard(readCard(reader));
				}
				reader.endArray();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return set;
	}

	private Card readCard(JsonReader reader) throws IOException {
		// Parse all data ahead of time
		String name = null;
		String type = null;
		boolean isAlternate = false;

		reader.beginObject();
		while (reader.hasNext()) {
			String jsonName = reader.nextName();

			if (jsonName.equals("name")) {
				name = reader.nextString();
			}
			else if (jsonName.equals("type")) {
				type = reader.nextString();
			}
			else if (jsonName.equals("alternate")) {
				isAlternate = reader.nextBoolean();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();

		// Return as card 
		Card card;
		if (type.equals("hero")) {
			Hero hero = new Hero();
			hero.setIsAlternate(isAlternate);
			card = hero;
		}
		else if (type.equals("villain")) {
			card = new Villain();
		}
		else if (type.equals("environment")) {
			card = new Environment();
		}
		else {
			throw new RuntimeException("Found card with no known type: " + type);
		}

		// Common code
		card.setName(name);

		return card;
	}

	private void initNameConversions(Context context) throws IOException {
		InputStream in = null;
		JsonReader reader = null;
		try {
			in = context.getAssets().open(NAME_FILE);
			reader = new JsonReader(new InputStreamReader(in));

			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				String value = reader.nextString();
				mNameConversions.put(name, value);
			}
			reader.endObject();
		}
		finally {
			if (in != null) {
				in.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void initPoints(Context context) throws IOException {
		InputStream in = null;
		JsonReader reader = null;
		try {
			in = context.getAssets().open(POINT_FILE);
			reader = new JsonReader(new InputStreamReader(in));
			reader.setLenient(true);

			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("difficulty")) {
					readPoints(reader);
				}
				else if (name.equals("scale")) {
					readDifficultyScale(reader);
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		finally {
			if (in != null) {
				in.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void readPoints(JsonReader reader) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();

			if (name.equals("hero") || name.equals("villain") || name.equals("env")) {
				reader.beginArray();
				while (reader.hasNext()) {
					// NOTE: This null-check can be removed if points JSON becomes compliant
					if (reader.peek() != JsonToken.NULL) {
						readCardPoints(reader);
					}
					else {
						reader.skipValue();
					}
				}
				reader.endArray();
			}
			else if (name.equals("nump")) {
				reader.beginArray();
				while (reader.hasNext()) {
					// NOTE: This null-check can be removed if points JSON becomes compliant
					if (reader.peek() != JsonToken.NULL) {
						readNumPlayers(reader);
					}
					else {
						reader.skipValue();
					}
				}
				reader.endArray();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}

	private void readCardPoints(JsonReader reader) throws IOException {
		String cardName = null;
		int points = 0;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();

			if (name.equals("name")) {
				cardName = reader.nextString();
			}
			else if (name.equals("points")) {
				points = reader.nextInt();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();

		if (mNameConversions.containsKey(cardName)) {
			String newName = mNameConversions.get(cardName);
			Log.v("Converting points card \"" + cardName + "\" to \"" + newName + "\"");
			cardName = newName;
		}

		Card card = mCards.get(cardName);
		if (card == null) {
			// Sanity check
			throw new RuntimeException("Could not find card \"" + cardName + "\" for points");
		}

		card.setPoints(points);
	}

	private void readNumPlayers(JsonReader reader) throws IOException {
		String numPlayers = null;
		int points = 0;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();

			if (name.equals("name")) {
				numPlayers = reader.nextString();
			}
			else if (name.equals("points")) {
				points = reader.nextInt();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();

		if (numPlayers.equals("Three")) {
			mNumPlayerPoints.put(3, points);
		}
		else if (numPlayers.equals("Four")) {
			mNumPlayerPoints.put(4, points);
		}
		else if (numPlayers.equals("Five")) {
			mNumPlayerPoints.put(5, points);
		}
		else {
			throw new RuntimeException("Unknown # of players: " + numPlayers);
		}
	}

	private void readDifficultyScale(JsonReader reader) throws IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			int total = 0;
			int lossPercent = 0;

			// NOTE: This null-check can be removed if points JSON becomes compliant
			if (reader.peek() != JsonToken.NULL) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (name.equals("total")) {
						total = reader.nextInt();
					}
					else if (name.equals("losspct")) {
						total = reader.nextInt();
					}
					else {
						reader.skipValue();
					}
				}
				reader.endObject();
			}
			else {
				reader.skipValue();
			}

			mDifficultyScale.put(total, lossPercent);
		}
		reader.endArray();
	}
}
