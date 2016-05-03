package com.example.manuelsanchez.udacitycapstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.manuelsanchez.udacitycapstone.data.EventContract.EventEntry;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;


public class EventDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "event.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_EVENT_TABLE =
                "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                        EventEntry._ID + " INTEGER PRIMARY KEY, " +
                        EventEntry.COLUMN_EVENT_ID + " UNIQUE TEXT NOT NULL, " +
                        EventEntry.COLUMN_VENUE + " TEXT NOT NULL, " +
                        EventEntry.COLUMN_COORD_LATITUDE + " REAL NOT NULL, " +
                        EventEntry.COLUMN_COORD_LONGITUDE + " REAL NOT NULL, " +
                        EventEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                        " );";

        final String SQL_CREATE_PERFORMER_TABLE =
                "CREATE TABLE " + PerformerEntry.TABLE_NAME + " (" +
                        PerformerEntry._ID + " INTEGER PRIMARY KEY, " +
                        PerformerEntry.COLUMN_PERFORMER_ID + " UNIQUE TEXT NOT NULL, " +
                        PerformerEntry.COLUMN_PERFORMER_NAME + " UNIQUE TEXT NOT NULL, " +
                        PerformerEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                        " );";

        final String SQL_CREATE_PERFORMER_EVENT_MAP_TABLE =
                "CREATE TABLE " + PerformerEventMapEntry.TABLE_NAME + " (" +
                        PerformerEventMapEntry._ID + " INTEGER PRIMARY KEY, " +
                        PerformerEventMapEntry.COLUMN_PERFORMER_ID + " UNIQUE TEXT NOT NULL, " +
                        PerformerEventMapEntry.COLUMN_EVENT_ID + " UNIQUE TEXT NOT NULL, " +
                        " );";

        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_PERFORMER_TABLE);
        db.execSQL(SQL_CREATE_PERFORMER_EVENT_MAP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PerformerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PerformerEventMapEntry.TABLE_NAME);
        onCreate(db);
    }
}
