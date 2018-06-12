package com.example.android.popularmovies.utilities;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.content.Context;
import android.os.Bundle;

import com.example.android.popularmovies.MovieTrailerAdapter;
import com.example.android.popularmovies.model.Movie;

import java.net.URL;
import java.util.List;

public class MovieVideoFetcher implements LoaderManager.LoaderCallbacks<List<String>> {

    private Context mContext;
    private Movie mMovie;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    public MovieVideoFetcher(Context context, Movie movie, MovieTrailerAdapter movieTrailerAdapter) {
        mContext = context;
        mMovie = movie;
        mMovieTrailerAdapter = movieTrailerAdapter;
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<String>>(mContext) {

            @Override
            protected void onStartLoading() {
                if (mMovie.getVideoKey() != null) {
                    deliverResult(mMovie.getVideoKey());
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<String> loadInBackground() {
                URL movieVideoRequestUrl = NetworkUtils.buildMovieVideoUrl(mMovie.getColumnId());
                String jsonMovieVideoResponse = NetworkUtils.getResponseFromHttpUrl(movieVideoRequestUrl);
                MovieJsonUtils.parseMovieVideoJson(jsonMovieVideoResponse, mMovie);
                return mMovie.getVideoKey();
            }

            @Override
            public void deliverResult(List<String> data) {
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        int numOfTrailers = data.size();
        String[] trailers = new String[numOfTrailers];
        for (int i = 0; i < numOfTrailers; i++) {
            int trailerNum = i + 1;
            trailers[i] = "Trailer " + trailerNum;
        }
        mMovieTrailerAdapter.setmTrailerTitle(trailers);
        mMovieTrailerAdapter.setTrailerKeys(data);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }
}
