package com.idunnolol.sotm.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import com.danlew.utils.Log;
import com.idunnolol.sotm.data.Db;

public class PointSyncAdapter extends AbstractThreadedSyncAdapter {

    public PointSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public PointSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(
        Account account, Bundle extras, String authority, ContentProviderClient provider,
        SyncResult syncResult) {
        Log.i("Syncing SotM data from server...");

        boolean success = Db.updatePoints(getContext());
        if (success) {
            getContext().getContentResolver().notifyChange(AccountUtils.SYNC_URI, null);
        }
        else {
            syncResult.hasError();
        }
    }

}
