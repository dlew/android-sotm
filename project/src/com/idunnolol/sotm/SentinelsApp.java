package com.idunnolol.sotm;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.danlew.utils.Log;
import com.idunnolol.sotm.data.Db;
import com.idunnolol.sotm.data.Prefs;

public class SentinelsApp extends Application {

    public static final String TAG = "SotM";

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics.start(this);

        Log.configure(TAG, true);

        Prefs.init(this);

        Db.init(this);

        BitmapCache.init(this);
    }
}
