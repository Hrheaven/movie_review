package com.heaven.moviereview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.MovieEntry.TABLE_NAME + " (" +
                    DatabaseContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME + " TEXT, " +
                    DatabaseContract.MovieEntry.COLUMN_YEAR + " INTEGER, " +
                    DatabaseContract.MovieEntry.COLUMN_RATING + " REAL, " +
                    DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME + " TEXT, " +
                    DatabaseContract.MovieEntry.COLUMN_GENRE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.MovieEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long addMovie(String movieName, String year, float rating, String actorName, String genre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        values.put(DatabaseContract.MovieEntry.COLUMN_YEAR, year);
        values.put(DatabaseContract.MovieEntry.COLUMN_RATING, rating);
        values.put(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME, actorName);
        values.put(DatabaseContract.MovieEntry.COLUMN_GENRE, genre);
        return db.insert(DatabaseContract.MovieEntry.TABLE_NAME, null, values);
    }

    public boolean updateMovie(int movieId, String movieName, String year, float rating, String actorName, String genre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        values.put(DatabaseContract.MovieEntry.COLUMN_YEAR, year);
        values.put(DatabaseContract.MovieEntry.COLUMN_RATING, rating);
        values.put(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME, actorName);
        values.put(DatabaseContract.MovieEntry.COLUMN_GENRE, genre);
        String selection = DatabaseContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};
        int rowsAffected = db.update(DatabaseContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
        return rowsAffected > 0;
    }

    public Cursor getMovie(int movieId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DatabaseContract.MovieEntry._ID,
                DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME,
                DatabaseContract.MovieEntry.COLUMN_YEAR,
                DatabaseContract.MovieEntry.COLUMN_RATING,
                DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME,
                DatabaseContract.MovieEntry.COLUMN_GENRE
        };
        String selection = DatabaseContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};
        return db.query(
                DatabaseContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public Cursor getAllMovies() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DatabaseContract.MovieEntry._ID,
                DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME,
                DatabaseContract.MovieEntry.COLUMN_YEAR,
                DatabaseContract.MovieEntry.COLUMN_RATING,
                DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME,
                DatabaseContract.MovieEntry.COLUMN_GENRE
        };
        String sortOrder = DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME + " ASC";
        return db.query(
                DatabaseContract.MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    public Cursor searchMovies(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        return db.query(
                DatabaseContract.MovieEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
}
