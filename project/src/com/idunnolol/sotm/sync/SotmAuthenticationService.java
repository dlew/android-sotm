package com.idunnolol.sotm.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SotmAuthenticationService extends Service {

	private SotmAccountAuthenticator mAuthenticator;

	@Override
	public void onCreate() {
		super.onCreate();

		mAuthenticator = new SotmAccountAuthenticator(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mAuthenticator.getIBinder();
	}

}
