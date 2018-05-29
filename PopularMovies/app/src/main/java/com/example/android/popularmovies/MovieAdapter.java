package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Movie[] mMovieData;
    private Context mContext;

    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w185/";

    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler handler) {
        mClickHandler = handler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMovieImageView;
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieData[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    public void setmMovieData(Movie[] movies) {
        mMovieData = movies;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_items;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData[position];
        String posterPath = BASE_POSTER_PATH + movie.getPosterPath();
        Picasso.with(mContext).load(posterPath).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) {
            return 0;
        } else {
            return mMovieData.length;
        }
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movieClicked);
    }
}
