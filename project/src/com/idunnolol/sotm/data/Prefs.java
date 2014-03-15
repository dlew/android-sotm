package com.idunnolol.sotm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    private static final String PREF_ADVANCED_ALLOWED = "PREF_ADVANCED_ALLOWED";

    private static Context sAppContext;

    public static void init(Context context) {
        sAppContext = context.getApplicationContext();
    }

    public static boolean isAdvancedAllowed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sAppContext);
        return prefs.getBoolean(PREF_ADVANCED_ALLOWED, false);
    }

    public static void setAdvancedAllowed(boolean allowed) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sAppContext);
        prefs.edit().putBoolean(PREF_ADVANCED_ALLOWED, allowed).apply();
    }

}
