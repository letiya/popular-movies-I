package com.example.android.popularmovies.model;

public class MovieReview {

    private String mAuthor;
    private String mComment;

    public MovieReview(String author, String comment) {
        mAuthor = author;
        mComment = comment;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getComment() {
        return mComment;
    }

}
