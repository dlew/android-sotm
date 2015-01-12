package com.idunnolol.sotm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Pair;
import android.util.SparseIntArray;
import com.danlew.utils.Log;
import com.danlew.utils.ResourceUtils;
import com.idunnolol.sotm.R;
import com.idunnolol.sotm.data.Card.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Db {

    private static final Db sInstance = new Db();

    // Only show advanced villains who have more than a certain # of games logged
    private static final int ADVANCED_COUNT_CUTOFF = 35;

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

            // If we have saved card state settings, use them; otherwise use
            // the defaults
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.contains(PREFERENCE_CARD_STATE)) {
                Set<String> enabledIds = prefs.getStringSet(PREFERENCE_CARD_STATE, null);
                for (String id : enabledIds) {
                    Card card = sInstance.mCards.get(id);

                    // Card can be null if an advanced card ends up being cut off later
                    if (card != null) {
                        sInstance.mCards.get(id).setEnabled(true);
                    }
                }
            }
            else {
                for (CardSet cardSet : sInstance.mCardSets) {
                    cardSet.setAllCardsEnabled(cardSet.isEnabledByDefault());
                }
                saveCardStates(context);
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

    private static final String PREFERENCE_CARD_STATE = "com.idunnolol.sotm.cards.state";

    private Map<String, Card> mCards = new HashMap<String, Card>();

    private List<CardSet> mCardSets = new ArrayList<CardSet>();

    private SparseIntArray mNumPlayerPoints = new SparseIntArray();

    private SparseIntArray mDifficultyScale = new SparseIntArray();

    private Map<String, String> mNameConversions = new HashMap<String, String>();

    private Map<Card, Set<Card>> mAlternates = new HashMap<Card, Set<Card>>();

    // Just used during parsing; if needed this can be more robust
    private Map<Card, CardSet> mReverseCardSetCache = new HashMap<Card, CardSet>();

    private Map<Card, String> mTeams = new HashMap<Card, String>();

    private int mMinDifficultyPoints;
    private int mMaxDifficultyPoints;

    public static List<CardSet> getCardSets() {
        return sInstance.mCardSets;
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

    public static Set<Card> getCardAndAlternates(Card card) {
        if (sInstance.mAlternates.containsKey(card)) {
            return sInstance.mAlternates.get(card);
        }
        else {
            // Return a set consisting of just the card itself
            Set<Card> cards = new HashSet<Card>();
            cards.add(card);
            return cards;
        }
    }

    public static void saveCardStates(Context context) {
        long start = System.nanoTime();

        Set<String> enabledIds = new HashSet<String>();
        for (Card card : sInstance.mCards.values()) {
            if (card.isEnabled()) {
                enabledIds.add(card.getId());
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putStringSet(PREFERENCE_CARD_STATE, enabledIds);
        editor.apply();

        long end = System.nanoTime();
        Log.d("Saved card states in " + ((end - start) / 100000) + " ms");
    }

    public static int getPointsForNumPlayers(int numPlayers) {
        return sInstance.mNumPlayerPoints.get(numPlayers, 0);
    }

    /**
     * Returns the average points v
     *
     * @param type
     * @return
     */
    public static int getAvgPoints(Type type) {
        int total = 0;
        List<Card> cards = getCards(type);
        for (Card card : cards) {
            total += card.getPoints();
        }
        return total / cards.size();
    }

    public static int getWinPercent(int points) {
        // Round points to the nearest 5, as the scale is in 5s.  Round up.
        int mod = points % 5;
        if (mod >= 3) {
            points += 5 - mod;
        }
        else {
            points -= mod;
        }

        // Search, heading towards 0, until you find something on the difficulty scale
        int lossPct = -1;
        do {
            lossPct = sInstance.mDifficultyScale.get(points, -1);
            points += points > 0 ? -5 : 5;
        }
        while (lossPct == -1);

        return 100 - lossPct;
    }

    /**
     * Calculates the range of point values that will satisfy the min/max win percents.
     */
    public static Pair<Integer, Integer> getPointRange(int minWinPercent, int maxWinPercent) {
        if (minWinPercent < 0) {
            minWinPercent = 0;
        }
        if (maxWinPercent > 100) {
            maxWinPercent = 100;
        }

        int start = sInstance.mMaxDifficultyPoints;
        int end = sInstance.mMinDifficultyPoints;

        // Search through the entire difficulty scale looking for
        // the closest value to the min/max win percent.
        int size = sInstance.mDifficultyScale.size();
        int closestMin = 200;
        int closestMax = 200;
        for (int index = 0; index < size; index++) {
            int points = sInstance.mDifficultyScale.keyAt(index);
            int winPct = 100 - sInstance.mDifficultyScale.get(points);

            int minDiff = Math.abs(minWinPercent - winPct);
            if (minDiff < closestMin) {
                start = points;
                closestMin = minDiff;
            }
            else if (minDiff == closestMin && points > start) {
                start = points;
            }

            int maxDiff = Math.abs(maxWinPercent - winPct);
            if (maxDiff < closestMax) {
                end = points;
                closestMax = maxDiff;
            }
            else if (maxDiff == closestMax && points < end) {
                end = points;
            }
        }

        return new Pair<Integer, Integer>(end, start);
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
                        mTeams.clear();

                        CardSet set = readSet(reader);
                        mCardSets.add(set);
                        for (Card card : set.getCards()) {
                            mCards.put(card.getId(), card);
                            mReverseCardSetCache.put(card, set);
                        }

                        for (Card card : mTeams.keySet()) {
                            Card team = mCards.get(mTeams.get(card));
                            team.addTeamMember(card);
                        }
                    }
                    reader.endArray();
                }
                else if (name.equals("alternates")) {
                    readAlternates(reader);
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

    private void readAlternates(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            Set<Card> altSet = new HashSet<Card>();

            reader.beginArray();
            while (reader.hasNext()) {
                altSet.add(mCards.get(reader.nextString()));
            }
            reader.endArray();

            for (Card card : altSet) {
                mAlternates.put(card, altSet);
            }
        }
        reader.endArray();
    }

    private void addAlternate(Card baseCard, Card altCard) {
        Set<Card> altSet = mAlternates.get(baseCard);

        if (altSet == null) {
            altSet = new HashSet<Card>();
            mAlternates.put(baseCard, altSet);
        }

        altSet.add(altCard);
        mAlternates.put(altCard, altSet);
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
                    Card card = readCard(reader);

                    // If this card is part of a team, let that team have it, rather than adding it to the set
                    if (!mTeams.containsKey(card)) {
                        set.addCard(card);
                    }
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
            else if (jsonName.equals("icon")) {
                String icon = reader.nextString();
                card.setIconResId(ResourceUtils.getIdentifier(R.drawable.class, icon));
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
            else if (jsonName.equals("team")) {
                // Put it into a hash for later
                mTeams.put(card, reader.nextString());
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
        boolean success = initPoints(context, false);

        // If we failed to load data from whatever we loaded off the network,
        // fallback to the version we store locally.
        if (!success) {
            initPoints(context, true);
        }
    }

    private boolean initPoints(Context context, boolean useAssetFile) {
        InputStream in = null;
        JsonReader reader = null;
        try {
            // Use the sync file if it exists; otherwise use the built-in points file
            File syncFile = context.getFileStreamPath(SYNCED_POINT_FILE);
            if (!useAssetFile && syncFile.exists()) {
                in = context.openFileInput(SYNCED_POINT_FILE);
                Log.d("Loading point data from synced file...");
            }
            else {
                in = context.getAssets().open(POINT_FILE);
                Log.d("Loading point data from built-in asset file...");
            }

            reader = new JsonReader(new InputStreamReader(in));

            try {
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
        catch (IOException e) {
            Log.w("Could not read point file", e);
            return false;
        }

        return true;
    }

    private void readPoints(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("hero") || name.equals("villain") || name.equals("env")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    readCardPoints(reader);
                }
                reader.endArray();
            }
            else if (name.equals("nump")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    readNumPlayers(reader);
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
        Integer advancedPoints = null;
        int advancedCount = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("name")) {
                cardName = reader.nextString();
            }
            else if (name.equals("points")) {
                points = reader.nextInt();
            }
            else if (name.equals("advanced")) {
                advancedPoints = reader.nextInt();
            }
            else if (name.equals("advcount")) {
                advancedCount = reader.nextInt();
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
            Log.e("Could not find card \"" + cardName + "\" for points");
            return;
        }

        card.setPoints(points);

        if (advancedPoints != null) {
            card.setAdvancedPoints(advancedPoints);
            card.setAdvancedCount(advancedCount);
        }
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
        mMinDifficultyPoints = Integer.MAX_VALUE;
        mMaxDifficultyPoints = Integer.MIN_VALUE;

        reader.beginArray();
        while (reader.hasNext()) {
            int total = 0;
            int lossPercent = 0;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("total")) {
                    total = reader.nextInt();
                }
                else if (name.equals("losspct")) {
                    lossPercent = reader.nextInt();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            mDifficultyScale.put(total, lossPercent);

            if (total < mMinDifficultyPoints) {
                mMinDifficultyPoints = total;
            }
            if (total > mMaxDifficultyPoints) {
                mMaxDifficultyPoints = total;
            }
        }
        reader.endArray();
    }

    //////////////////////////////////////////////////////////////////////////
    // Network updates

    private static final String SYNCED_POINT_FILE = "synced-points.json";

    private static final String TMP_SYNCED_POINT_FILE = "synced-points.json.tmp";

    private static final String SYNC_POINT_URL = "http://x.gray.org/sentinels.json";

    public static boolean updatePoints(Context context) {
        try {
            // Clear the old TMP file
            File tmpFile = context.getFileStreamPath(TMP_SYNCED_POINT_FILE);
            if (tmpFile.exists()) {
                Log.v("Deleted old TMP sync download file");
                tmpFile.delete();
            }

            // Read JSON to TMP file
            Log.v("Loading latest points JSON into TMP file");

            URL url = new URL(SYNC_POINT_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            FileOutputStream out = context.openFileOutput(TMP_SYNCED_POINT_FILE, Context.MODE_PRIVATE);
            InputStream in = urlConnection.getInputStream();

            try {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
            finally {
                in.close();
                out.close();
            }

            // Rename the TMP file to the sync file
            Log.v("Renaming TMP sync file to POINT sync file");
            tmpFile.renameTo(context.getFileStreamPath(SYNCED_POINT_FILE));

            // Reload the new points file
            Log.v("Reloading points data");
            sInstance.initPoints(context);
        }
        catch (MalformedURLException e) {
            // Ignore; this should never happen
            return false;
        }
        catch (IOException e) {
            Log.w("Could not sync Sentinels data", e);
            return false;
        }

        return true;
    }
}
