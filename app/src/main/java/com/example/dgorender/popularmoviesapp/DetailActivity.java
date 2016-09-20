package com.example.dgorender.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            this.movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        new FetchMovieTask(this).execute(this.movieId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Movie getMovie(String movieJsonStr) throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        String id = movieJson.getString("id");
        String title = movieJson.getString("title");
        String releaseDate = movieJson.getString("release_date").substring(0,4);
        String runTime = movieJson.getString("runtime")+"min";
        String synopsis = movieJson.getString("overview");
        String userRating = movieJson.getString("vote_average") + "/10";
        String posterPath = movieJson.getString("poster_path");

        return new Movie(id, title, synopsis, userRating, posterPath, releaseDate, runTime);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private Context context;

        public FetchMovieTask(Context context) {
            this.context = context;
        }

        protected Movie doInBackground(String... params) {
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
                        "http://api.themoviedb.org/3/movie/";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL + params[0] + "?")
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
            Movie result;
            try {
                result = getMovie(moviesJsonStr);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                TextView titleView = (TextView) findViewById(R.id.detail_title);
                titleView.setText(movie.getTitle());
                Picasso
                        .with(this.context)
                        .load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                        .into((ImageView) findViewById(R.id.detail_poster));
                TextView releaseDateView = (TextView) findViewById(R.id.detail_release_date);
                releaseDateView.setText(movie.getReleaseDate());
                TextView runtimeView = (TextView) findViewById(R.id.detail_runtime);
                runtimeView.setText(movie.getRunTime());
                TextView userRatingView = (TextView) findViewById(R.id.detail_user_rating);
                userRatingView.setText(movie.getUserRating());
                TextView synopsisView = (TextView) findViewById(R.id.detail_synopsis);
                synopsisView.setText(movie.getSynopsis());
                synopsisView.setMovementMethod(new ScrollingMovementMethod());

            }
        }

    }

}
