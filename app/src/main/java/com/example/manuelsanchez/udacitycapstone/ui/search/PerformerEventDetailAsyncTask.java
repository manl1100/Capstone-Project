package com.example.manuelsanchez.udacitycapstone.ui.search;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.data.EventContract;
import com.example.manuelsanchez.udacitycapstone.ui.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;


public class PerformerEventDetailAsyncTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = PerformerEventDetailAsyncTask.class.getSimpleName();

    private Context mContext;
    private String eventId;

    public PerformerEventDetailAsyncTask(Context mContext, String eventId) {
        this.mContext = mContext;
        this.eventId = eventId;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventId = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        try {
            String urlBase = "http://api.eventful.com/json/events/get?";
            String apiKeyParam = "app_key";
            String apiKey = mContext.getString(R.string.eventful_api_key);
            String performerIdParam = "id";

            Uri builtUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .appendQueryParameter(performerIdParam, eventId)
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

        parseTourString(concertString);
        return null;
    }

    private List<Event> parseTourString(String response) {
        Vector<ContentValues> eventsVector = new Vector<>();

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
        final String JSON_STRING_VENUE_ADDRESS = "address";
        final String JSON_STRING_VENUE_CITY = "city";
        final String JSON_STRING_COUNTRY = "country";
        final String JSON_STRING_ID = "id";

        try {
            JSONObject responseObject = new JSONObject(response);
            ContentValues contentValues = new ContentValues();
            contentValues.put(EventEntry.COLUMN_EVENT_ID, eventId);
            contentValues.put(EventEntry.COLUMN_VENUE, responseObject.getString(JSON_STRING_VENUE_NAME));
            contentValues.put(EventEntry.COLUMN_COORD_LATITUDE, responseObject.getString(JSON_DOUBLE_LATITUDE));
            contentValues.put(EventEntry.COLUMN_COORD_LONGITUDE, responseObject.getString(JSON_DOUBLE_LONGITUDE));
            contentValues.put(EventEntry.COLUMN_DATE, responseObject.getString(JSON_STRING_DATE));
            contentValues.put(EventEntry.COLUMN_COUNTRY, responseObject.getString(JSON_STRING_COUNTRY));
            contentValues.put(EventEntry.COLUMN_REGION, responseObject.getString(JSON_STRING_REGION_ABBR));
            contentValues.put(EventEntry.COLUMN_REGION_ABBR, responseObject.getString(JSON_STRING_REGION_ABBR));
            contentValues.put(EventEntry.COLUMN_VENUE_CITY, responseObject.getString(JSON_STRING_VENUE_CITY));
            contentValues.put(EventEntry.COLUMN_VENUE_ADDRESS, responseObject.getString(JSON_STRING_VENUE_ADDRESS));
            contentValues.put(EventEntry.COLUMN_VENUE_POSTAL_CODE, responseObject.getString(JSON_STRING_POSTAL_CODE));
            eventsVector.add(contentValues);

            ContentValues[] values = new ContentValues[eventsVector.size()];
            eventsVector.toArray(values);
            mContext.getContentResolver().bulkInsert(EventContract.EventEntry.CONTENT_URI, values);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
