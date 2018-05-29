package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mErrorMessageDisplay;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieData();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movie, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie clickedMovie) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, clickedMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATE = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATE) {
            loadMovieData();
            PREFERENCES_HAVE_BEEN_UPDATE = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadMovieData() {
        if (isOnline()) {
            new FetchNetworkRequest().execute();
        } else {
            showErrorMessage();
        }
    }

    private class FetchNetworkRequest extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            URL movieRequestUrl = NetworkUtils.buildUrl(MainActivity.this);
            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                Movie[] movies = MovieJsonUtils.parseMovieJson(jsonMovieResponse);
                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mMovieAdapter.setmMovieData(movies);
            if (movies == null) {
                showErrorMessage();
            } else {
                showMovieDataView();
            }
        }
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
