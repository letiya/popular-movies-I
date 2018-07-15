package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.database.MovieEntry;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.MainViewModel;
import com.example.android.popularmovies.utilities.MovieFetcher;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener, MovieFetcher.MovieFetcherDisplayHandler {

    private TextView mErrorMessageDisplay;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private static boolean preferences_have_been_update = false;

    private MovieFetcher mMovieFetcher;
    private static final int MOVIE_LOADER_ID = 0;

    private Movie[] mFavoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mMovieFetcher = new MovieFetcher(this, mMovieAdapter, this);

        setupViewModel();
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
        preferences_have_been_update = true;
        mMovieFetcher.sortingChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preferences_have_been_update) {
            loadMovieData();
            preferences_have_been_update = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Dynamically calculate the number of columns and the layout would adapt to the screen size and orientation
     * @return number of columns in the grid for GridLayoutManager.
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    /**
     * Check if cell phone is connected to the internet.
     * @return true if it is connected. False otherwise.
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadMovieData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String keyForSorting = this.getString(R.string.pref_sorting_key);
        String defaultSorting = this.getString(R.string.pref_sorting_top_rated);
        String preferredSorting = sharedPreferences.getString(keyForSorting, defaultSorting);
        String favorite = this.getString(R.string.pref_sorting_favorite);
        if (preferredSorting.equals(favorite)) {
            mMovieAdapter.setmMovieData(mFavoriteMovies);
        } else if (isOnline()) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, mMovieFetcher);
        } else {
            showErrorMessage();
        }
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                if (movieEntries == null || movieEntries.size() == 0) {
                    mFavoriteMovies = null;
                } else {
                    mFavoriteMovies = new Movie[movieEntries.size()];
                    for (int i = 0; i < movieEntries.size(); i++) {
                        MovieEntry movieEntry = movieEntries.get(i);
                        Movie movie = new Movie(movieEntry.getId(), movieEntry.getVoteAvg(), movieEntry.getTitle(), movieEntry.getPosterPath(), movieEntry.getOverview(), movieEntry.getReleaseDate());
                        mFavoriteMovies[i] = movie;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMovieAdapter.setmMovieData(mFavoriteMovies);
                    }
                });
            }
        });
    }

    @Override
    public void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
