package com.example.manuelsanchez.udacitycapstone.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;

public class EventProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int EVENT = 100;
    private static final int PERFORMER = 200;
    private static final int PERFORMEREVENT = 300;

    private EventDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case EVENT: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EVENT: {
                return EventEntry.CONTENT_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown type: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case EVENT: {
                long id = db.insert(EventEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = EventEntry.buildEventUri(id);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case EVENT: {
                return bulkInsert(values, EventEntry.TABLE_NAME);
            }

            case PERFORMER: {
                return bulkInsert(values, PerformerEntry.TABLE_NAME);
            }

            case PERFORMEREVENT: {
                return bulkInsert(values, PerformerEventMapEntry.TABLE_NAME);
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsert(ContentValues[] contentValues, String tableName) {
        SQLiteDatabase database =  mOpenHelper.getWritableDatabase();
        int count = 0;
        try {
            for (ContentValues val : contentValues) {
                long id = database.insert(tableName, null, val);
                if (id != -1) {
                    count++;
                }
            }
            database.setTransactionSuccessful();
        } finally {
          database.endTransaction();
        }

        return count;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_EVENT, EVENT);
        matcher.addURI(authority, PATH_PERFORMER, PERFORMER);
        matcher.addURI(authority, PATH_PERFORMER_EVENT, PERFORMEREVENT);

        return matcher;
    }
}
