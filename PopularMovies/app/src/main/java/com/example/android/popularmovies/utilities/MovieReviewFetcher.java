package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieReview;

import java.net.URL;
import java.util.List;

public class MovieReviewFetcher implements LoaderManager.LoaderCallbacks<List<MovieReview>>{

    private Context mContext;
    private Movie mMovie;
    private LinearLayout mLinearLayout;

    public MovieReviewFetcher(Context context, Movie movie, LinearLayout linearLayout) {
        mContext = context;
        mMovie = movie;
        mLinearLayout = linearLayout;
    }

    @Override
    public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<MovieReview>>(mContext) {

            @Override
            protected void onStartLoading() {
                if (mMovie.getReview() != null) {
                    deliverResult(mMovie.getReview());
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<MovieReview> loadInBackground() {
                URL movieReviewRequestUrl = NetworkUtils.buildMovieReviewUrl(mMovie.getColumnId());
                String jsonMovieReviewResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewRequestUrl);
                MovieJsonUtils.parseMovieReviewJson(jsonMovieReviewResponse, mMovie);
                return mMovie.getReview();
            }

            @Override
            public void deliverResult(List<MovieReview> data) {
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                TextView reviewTextView = new TextView(mContext);
                String review = "Author: " + data.get(i).getAuthor() + ". \n\n" + data.get(i).getComment();
                reviewTextView.setText(review);
                reviewTextView.setTextSize(15);
                reviewTextView.setPadding(20, 5, 10, 5);
                mLinearLayout.addView(reviewTextView);

                View view = new View(mContext);
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                marginLayoutParams.setMarginStart(10);
                marginLayoutParams.setMarginEnd(10);
                view.setBackgroundColor(mContext.getResources().getColor(R.color.colorBlack));
                view.setLayoutParams(new LinearLayout.MarginLayoutParams(marginLayoutParams));
                mLinearLayout.addView(view);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieReview>> loader) {

    }
}
