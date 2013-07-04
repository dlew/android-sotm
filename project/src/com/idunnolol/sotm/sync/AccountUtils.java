package com.idunnolol.sotm.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.idunnolol.utils.Log;

public class AccountUtils {

	public static final String ACCOUNT_TYPE = "com.idunnolol.sotm";
	public static final String ACCOUNT_NAME = "Data Updater";

	private static final long SYNC_INTERVAL = 24 * 60 * 60;

	public static Account getSyncAccount() {
		return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
	}

	public static void addSyncAccount(Context context) {
		AccountManager manager = AccountManager.get(context);

		Account[] accounts = manager.getAccountsByType(ACCOUNT_TYPE);
		if (accounts.length != 0) {
			Log.d("Tried to add sync account, but one already existed!");
		}
		else {
			Log.i("Adding sync account...");

			Account account = getSyncAccount();
			manager.addAccountExplicitly(account, null, null);
			ContentResolver.setIsSyncable(account, PointContentProvider.CONTENT_AUTHORITY, 1);
			ContentResolver.setSyncAutomatically(account, PointContentProvider.CONTENT_AUTHORITY, true);
		}
	}

	public static void startPeriodicSync() {
		Log.i("Adding periodic syncs every " + SYNC_INTERVAL + " seconds...");
		ContentResolver.addPeriodicSync(getSyncAccount(), PointContentProvider.CONTENT_AUTHORITY, new Bundle(),
				SYNC_INTERVAL);
	}
}
