package com.example.manuelsanchez.udacitycapstone.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class EventContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.udacitycapstone.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "event";


    public static final class EventEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;

        public static final String TABLE_NAME =  "event";
        public static final String COLUMN_VENUE = "venue";
        public static final String COLUMN_PERFORMER = "performer";
        public static final String COLUMN_COORD_LATITUDE = "coord_lat";
        public static final String COLUMN_COORD_LONGITUDE = "coord_long";
        public static final String COLUMN_DATE = "event_date";

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventUriWithDateAndLocation(String location, long startDate) {
            return CONTENT_URI;
        }
    }
}
