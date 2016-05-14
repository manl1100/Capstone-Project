package com.example.manuelsanchez.udacitycapstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class EventSyncService extends Service {

    private static EventSyncAdapter eventSyncAdapter = null;

    private static final Object syncAdapterLock = new Object();


    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (eventSyncAdapter == null) {
                eventSyncAdapter = new EventSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return eventSyncAdapter.getSyncAdapterBinder();
    }
}
