package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String URL_PATH_POPULAR = "popular";
    private static final String URL_PATH_TOP_RATED = "top_rated";
    private static final String API_KEY_PARAM = "api_key";

    private static final String TAG_VIDEOS = "videos";
    private static final String TAG_REVIEWS = "reviews";

    /**
     * This method builds the URL used to talk to movie db server.
     * @return a URL to query the movie server.
     */
    public static URL buildMovieSummaryUrl(Context context) {
        String sortBy; // sortBy the most popular, or by top rated movie.

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSorting = context.getString(R.string.pref_sorting_key);
        String defaultSorting = context.getString(R.string.pref_sorting_top_rated);
        String preferredSorting = sharedPreferences.getString(keyForSorting, defaultSorting);
        String top_rated = context.getString(R.string.pref_sorting_top_rated);
        if (preferredSorting.equals(top_rated)) {
            sortBy = URL_PATH_TOP_RATED;
        } else {
            sortBy = URL_PATH_POPULAR;
        }

        Uri uri = Uri.parse(MOVIE_BASE_URL)
                .buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY_PARAM, MovieAPI.API_KEY)
                .build();

        if (uri == null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid uri: " + uri);
            return null;
        }
    }

    public static URL buildMovieVideoUrl(int movieId) {
        Uri uri = Uri.parse(MOVIE_BASE_URL)
                .buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(TAG_VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, MovieAPI.API_KEY)
                .build();

        if (uri == null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid uri: " + uri);
            return null;
        }
    }

    public static URL buildMovieReviewUrl(int movieId) {
        Uri uri = Uri.parse(MOVIE_BASE_URL)
                .buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(TAG_REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, MovieAPI.API_KEY)
                .build();

        if (uri == null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid uri: " + uri);
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     */
    public static String getResponseFromHttpUrl(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A"); // Regular expression - Beginning of string.

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                Log.e(TAG, "No result from HTTP!");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "No internet connection!");
            return null;
        }  finally {
            urlConnection.disconnect();
        }
    }
}
