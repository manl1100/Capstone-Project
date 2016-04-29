package com.example.manuelsanchez.udacitycapstone;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

public class EventDetailAsyncTask extends AsyncTask<String, Void, List<Event>> {

    private final String LOG_TAG = EventDetailAsyncTask.class.getSimpleName();

    private ArtistDetailActivityFragment.TourDateRecyclerViewAdapter mTourDateRecyclerViewAdapter;
    private Context mContext;

    public EventDetailAsyncTask(Context context, ArtistDetailActivityFragment.TourDateRecyclerViewAdapter tourDateRecyclerViewAdapter) {
        mContext = context;
        mTourDateRecyclerViewAdapter = tourDateRecyclerViewAdapter;
    }

    @Override
    protected List<Event> doInBackground(String... params) {
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
                    .appendQueryParameter(performerIdParam, params[0])
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

        return parseTourString(concertString);

    }

    private List<Event> parseTourString(String response) {
        List<Event> events = new ArrayList<Event>();

        final String JSON_ARRAY_EVENT = "event";
        final String JSON_STRING_COUNTRY = "country";
        final String JSON_STRING_REGION = "region";
        final String JSON_STRING_CITY = "city";
        final String JSON_STRING_EVENT_ID = "id";
        final String JSON_STRING_TIME = "start_time";

        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray eventArray = responseObject.getJSONArray(JSON_ARRAY_EVENT);

            for (int i = 0; i < eventArray.length(); i++) {
                JSONObject eventItem = eventArray.getJSONObject(i);

                String country = eventItem.getString(JSON_STRING_COUNTRY);
                String region = eventItem.getString(JSON_STRING_REGION);
                String city = eventItem.getString(JSON_STRING_CITY);
                String eventId = eventItem.getString(JSON_STRING_EVENT_ID);
                String eventDate = eventItem.getString(JSON_STRING_TIME);
                Event event = new Event.Builder()
                        .country(country)
                        .city(city)
                        .eventDate(eventDate)
                        .eventId(eventId)
                        .region(region)
                        .build();
                events.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        mTourDateRecyclerViewAdapter.setEventDates(events);
        mTourDateRecyclerViewAdapter.notifyDataSetChanged();
    }

}
