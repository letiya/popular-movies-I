package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private ImageView mMovieImageView;
    private TextView mMovieOverview;
    private TextView mMovieVoteAvg;
    private TextView mMovieReleaseDate;

    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieImageView = findViewById(R.id.iv_image_thumbnail);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mMovieVoteAvg = findViewById(R.id.tv_movie_voteAvg);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_date);

        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
        mMovieTitle.setText(movie.getTitle());

        String posterPath = BASE_POSTER_PATH + movie.getPosterPath();
        Picasso.with(this).load(posterPath).into(mMovieImageView);

        mMovieOverview.setText(movie.getOverview());
        mMovieVoteAvg.setText(String.valueOf(movie.getVoteAvg()));
        mMovieReleaseDate.setText(movie.getReleaseDate());

    }

}
