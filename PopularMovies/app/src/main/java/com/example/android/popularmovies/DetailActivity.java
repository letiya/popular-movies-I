package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.database.MovieEntry;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.AppExecutors;
import com.example.android.popularmovies.utilities.MovieReviewFetcher;
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
    private MovieVideoFetcher mMovieVideoFetcher;
    private static final int TRAILER_LOADER_ID = 1;

    private LinearLayout mLinearLayout;
    private MovieReviewFetcher mMovieReviewFetcher;
    private static final int REVIEW_LOADER_ID = 2;

    private ImageView mFavoriteImageView;
    private AppDatabase mDb;

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
        mLinearLayout = findViewById(R.id.ll_reviews);

        Intent intentThatStartedThisActivity = getIntent();
        final Movie movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
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

        mMovieVideoFetcher = new MovieVideoFetcher(this, movie, mTrailerAdapter);
        loadMovieVideoData();

        mMovieReviewFetcher = new MovieReviewFetcher(this, movie, mLinearLayout);
        loadMovieReviewData();

        mDb = AppDatabase.getInstance(getApplicationContext());
        mFavoriteImageView = findViewById(R.id.iv_image_star);
        final ColorStateList oroginalColor = mFavoriteImageView.getImageTintList();
        final ColorStateList favoriteColor = ColorStateList.valueOf(getResources().getColor(R.color.colorPink));

        // Set initial color for mFavoriteImageView
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movieAdded = mDb.movieDao().selectByMovieId(movie.getColumnId());

                if (movieAdded != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteImageView.setImageTintList(favoriteColor);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteImageView.setImageTintList(oroginalColor);
                        }
                    });
                }
            }
        });

        // Handle click for mFavoriteImageView
        mFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MovieEntry movieAdded = mDb.movieDao().selectByMovieId(movie.getColumnId());
                        if (movieAdded != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFavoriteImageView.setImageTintList(oroginalColor);
                                }
                            });
                            mDb.movieDao().deleteMovie(movieAdded);
                        } else {
                            movieAdded = new MovieEntry(movie.getColumnId(), movie.getTitle(), movie.getVoteAvg(), movie.getPosterPath(), movie.getOverview(), movie.getReleaseDate());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFavoriteImageView.setImageTintList(favoriteColor);
                                }
                            });
                            mDb.movieDao().insertMovie(movieAdded);
                        }
                    }
                });
            }
        });
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
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, mMovieVideoFetcher);
        } else {
            showErrorMessage();
        }
    }

    private void loadMovieReviewData() {
        if (isOnline()) {
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, mMovieReviewFetcher);
        } else {
            showErrorMessage();
        }
    }
}
