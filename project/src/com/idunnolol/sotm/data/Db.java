package com.idunnolol.sotm.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.SparseIntArray;

import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card.Type;
import com.idunnolol.utils.Log;
import com.idunnolol.utils.ResourceUtils;

public class Db {

	private static final Db sInstance = new Db();

	private Db() {
		// Singleton
	}

	public static void init(Context context) {
		try {
			long start = System.nanoTime();

			// Read in data from assets
			sInstance.initCards(context);
			sInstance.initNameConversions(context);
			sInstance.initPoints(context);

			// TODO: Restore disabled state
			//
			// Right now we just set default state each run
			for (CardSet cardSet : sInstance.mCardSets) {
				cardSet.setAllCardsEnabled(cardSet.isEnabledByDefault());
			}

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

	private List<CardSet> mCardSets = new ArrayList<CardSet>();

	private SparseIntArray mNumPlayerPoints = new SparseIntArray();

	private SparseIntArray mDifficultyScale = new SparseIntArray();

	private Map<String, String> mNameConversions = new HashMap<String, String>();

	public static List<CardSet> getCardSets() {
		return sInstance.mCardSets;
	}

	public static Card getCard(String id) {
		if (id.equals(Card.RANDOM.getId())) {
			return Card.RANDOM;
		}

		return sInstance.mCards.get(id);
	}

	/**
	 * Returns all cards of a particular type.  Only returns enabled cards.
	 */
	public static List<Card> getCards(Type type) {
		List<Card> cards = new ArrayList<Card>();

		for (Card card : sInstance.mCards.values()) {
			if (card.getType() == type && card.isEnabled()) {
				cards.add(card);
			}
		}

		return cards;
	}

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
							mCards.put(card.getId(), card);
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

			if (name.equals("id")) {
				set.setId(reader.nextString());
			}
			else if (name.equals("name")) {
				set.setNameResId(ResourceUtils.getIdentifier(R.string.class, reader.nextString()));
			}
			else if (name.equals("enabledByDefault")) {
				set.setEnabledByDefault(reader.nextBoolean());
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
		Card card = new Card();

		reader.beginObject();
		while (reader.hasNext()) {
			String jsonName = reader.nextName();

			if (jsonName.equals("id")) {
				card.setId(reader.nextString());
			}
			else if (jsonName.equals("name")) {
				String name = reader.nextString();
				card.setNameResId(ResourceUtils.getIdentifier(R.string.class, name));
			}
			else if (jsonName.equals("type")) {
				String type = reader.nextString();
				if (type.equals("hero")) {
					card.setType(Type.HERO);
				}
				else if (type.equals("villain")) {
					card.setType(Type.VILLAIN);
				}
				else if (type.equals("environment")) {
					card.setType(Type.ENVIRONMENT);
				}
			}
			else if (jsonName.equals("alternate")) {
				card.setIsAlternate(reader.nextBoolean());
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();

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
