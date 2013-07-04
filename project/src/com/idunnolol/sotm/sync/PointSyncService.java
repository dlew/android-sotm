package com.idunnolol.sotm.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PointSyncService extends Service {

	private static final Object SYNC_ADAPTER_LOCK = new Object();

	private static PointSyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		super.onCreate();

		synchronized (SYNC_ADAPTER_LOCK) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new PointSyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

}
