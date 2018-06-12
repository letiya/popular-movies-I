package com.example.android.popularmovies;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.MovieVideoFetcher;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private ImageView mMovieImageView;
    private TextView mMovieOverview;
    private TextView mMovieVoteAvg;
    private TextView mMovieReleaseDate;
    private TextView mErrorMessageDisplay;
    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342/";

    private RecyclerView mTrailerRecyclerView;
    private MovieTrailerAdapter mTrailerAdapter;
    private MovieVideoFetcher movieVideoFetcher;
    private static final int TRAILER_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieImageView = findViewById(R.id.iv_image_thumbnail);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mMovieVoteAvg = findViewById(R.id.tv_movie_voteAvg);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_date);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
        mMovieTitle.setText(movie.getTitle());

        String posterPath = BASE_POSTER_PATH + movie.getPosterPath();
        Picasso.with(this).load(posterPath).into(mMovieImageView);

        mMovieOverview.setText(movie.getOverview());
        mMovieVoteAvg.setText(String.valueOf(movie.getVoteAvg()));
        mMovieReleaseDate.setText(movie.getReleaseDate());


        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_trailer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(linearLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
        mTrailerAdapter = new MovieTrailerAdapter();
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        movieVideoFetcher = new MovieVideoFetcher(this, movie, mTrailerAdapter);
        loadMovieVideoData();
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
    public void showErrorMessage() {
        /* First, hide the currently visible data */
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void loadMovieVideoData() {
        if (isOnline()) {
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, movieVideoFetcher);
        } else {
            showErrorMessage();
        }
    }
}
