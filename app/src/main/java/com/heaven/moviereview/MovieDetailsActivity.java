package com.heaven.moviereview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView textViewMovieName;
    private TextView textViewYear;
    private RatingBar ratingBar;
    private TextView textViewGenre;
    private TextView textViewActorName;

    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewMovieName = findViewById(R.id.textViewMovieName);
        textViewYear = findViewById(R.id.textViewYear);
        ratingBar = findViewById(R.id.ratingBar);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewActorName = findViewById(R.id.textViewActorName);

        dbHelper = new DatabaseHelper(this);

        // Get the movie ID from the intent
        Intent intent = getIntent();
        int movieId = intent.getIntExtra("movieId", -1);

        if (movieId != -1) {
            // Retrieve movie details from the database
            Movie movie = getMovieDetails(movieId);

            if (movie != null) {
                // Set the movie details in the TextViews
                textViewMovieName.setText(" " + movie.getMovieName());
                textViewYear.setText("Release year: " + movie.getYear());
                ratingBar.setRating(Float.parseFloat(movie.getRating()));
                textViewGenre.setText("Genre: " + movie.getGenre());
                textViewActorName.setText("Actor Name: " + movie.getActorName());
            } else {
                // Movie details not found, display an error message or handle it accordingly
                textViewMovieName.setText("Movie details not found");
            }
        } else {
            // Invalid movie ID, display an error message or handle it accordingly
            textViewMovieName.setText("Invalid movie ID");
        }
    }

    private Movie getMovieDetails(int movieId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.MovieEntry._ID,
                DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME,
                DatabaseContract.MovieEntry.COLUMN_YEAR,
                DatabaseContract.MovieEntry.COLUMN_RATING,
                DatabaseContract.MovieEntry.COLUMN_GENRE,
                DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME
        };

        String selection = DatabaseContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        Cursor cursor = db.query(
                DatabaseContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String movieName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_YEAR));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_RATING));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_GENRE));
            String actorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME));
            cursor.close();
            return new Movie(movieId, movieName, year, rating, genre, actorName);
        }
        return null;
    }
    public static class Movie {
        private int movieId;
        private String movieName;
        private String year;
        private float rating;
        private String genre;
        private String actorName;
        public Movie(int movieId, String movieName, String year, String rating, String genre, String actorName) {
            this.movieId = movieId;
            this.movieName = movieName;
            this.year = year;
            this.rating = Float.parseFloat(rating);
            this.genre = genre;
            this.actorName = actorName;
        }

        public Movie(int id, String movieName, String year, float rating, String actorName, String genre) {
        }

        public String getMovieName() {
            return movieName;
        }
        public String getYear() {
            return year;
        }
        public String getRating() {
            return String.valueOf(rating);
        }
        public String getGenre() {
            return genre;
        }
        public String getActorName() {
            return actorName;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            finishAffinity();
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}