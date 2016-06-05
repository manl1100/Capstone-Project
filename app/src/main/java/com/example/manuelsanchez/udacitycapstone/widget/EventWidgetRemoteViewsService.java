package com.example.manuelsanchez.udacitycapstone.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by manuelsanchez on 6/5/16.
 */
public class EventWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventListRemoteViewsFactory(this);
    }
}
