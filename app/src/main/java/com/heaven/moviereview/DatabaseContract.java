package com.heaven.moviereview;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract() {}

    public static class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_ACTOR_NAME = "actor_name";
        public static final String COLUMN_GENRE = "genre";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_MOVIE_NAME + " TEXT, " +
                        COLUMN_YEAR + " INTEGER, " +
                        COLUMN_RATING + " REAL, " +
                        COLUMN_ACTOR_NAME + " TEXT, " +
                        COLUMN_GENRE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
