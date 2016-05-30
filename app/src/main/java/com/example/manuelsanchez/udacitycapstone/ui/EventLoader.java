package com.example.manuelsanchez.udacitycapstone.ui;

import com.example.manuelsanchez.udacitycapstone.data.EventContract;

/**
 * Created by manuelsanchez on 5/15/16.
 */
public class EventLoader {

    public static final int EVENT_LOADER = 0;

    public static final int COL_EVENT_ID = 0;
    public static final int COL_PERFORMER = 1;
    public static final int COL_VENUE = 2;
    public static final int COL_LATITUDE = 3;
    public static final int COL_LONGITUDE = 4;
    public static final int COL_DATE = 5;
    public static final int COL_COUNTRY = 6;
    public static final int COL_REGION = 7;
    public static final int COL_REGION_ABBR = 8;
    public static final int COL_VENUE_CITY = 9;
    public static final int COL_VENUE_ADDRESS = 10;
    public static final int COL_VENUE_POSTAL_CODE = 11;
    public static final int COL_PERFORMER_URL = 12;

    public static final String[] EVENT_COLUMNS = {
            EventContract.EventEntry.TABLE_NAME + "." + EventContract.EventEntry.COLUMN_EVENT_ID,
            "GROUP_CONCAT(" + EventContract.PerformerEntry.COLUMN_PERFORMER_NAME + ")",
            EventContract.EventEntry.COLUMN_VENUE,
            EventContract.EventEntry.COLUMN_COORD_LATITUDE,
            EventContract.EventEntry.COLUMN_COORD_LONGITUDE,
            EventContract.EventEntry.COLUMN_DATE,
            EventContract.EventEntry.COLUMN_COUNTRY,
            EventContract.EventEntry.COLUMN_REGION,
            EventContract.EventEntry.COLUMN_REGION_ABBR,
            EventContract.EventEntry.COLUMN_VENUE_CITY,
            EventContract.EventEntry.COLUMN_VENUE_ADDRESS,
            EventContract.EventEntry.COLUMN_VENUE_POSTAL_CODE,
            "GROUP_CONCAT(" + EventContract.PerformerEntry.COLUMN_IMAGE_URL + ")",
    };

    public static final String[] PERFORMER_EVENT_COLUMNS = {
            EventContract.EventEntry.TABLE_NAME + "." + EventContract.EventEntry.COLUMN_EVENT_ID,
            EventContract.EventEntry.COLUMN_VENUE,
            EventContract.EventEntry.COLUMN_COORD_LATITUDE,
            EventContract.EventEntry.COLUMN_COORD_LONGITUDE,
            EventContract.EventEntry.COLUMN_DATE,
            EventContract.EventEntry.COLUMN_COUNTRY,
            EventContract.EventEntry.COLUMN_REGION,
            EventContract.EventEntry.COLUMN_REGION_ABBR,
            EventContract.EventEntry.COLUMN_VENUE_CITY,
            EventContract.EventEntry.COLUMN_VENUE_ADDRESS,
            EventContract.EventEntry.COLUMN_VENUE_POSTAL_CODE,
    };
}
