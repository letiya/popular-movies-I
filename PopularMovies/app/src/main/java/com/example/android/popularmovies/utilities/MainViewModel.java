package com.example.android.popularmovies.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        movies = appDatabase.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovies() {
        return movies;
    }
}
