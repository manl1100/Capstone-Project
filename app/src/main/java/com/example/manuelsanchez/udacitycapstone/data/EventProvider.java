package com.example.manuelsanchez.udacitycapstone.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;

public class EventProvider extends ContentProvider {

    private EventDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int EVENT = 100;
    private static final int EVENT_WITH_ID = 150;
    private static final int PERFORMER = 200;
    private static final int PERFORMER_WITH_ID = 250;
    private static final int PERFORMEREVENT = 300;
    private static final int LOCATION = 400;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_EVENT, EVENT);
        matcher.addURI(authority, PATH_EVENT + "/*", EVENT_WITH_ID);

        matcher.addURI(authority, PATH_PERFORMER, PERFORMER);
        matcher.addURI(authority, PATH_PERFORMER + "/*", PERFORMER_WITH_ID);

        matcher.addURI(authority, PATH_PERFORMER_EVENT, PERFORMEREVENT);

        matcher.addURI(authority, PATH_LOCATION, LOCATION);
        return matcher;
    }

    private static final SQLiteQueryBuilder eventsQueryBuilder = new SQLiteQueryBuilder();
    private static final SQLiteQueryBuilder eventsByPerformerQueryBuilder = new SQLiteQueryBuilder();

    static {
        eventsQueryBuilder.setTables(
                EventEntry.TABLE_NAME +
                        " LEFT OUTER JOIN " + PerformerEventMapEntry.TABLE_NAME +
                        " ON " + EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID +
                        " = " + PerformerEventMapEntry.TABLE_NAME + "." + PerformerEventMapEntry.COLUMN_EVENT_ID +

                        " LEFT OUTER JOIN " + PerformerEntry.TABLE_NAME +
                        " ON " + PerformerEntry.TABLE_NAME + "." + PerformerEntry.COLUMN_PERFORMER_ID +
                        " = " + PerformerEventMapEntry.TABLE_NAME + "." + PerformerEventMapEntry.COLUMN_PERFORMER_ID
        );

        eventsByPerformerQueryBuilder.setTables(
                EventEntry.TABLE_NAME +
                        " INNER JOIN " + PerformerEventMapEntry.TABLE_NAME +
                        " ON " + EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID +
                        " = " + PerformerEventMapEntry.TABLE_NAME + "." + PerformerEventMapEntry.COLUMN_EVENT_ID

        );
    }

    private static final String eventById = EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID + " = ? ";
    private static final String eventByPerformerId = PerformerEventMapEntry.TABLE_NAME + "." + PerformerEventMapEntry.COLUMN_PERFORMER_ID + " = ? ";
    private static final String groupByEventId = EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID;

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
                returnCursor = eventsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        null,
                        null,
                        groupByEventId,
                        null,
                        sortOrder);
                break;
            }

            case EVENT_WITH_ID: {
                String eventId = EventEntry.getEventIdFromUri(uri);
                returnCursor = eventsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        eventById,
                        new String[]{eventId},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case PERFORMER_WITH_ID: {
                String performerId = PerformerEntry.getPerformerIdFromUri(uri);
                returnCursor = eventsByPerformerQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        eventByPerformerId,
                        new String[]{performerId},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case LOCATION: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
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

            case LOCATION: {
                long id = db.insert(LocationEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = LocationEntry.buildLocationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        database.beginTransaction();
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
}
