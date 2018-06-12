package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder> {

    private String[] mTrailerTitles;
    private List<String> mTrailerKeys;
    private Context mContext;

    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mMovieTrailerView;

        public MovieTrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieTrailerView = itemView.findViewById(R.id.tv_movie_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String key = mTrailerKeys.get(adapterPosition);
            watchYoutubeVideo(mContext, key);
        }
    }

    /**
     * A helper method which will open a youtube app/web with provided key.
     * @param context
     * @param key
     */
    public static void watchYoutubeVideo(Context context, String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (Exception ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_items;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerAdapterViewHolder holder, int position) {
        String trailer = mTrailerTitles[position];
        holder.mMovieTrailerView.setText(trailer);
    }

    @Override
    public int getItemCount() {
        if (mTrailerTitles == null) {
            return 0;
        } else {
            return mTrailerTitles.length;
        }
    }

    public void setmTrailerTitle(String[] trailerTitles) {
        mTrailerTitles = trailerTitles;
        notifyDataSetChanged();
    }

    public void setTrailerKeys(List<String> keys) {
        mTrailerKeys = keys;
    }
}
