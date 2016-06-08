package com.example.manuelsanchez.udacitycapstone.ui;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.ui.search.PerformerEventDetailAsyncTask;
import com.example.manuelsanchez.udacitycapstone.data.EventContract;

import org.json.JSONArray;
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

public class EventDetailAsyncTask extends AsyncTask<String, Void, List<Event>> {

    private final String LOG_TAG = EventDetailAsyncTask.class.getSimpleName();

    private Context mContext;
    private String artistId;

    public EventDetailAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<Event> doInBackground(String... params) {
        artistId = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        try {
            String urlBase = "http://api.eventful.com/json/performers/events/list?";
            String apiKeyParam = "app_key";
            String apiKey = mContext.getString(R.string.eventful_api_key);
            String performerIdParam = "id";


            Uri builtUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .appendQueryParameter(performerIdParam, artistId)
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
            Log.e(LOG_TAG, "Error ", e);
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

        return parseTourString(concertString);

    }

    private List<Event> parseTourString(String response) {

        Vector<ContentValues> performerEventVector = new Vector<>();

        final String JSON_ARRAY_EVENT = "event";
        final String JSON_STRING_EVENT_ID = "id";

        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray eventArray = responseObject.getJSONArray(JSON_ARRAY_EVENT);

            for (int i = 0; i < eventArray.length(); i++) {
                JSONObject eventItem = eventArray.getJSONObject(i);
                String eventId = eventItem.getString(JSON_STRING_EVENT_ID);

                ContentValues performerEvent = new ContentValues();
                performerEvent.put(EventContract.PerformerEventMapEntry.COLUMN_EVENT_ID, eventId);
                performerEvent.put(EventContract.PerformerEventMapEntry.COLUMN_PERFORMER_ID, artistId);
                performerEventVector.add(performerEvent);
                new PerformerEventDetailAsyncTask(mContext, eventId).execute(eventId);
            }

            ContentValues[] values = new ContentValues[performerEventVector.size()];
            performerEventVector.toArray(values);
            mContext.getContentResolver().bulkInsert(EventContract.PerformerEventMapEntry.CONTENT_URI, values);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
