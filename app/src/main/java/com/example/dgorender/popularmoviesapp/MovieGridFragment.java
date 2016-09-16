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
import android.widget.GridView;
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

    private ImageListAdapter mMoviesAdapter;

    private static String[] eatFoodyImages = {
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg",
    };

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviegrid_fragment, container, false);

        ArrayList<String> urls= new ArrayList(Arrays.asList(eatFoodyImages));
        /*mMoviesAdapter = new ImageListAdapter(getActivity(), eatFoodyImages);
        GridView mMoviesView = (GridView) rootView.findViewById(R.id.moviegridview);
        mMoviesView.setAdapter(mMoviesAdapter);*/
        mMoviesAdapter = new ImageListAdapter(getActivity(),R.layout.grid_image_item, new ArrayList<String>());
        GridView grid = (GridView) rootView.findViewById(R.id.usage_example_gridview);
        grid.setAdapter(mMoviesAdapter);
        /*for (String s : eatFoodyImages) {
            mMoviesAdapter.add(s);
        }*/
        //mMoviesAdapter.add("http://www.moviedeskback.com/wp-content/uploads/2016/01/Suicide_Squad_Poster.jpg");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute();
    }

    private String[] getMovieTitles(String moviesJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String base_url = "http://image.tmdb.org/t/p/w185";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

        String[] resultStrs = new String[moviesArray.length()];
        for(int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie title
            JSONObject movieJson = moviesArray.getJSONObject(i);
            resultStrs[i] = base_url + movieJson.getString("poster_path");
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
                        "http://api.themoviedb.org/3/movie/popular?";
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
