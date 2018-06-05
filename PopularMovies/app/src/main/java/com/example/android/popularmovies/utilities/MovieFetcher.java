package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.MovieAdapter;
import com.example.android.popularmovies.model.Movie;

import java.net.URL;

public class MovieFetcher implements LoaderManager.LoaderCallbacks<Movie[]> {

    private Context mContext;
    private MovieAdapter mMovieAdapter;
    private final MovieFetcherDisplayHandler mDisplayHandler;
    private boolean isSortingChanged;

    public MovieFetcher(Context context, MovieAdapter movieAdapter, MovieFetcherDisplayHandler handler) {
        mContext = context;
        mMovieAdapter = movieAdapter;
        mDisplayHandler = handler;
        isSortingChanged = false;
    }

    public interface MovieFetcherDisplayHandler {
        void showErrorMessage();
        void showMovieDataView();
    }

    /**
     * Update when the user change the sorting preference.
     */
    public void sortingChanged() {
        isSortingChanged = true;
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Movie[]>(mContext) {
            Movie[] mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null && !isSortingChanged) {
                    deliverResult(mMovieData);
                    isSortingChanged = false;
                } else {
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {
                try {
                    URL movieRequestUrl = NetworkUtils.buildMovieSummaryUrl(mContext);
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    Movie[] movies = MovieJsonUtils.parseMovieJson(jsonMovieResponse);
                    return movies;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Movie[] data) {
                mMovieData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mMovieAdapter.setmMovieData(data);
        if (data == null) {
            mDisplayHandler.showErrorMessage();
        } else {
            mDisplayHandler.showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}
