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


public class EventAsyncTask extends AsyncTask<String, Void, List<Event>> {

    private final String LOG_TAG = EventAsyncTask.class.getSimpleName();

    private Context mContext;
    private EventItemListActivity.SimpleItemRecyclerViewAdapter mConcertEventAdapter;

    public EventAsyncTask(Context context, EventItemListActivity.SimpleItemRecyclerViewAdapter eventAdapter) {
        this.mContext = context;
        this.mConcertEventAdapter = eventAdapter;
    }

    @Override
    protected List<Event> doInBackground(String... params) {
        return fetchConcertInArea("75209");
    }

    private List<Event> fetchConcertInArea(String search) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        try {
            String urlBase = "http://api.eventful.com/json/events/search?";
            String apiKeyParam = "app_key";
            String apiKey = mContext.getString(R.string.eventful_api_key);
            String categoryParam = "category";
            String categoryParamValue = "music";
            String locationParam = "location";
            String locationParamValue = "75209";
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
                    .appendQueryParameter(locationParam, locationParamValue)
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

        return parseConcertString(concertString);

    }

    private List<Event> parseConcertString(String response) {
        List<Event> output = new ArrayList<>();

        final String JSON_OBJECT_EVENT = "events";
        final String JSON_ARRAY_EVENT = "event";
        final String JSON_STRING_DATE = "start_time";
        final String JSON_STRING_VENUE_NAME = "venue_name";
        final String JSON_DOUBLE_LATITUDE = "latitude";
        final String JSON_DOUBLE_LONGITUDE = "longitude";
        final String JSON_OBJECT_ARTIST = "performers";
        final String JSON_ARTIST = "performer";
        final String JSON_STRING_ARTIST = "name";

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
                        artistList.add(new Artist(artistName));
                    }
                } else {
                    String artist = artistObject.getJSONObject(JSON_ARTIST).getString(JSON_STRING_ARTIST);
                    artistList.add(new Artist(artist));
                }


                Event event = new Event.Builder()
                        .venueName(eventItem.getString(JSON_STRING_VENUE_NAME))
                        .latitude(eventItem.getDouble(JSON_DOUBLE_LATITUDE))
                        .longitude(eventItem.getDouble(JSON_DOUBLE_LONGITUDE))
                        .eventDate(eventItem.getString(JSON_STRING_DATE))
                        .artists(artistList)
                        .build();
                output.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        mConcertEventAdapter.setData(events);
        mConcertEventAdapter.notifyDataSetChanged();
    }
}