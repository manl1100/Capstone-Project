package com.example.manuelsanchez.udacitycapstone.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by manuelsanchez on 4/14/16
 */
public class TestDb extends AndroidTestCase {

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(EventContract.EventEntry.TABLE_NAME);

        mContext.deleteDatabase(EventDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new EventDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + EventContract.EventEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(EventContract.EventEntry._ID);
        locationColumnHashSet.add(EventContract.EventEntry.COLUMN_PERFORMER);
        locationColumnHashSet.add(EventContract.EventEntry.COLUMN_COORD_LATITUDE);
        locationColumnHashSet.add(EventContract.EventEntry.COLUMN_COORD_LONGITUDE);
        locationColumnHashSet.add(EventContract.EventEntry.COLUMN_DATE);
        locationColumnHashSet.add(EventContract.EventEntry.COLUMN_VENUE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns", locationColumnHashSet.isEmpty());
        db.close();
    }
}
