package com.example.dgorender.popularmoviesapp;


import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Arrays;
import java.util.List;

public class MovieGridFragment extends Fragment {

    private ArrayAdapter<String> mMoviesAdapter;

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviegrid_fragment, container, false);
        mMoviesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        ListView mMoviesView = (ListView) rootView.findViewById(R.id.moviegridlayout);
        mMoviesView.setAdapter(mMoviesAdapter);
        //mMoviesAdapter.add("Movie 1");
        //mMoviesAdapter.add("Movie 2");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute();
    }

    private String[] getMovieTitles(String moviesJsonStr) throws JSONException {

        final String TMDB_ITEMS = "items";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_ITEMS);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        String[] resultStrs = new String[moviesArray.length()];
        for(int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie title
            JSONObject movieJson = moviesArray.getJSONObject(i);
            resultStrs[i] = movieJson.getString("title");
        }
        return resultStrs;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/list/509ec17b19c2950a0600050d?";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
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
            String[] result;
            try {
                result = getMovieTitles(moviesJsonStr);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mMoviesAdapter.clear();
            if (strings != null) {
                for (String s : strings) {
                    mMoviesAdapter.add(s);
                }
            }
        }
    }
}
