package com.example.manuelsanchez.udacitycapstone.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.ui.Artist;
import com.example.manuelsanchez.udacitycapstone.ui.Event;
import com.example.manuelsanchez.udacitycapstone.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.EventEntry;
import static com.example.manuelsanchez.udacitycapstone.data.EventContract.LocationEntry;
import static com.example.manuelsanchez.udacitycapstone.data.EventContract.PerformerEntry;
import static com.example.manuelsanchez.udacitycapstone.data.EventContract.PerformerEventMapEntry;

public class EventSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = EventSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    ContentResolver contentResolver;

    public EventSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    public EventSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        String location = Utility.getPreferredLocation(getContext());


        try {
            String urlBase = "http://api.eventful.com/json/events/search?";
            String apiKeyParam = "app_key";
            String apiKey = getContext().getString(R.string.eventful_api_key);
            String categoryParam = "category";
            String categoryParamValue = "music";
            String locationParam = "location";
            String withinParam = "within";
            String withinParamValue = "50";
            String unitParam = "units";
            String unitParamValue = "mi";
            String sortOrderParam = "sort_order";
            String sortOrderValue = "popularity";
            String dateParam = "date";
            String dateParamValue = "future";
            String pageSizeParam = "page_size";
            String pageSizeParamValue = "50";


            Uri builtUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .appendQueryParameter(categoryParam, categoryParamValue)
                    .appendQueryParameter(locationParam, location)
                    .appendQueryParameter(withinParam, withinParamValue)
                    .appendQueryParameter(unitParam, unitParamValue)
                    .appendQueryParameter(sortOrderParam, sortOrderValue)
                    .appendQueryParameter(dateParam, dateParamValue)
                    .appendQueryParameter(pageSizeParam, pageSizeParamValue)
                    .build();


            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            concertString = buffer.toString();
            Log.i(LOG_TAG, "JsonString: " + concertString);


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        parseConcertString(concertString, location);

    }

    private void parseConcertString(String response, String location) {

        // TODO: Move to constants file
        final String JSON_OBJECT_EVENT = "events";
        final String JSON_ARRAY_EVENT = "event";
        final String JSON_STRING_DATE = "start_time";
        final String JSON_STRING_VENUE_NAME = "venue_name";
        final String JSON_DOUBLE_LATITUDE = "latitude";
        final String JSON_DOUBLE_LONGITUDE = "longitude";
        final String JSON_OBJECT_ARTIST = "performers";
        final String JSON_ARTIST = "performer";
        final String JSON_STRING_ARTIST = "name";
        final String JSON_STRING_REGION_ABBR = "region_abbr";
        final String JSON_STRING_POSTAL_CODE = "postal_code";
        final String JSON_STRING_VENUE_ADDRESS = "venue_address";
        final String JSON_STRING_VENUE_CITY = "city_name";
        final String JSON_STRING_ID = "id";
        final String JSON_OBJECT_IMAGE = "image";
        final String JSON_OBJECT_MEDIUM = "medium";
        final String JSON_STRING_IMAGE = "url";


        List<Event> events = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();

        Vector<ContentValues> eventsVector = new Vector<>();
        Vector<ContentValues> performerVector = new Vector<>();
        Vector<ContentValues> performerEventVector = new Vector<>();

        long locationId = addLocation(location);

        try {
            JSONObject responseObject = new JSONObject(response);
            JSONObject eventObject = responseObject.getJSONObject(JSON_OBJECT_EVENT);
            JSONArray eventArray = eventObject.getJSONArray(JSON_ARRAY_EVENT);

            for (int i = 0; i < eventArray.length(); i++) {
                JSONObject eventItem = eventArray.getJSONObject(i);

                List<Artist> artistList = new ArrayList<>();

                if (eventItem.isNull(JSON_OBJECT_ARTIST)) {
                    continue;
                }
                JSONObject artistObject = eventItem.getJSONObject(JSON_OBJECT_ARTIST);
                if (artistObject.get(JSON_ARTIST) instanceof JSONArray) {
                    JSONArray artistArray = artistObject.getJSONArray(JSON_ARTIST);
                    for (int j = 0; j < artistArray.length(); j++) {
                        String artistName = artistArray.getJSONObject(j).getString(JSON_STRING_ARTIST);
                        String artistId = artistArray.getJSONObject(j).getString(JSON_STRING_ID);
                        JSONObject image = eventItem.getJSONObject(JSON_OBJECT_IMAGE);
                        JSONObject medium = image.getJSONObject(JSON_OBJECT_MEDIUM);
                        String imageUrl = medium.getString(JSON_STRING_IMAGE);
                        artistList.add(new Artist(artistName, artistId, imageUrl));
                    }
                } else {
                    String artist = artistObject.getJSONObject(JSON_ARTIST).getString(JSON_STRING_ARTIST);
                    String artistId = artistObject.getJSONObject(JSON_ARTIST).getString(JSON_STRING_ID);
                    JSONObject image = eventItem.getJSONObject(JSON_OBJECT_IMAGE);
                    JSONObject medium = image.getJSONObject(JSON_OBJECT_MEDIUM);
                    String imageUrl = medium.getString(JSON_STRING_IMAGE);
                    artistList.add(new Artist(artist, artistId, imageUrl));
                }

                Event event = new Event.Builder()
                        .eventId(eventItem.getString(JSON_STRING_ID))
                        .venueName(eventItem.getString(JSON_STRING_VENUE_NAME))
                        .latitude(eventItem.getDouble(JSON_DOUBLE_LATITUDE))
                        .longitude(eventItem.getDouble(JSON_DOUBLE_LONGITUDE))
                        .eventDate(eventItem.getString(JSON_STRING_DATE))
                        .postalCode(eventItem.getString(JSON_STRING_POSTAL_CODE))
                        .address(eventItem.getString(JSON_STRING_VENUE_ADDRESS))
                        .regionAbbr(eventItem.getString(JSON_STRING_REGION_ABBR))
                        .city(eventItem.getString(JSON_STRING_VENUE_CITY))
                        .country(eventItem.getString(JSON_STRING_VENUE_CITY))
                        .artists(artistList)
                        .build();
                events.add(event);

                ContentValues eventValues = new ContentValues();
                eventValues.put(EventEntry.COLUMN_EVENT_ID, event.getEventId());
                eventValues.put(EventEntry.COLUMN_VENUE, event.getVenueName());
                eventValues.put(EventEntry.COLUMN_COORD_LATITUDE, event.getLatitude());
                eventValues.put(EventEntry.COLUMN_COORD_LONGITUDE, event.getLongitute());
                eventValues.put(EventEntry.COLUMN_DATE, event.getEventDate());
                eventValues.put(EventEntry.COLUMN_COUNTRY, event.getCountry());
                eventValues.put(EventEntry.COLUMN_REGION, event.getRegion());
                eventValues.put(EventEntry.COLUMN_REGION_ABBR, event.getRegionAbbr());
                eventValues.put(EventEntry.COLUMN_VENUE_CITY, event.getVenueCity());
                eventValues.put(EventEntry.COLUMN_VENUE_ADDRESS, event.getVenueAddress());
                eventValues.put(EventEntry.COLUMN_VENUE_POSTAL_CODE, event.getPostalCode());
                eventValues.put(EventEntry.COLUMN_LOCATION_SETTING_ID, locationId);
                eventsVector.add(eventValues);

                for (Artist artist : artistList) {
                    ContentValues performerValues = new ContentValues();
                    performerValues.put(PerformerEntry.COLUMN_PERFORMER_ID, artist.getEventfulArtistId());
                    performerValues.put(PerformerEntry.COLUMN_PERFORMER_NAME, artist.getArtistName());
                    performerValues.put(PerformerEntry.COLUMN_IMAGE_URL, artist.getImageUrl());
                    performerVector.add(performerValues);

                    ContentValues performerEvent = new ContentValues();
                    performerEvent.put(PerformerEventMapEntry.COLUMN_EVENT_ID, event.getEventId());
                    performerEvent.put(PerformerEventMapEntry.COLUMN_PERFORMER_ID, artist.getEventfulArtistId());
                    performerEventVector.add(performerEvent);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bulkInsert(performerEventVector, PerformerEventMapEntry.CONTENT_URI);
        bulkInsert(performerVector, PerformerEntry.CONTENT_URI);
        bulkInsert(eventsVector, EventEntry.CONTENT_URI);
    }

    private void bulkInsert(Vector<ContentValues> contentValues, Uri contentUri) {
        ContentValues[] values = new ContentValues[contentValues.size()];
        contentValues.toArray(values);
        getContext().getContentResolver().bulkInsert(contentUri, values);
    }

    private long addLocation(String location) {
        long id;

        Cursor locationCursor = getContext().getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID},
                LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{location},
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(LocationEntry._ID);
            id = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();
            locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, location);
            Uri uri = getContext().getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
            id = LocationEntry.getLocationFromUri(uri);
        }
        return id;
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // if password doesnt exist neither does the account
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.e(LOG_TAG, "EventSyncAdapter.getSyncAccount: problem creating account");
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account account, Context context) {
        EventSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
