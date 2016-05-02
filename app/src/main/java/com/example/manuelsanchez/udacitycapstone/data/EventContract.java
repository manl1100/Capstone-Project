package com.example.manuelsanchez.udacitycapstone.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class EventContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.udacitycapstone.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EVENT = "event";
    public static final String PATH_PERFORMER = "performer";
    public static final String PATH_PERFORMER_EVENT = "performer_event";
    public static final String PATH_LOCATION = "location";


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
        public static final String COLUMN_EVENT_ID = "event_id";

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventUriWithDateAndLocation(String location, long startDate) {
            return CONTENT_URI;
        }
    }

    public static final class PerformerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERFORMER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;

        public static final String TABLE_NAME =  "performer";
        public static final String COLUMN_PERFORMER_NAME = "name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PERFORMER_ID = "performer_id";

    }

    public static final class PerformerEventMapEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERFORMER_EVENT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;

        public static final String TABLE_NAME =  "performer_event_map";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_PERFORMER_ID = "performer_id";

    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +"/" + PATH_EVENT;

        public static final String TABLE_NAME =  "location";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COUNTRY = "country";

    }
}
