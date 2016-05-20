package com.example.manuelsanchez.udacitycapstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class EventAuthenticatorService extends Service {

    private EventAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new EventAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
