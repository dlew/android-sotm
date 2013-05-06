package com.idunnolol.sotm;

import com.idunnolol.sotm.data.Db;
import com.idunnolol.utils.Log;

import android.app.Application;

public class SentinelsApp extends Application {

	public static final String TAG = "SotM";

	@Override
	public void onCreate() {
		super.onCreate();

		Log.configure(TAG, true);

		Db.init(this);
	}
}
