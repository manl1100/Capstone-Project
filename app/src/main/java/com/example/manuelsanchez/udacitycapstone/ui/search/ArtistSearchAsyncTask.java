package com.example.manuelsanchez.udacitycapstone.ui.search;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.ui.Artist;

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

import static com.example.manuelsanchez.udacitycapstone.util.EventfulJsonConstants.*;


public class ArtistSearchAsyncTask extends AsyncTask<String, Void, List<Artist>> {

    private final String LOG_TAG = ArtistSearchAsyncTask.class.getSimpleName();

    private ArtistSearchActivityFragment.ArtistSearchItemRecyclerViewAdapter adapter;
    private Context mContext;

    public ArtistSearchAsyncTask(Context context, ArtistSearchActivityFragment.ArtistSearchItemRecyclerViewAdapter simpleItemRecyclerViewAdapter) {
        mContext = context;
        adapter = simpleItemRecyclerViewAdapter;
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        return fetchArtistSearchResults(params[0]);
    }

    private List<Artist> fetchArtistSearchResults(String query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String concertString = null;

        try {
            String urlBase = "http://api.eventful.com/json/performers/search?";
            String apiKeyParam = "app_key";
            String apiKey = mContext.getString(R.string.eventful_api_key);
            String keyWordParam = "keywords";

            Uri builtUri = Uri.parse(urlBase).buildUpon()
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .appendQueryParameter(keyWordParam, query)
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

        return parseArtistSearchString(concertString);

    }

    private List<Artist> parseArtistSearchString(String response) {
        List<Artist> output = new ArrayList<>();

        try {
            JSONObject responseObject = new JSONObject(response);
            JSONObject eventObject = responseObject.getJSONObject(JSON_OBJECT_PERFORMERS);
            if (eventObject.get(JSON_PERFORMER) instanceof JSONArray) {
                JSONArray eventArray = eventObject.getJSONArray(JSON_PERFORMER);
                for (int i = 0; i < eventArray.length(); i++) {
                    output.add(createArtist(eventArray.getJSONObject(i)));
                }
            } else {
                JSONObject performer = eventObject.getJSONObject(JSON_PERFORMER);
                output.add(createArtist(performer));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    private Artist createArtist(JSONObject performer) throws JSONException {
        String name = performer.getString(JSON_STRING_NAME);
        String id = performer.getString(JSON_STRING_PERFORMER_ID);
        Integer eventCount = performer.getInt(JSON_INTEGER_EVENT_COUNT);
        JSONObject image = performer.getJSONObject(JSON_OBJECT_IMAGE);
        JSONObject medium = image.getJSONObject(JSON_OBJECT_MEDIUM_IMAGE);
        String url = medium.getString(JSON_STRING_IMAGE_URL);

        return new Artist.Builder()
                .withArtistName(name)
                .withEventCount(eventCount)
                .withUrl(url)
                .withEventfulId(id)
                .build();
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        adapter.setData(artists);
        adapter.notifyDataSetChanged();
    }
}
