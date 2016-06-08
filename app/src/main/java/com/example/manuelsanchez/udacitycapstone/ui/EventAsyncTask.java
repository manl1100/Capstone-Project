package com.example.manuelsanchez.udacitycapstone.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.example.manuelsanchez.udacitycapstone.R;
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

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;


public class EventAsyncTask extends AsyncTask<String, Void, List<Event>> {

    private final String LOG_TAG = EventAsyncTask.class.getSimpleName();

    private Context mContext;

    public EventAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected List<Event> doInBackground(String... params) {
        return fetchConcertInArea("75209");
    }

    private List<Event> fetchConcertInArea(String search) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        String location = Utility.getPreferredLocation(mContext);


        try {
            String urlBase = "http://api.eventful.com/json/events/search?";
            String apiKeyParam = "app_key";
            String apiKey = mContext.getString(R.string.eventful_api_key);
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
                return null;
            }
            concertString = buffer.toString();
            Log.i(LOG_TAG, "JsonString: " + concertString);


        } catch (Exception e) {
            Toast.makeText(mContext, mContext.getString(R.string.check_connection_notif), Toast.LENGTH_LONG).show();
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

        return parseConcertString(concertString, location);

    }

    private List<Event> parseConcertString(String response, String location) {

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
                        artistList.add(new Artist(artistName, artistId));
                    }
                } else {
                    String artist = artistObject.getJSONObject(JSON_ARTIST).getString(JSON_STRING_ARTIST);
                    String artistId = artistObject.getJSONObject(JSON_ARTIST).getString(JSON_STRING_ID);
                    artistList.add(new Artist(artist, artistId));
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

        bulkInsert(eventsVector, EventEntry.CONTENT_URI);
        bulkInsert(performerVector, PerformerEntry.CONTENT_URI);
        bulkInsert(performerEventVector, PerformerEventMapEntry.CONTENT_URI);

        return events;
    }

    private void bulkInsert(Vector<ContentValues> contentValues, Uri contentUri) {
        ContentValues[] values = new ContentValues[contentValues.size()];
        contentValues.toArray(values);
        mContext.getContentResolver().bulkInsert(contentUri, values);
    }

    private long addLocation(String location) {
        long id;

        Cursor locationCursor = mContext.getContentResolver().query(
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
            Uri uri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
            id = LocationEntry.getLocationFromUri(uri);
        }
        return id;


    }

}