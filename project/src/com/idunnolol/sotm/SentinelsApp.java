package com.idunnolol.sotm;

import android.app.Application;

import com.idunnolol.sotm.data.Db;
import com.idunnolol.utils.Log;

public class SentinelsApp extends Application {

	public static final String TAG = "SotM";

	@Override
	public void onCreate() {
		super.onCreate();

		Log.configure(TAG, true);

		Db.init(this);

		BitmapCache.init(this);
	}
}
