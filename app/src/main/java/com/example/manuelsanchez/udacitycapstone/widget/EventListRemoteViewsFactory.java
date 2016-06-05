package com.example.manuelsanchez.udacitycapstone.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.data.EventContract;
import com.example.manuelsanchez.udacitycapstone.ui.EventLoader;
import com.example.manuelsanchez.udacitycapstone.util.Utility;

import static com.example.manuelsanchez.udacitycapstone.ui.EventLoader.COL_EVENT_ID;
import static com.example.manuelsanchez.udacitycapstone.ui.EventLoader.COL_PERFORMER;
import static com.example.manuelsanchez.udacitycapstone.ui.EventLoader.COL_VENUE;


public class EventListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor cursor;
    private Context mContext;

    public EventListRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri eventUri = EventContract.EventEntry.buildEventUriWithDateAndLocation(Utility.getPreferredLocation(mContext), System.currentTimeMillis());
        cursor = mContext.getContentResolver().query(eventUri,
                EventLoader.EVENT_COLUMNS,
                null,
                null,
                null
        );

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_event);

        String performer = cursor.getString(COL_PERFORMER).split(",")[0];
        String venue = cursor.getString(COL_VENUE);


        views.setTextViewText(R.id.widget_event_performer, performer);
        views.setTextViewText(R.id.widget_event_venue, venue);


        Bundle bundle = new Bundle();
        bundle.putString(EventWidgetProvider.EXTRA_ITEM, cursor.getString(COL_EVENT_ID));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(bundle);
        views.setOnClickFillInIntent(R.id.widget, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_event);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position)) {
            return cursor.getLong(EventLoader.COL_EVENT_ITEM_ID);
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
