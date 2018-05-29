package com.example.android.popularmovies.utilities;

import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    private static final String JSON_RESULTS = "results";
    private static final String JSON_VOTE_COUNT = "vote_count";
    private static final String JSON_ID = "id";
    private static final String JSON_VIDEO = "video";
    private static final String JSON_VOTE_AVG = "vote_average";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POPULARITY = "popularity";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_ORIGINAL_LANGUAGE = "original_language";
    private static final String JSON_ORIGINAL_TITLE = "original_title";
    private static final String JSON_GENRE_IDS = "genre_ids";
    private static final String JSON_BACKDROP_PATH = "backdrop_path";
    private static final String JSON_ADULT = "adult";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";

    /**
     * Parse a json String and store information to each movie object.
     * @param json contains information for each movie.
     * @return an array of Movie objects.
     */
    public static Movie[] parseMovieJson(String json) {
        if (json == null) {
            return null;
        }
        Movie[] movies = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray movieArray = jsonObject.getJSONArray(JSON_RESULTS);
            movies = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieAttributes = movieArray.getJSONObject(i);
                movies[i] = getMovieAttributes(movieAttributes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid json String: " + json);
            return null;
        }
        return movies;
    }

    /**
     * A helper function to parse movie attributes in a JSONObject.
     * @param movieAttributes a JSONObject contains information of a movie.
     * @return a movie object with all attributes.
     */
    private static Movie getMovieAttributes(JSONObject movieAttributes) {
        if (movieAttributes == null) {
            return null;
        }
        Movie movie = null;
        try {
            int voteCount = Integer.parseInt(movieAttributes.getString(JSON_VOTE_COUNT));
            int Id = Integer.parseInt(movieAttributes.getString(JSON_ID));
            boolean hasVideo = Boolean.parseBoolean(movieAttributes.getString(JSON_VIDEO));
            double voteAvg = Double.parseDouble(movieAttributes.getString(JSON_VOTE_AVG));
            String title = movieAttributes.getString(JSON_TITLE);
            double popularity = Double.parseDouble(movieAttributes.getString(JSON_POPULARITY));
            String posterPath = movieAttributes.getString(JSON_POSTER_PATH);
            String originalLanguage = movieAttributes.getString(JSON_ORIGINAL_LANGUAGE);
            String originalTitle = movieAttributes.getString(JSON_ORIGINAL_TITLE);

            JSONArray genreIdArray = movieAttributes.getJSONArray(JSON_GENRE_IDS);
            List<Double> genreIds = new ArrayList<Double>();
            for (int i = 0; i < genreIdArray.length(); i++) {
                genreIds.add(genreIdArray.getDouble(i));
            }

            String backdropPath = movieAttributes.getString(JSON_BACKDROP_PATH);
            boolean adult = Boolean.parseBoolean(movieAttributes.getString(JSON_ADULT));
            String overview = movieAttributes.getString(JSON_OVERVIEW);
            String releaseDate = movieAttributes.getString(JSON_RELEASE_DATE);
            movie = new Movie(voteCount, Id, hasVideo, voteAvg, title, popularity, posterPath, originalLanguage, originalTitle, genreIds, backdropPath, adult, overview, releaseDate);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid JSONObject arg!");
        }
        return movie;
    }
}
