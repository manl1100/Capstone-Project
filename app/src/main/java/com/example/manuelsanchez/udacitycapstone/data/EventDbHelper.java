package com.example.manuelsanchez.udacitycapstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.manuelsanchez.udacitycapstone.data.EventContract.EventEntry;


public class EventDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "event.db";

    public EventDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_EVENT_TABLE =
                "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                        EventEntry._ID + " INTEGER PRIMARY KEY, " +
                        EventEntry.COLUMN_PERFORMER + " TEXT UNIQUE NOT NULL, " +
                        EventEntry.COLUMN_VENUE + " TEXT UNIQUE NOT NULL, " +
                        EventEntry.COLUMN_COORD_LATITUDE + " REAL NOT NULL, " +
                        EventEntry.COLUMN_COORD_LONGITUDE + " REAL NOT NULL, " +
                        EventEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                        " );";

        db.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        onCreate(db);
    }
}
