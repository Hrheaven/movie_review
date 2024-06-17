package com.heaven.moviereview;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMovieActivity extends AppCompatActivity {

    private EditText editTextMovieName;
    private Spinner spinnerYear;
    private EditText editTextActorName;
    private Button buttonSave;
    private RatingBar ratingBar;
    private CheckBox checkboxHorror;
    private CheckBox checkboxAction;
    private CheckBox checkboxSciFi;
    private CheckBox checkboxComedy;
    private CheckBox checkboxAdventure;
    private CheckBox checkboxRomantic;

    private DatabaseHelper dbHelper;
    private long movieId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        editTextMovieName = findViewById(R.id.editTextMovieName);
        spinnerYear = findViewById(R.id.spinnerYear);
        ratingBar = findViewById(R.id.ratingBar);
        editTextActorName = findViewById(R.id.editTextActorName);
        buttonSave = findViewById(R.id.buttonSave);
        checkboxHorror = findViewById(R.id.checkboxHorror);
        checkboxAction = findViewById(R.id.checkboxAction);
        checkboxSciFi = findViewById(R.id.checkboxSciFi);
        checkboxComedy = findViewById(R.id.checkboxComedy);
        checkboxAdventure = findViewById(R.id.checkboxAdventure);
        checkboxRomantic = findViewById(R.id.checkboxRomantic);

        setupYearSpinner();

        Intent intent = getIntent();
        if (intent.hasExtra("movie_id")) {
            movieId = intent.getLongExtra("movie_id", -1);
            if (movieId != -1) {
                loadMovieData();
            }
        }
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovie();
                Intent srcMovie= new Intent(AddMovieActivity.this, SearchMovieActivity.class);
                startActivity(srcMovie);
                finish();
            }
        });
    }
    private void setupYearSpinner() {
        List<String> years = new ArrayList<>();
        // Add years to the list (e.g., 1900 to 2023)
        for (int year = 1990; year <= 2023; year++) {
            years.add(String.valueOf(year));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void loadMovieData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME,
                DatabaseContract.MovieEntry.COLUMN_YEAR,
                DatabaseContract.MovieEntry.COLUMN_RATING,
                DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME,
                DatabaseContract.MovieEntry.COLUMN_GENRE
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

        if (cursor.moveToFirst()) {
            String movieName = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME));
            String year = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_YEAR));
            float rating = cursor.getFloat(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_RATING));
            String actorName = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME));
            String genres = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieEntry.COLUMN_GENRE));

            editTextMovieName.setText(movieName);
            int yearIndex = ((ArrayAdapter<String>) spinnerYear.getAdapter()).getPosition(year);
            spinnerYear.setSelection(yearIndex);
            ratingBar.setRating(rating);
            editTextActorName.setText(actorName);

            List<String> genreList = Arrays.asList(genres.split(","));
            for (String genre : genreList) {
                if (genre.trim().equals("Horror")) {
                    checkboxHorror.setChecked(true);
                } else if (genre.trim().equals("Action")) {
                    checkboxAction.setChecked(true);
                } else if (genre.trim().equals("Sci-fi")) {
                    checkboxSciFi.setChecked(true);
                } else if (genre.trim().equals("Comedy")) {
                    checkboxComedy.setChecked(true);
                } else if (genre.trim().equals("Adventure")) {
                    checkboxAdventure.setChecked(true);
                } else if (genre.trim().equals("Romantic")) {
                    checkboxRomantic.setChecked(true);
                }
            }
        }

        cursor.close();
    }

    private void saveMovie() {
        String movieName = editTextMovieName.getText().toString().trim();
        String year = spinnerYear.getSelectedItem().toString();
        float rating = ratingBar.getRating();
        String actorName = editTextActorName.getText().toString().trim();

        if (movieName.isEmpty()) {
            Toast.makeText(this, "Please enter a movie name", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> genres = new ArrayList<>();
        if (checkboxHorror.isChecked()) {
            genres.add("Horror");
        }
        if (checkboxAction.isChecked()) {
            genres.add("Action");
        }
        if (checkboxSciFi.isChecked()) {
            genres.add("Sci-fi");
        }
        if (checkboxComedy.isChecked()) {
            genres.add("Comedy");
        }
        if (checkboxAdventure.isChecked()) {
            genres.add("Adventure");
        }
        if (checkboxRomantic.isChecked()) {
            genres.add("Romantic");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        values.put(DatabaseContract.MovieEntry.COLUMN_YEAR, year);
        values.put(DatabaseContract.MovieEntry.COLUMN_RATING, rating);
        values.put(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME, actorName);
        values.put(DatabaseContract.MovieEntry.COLUMN_GENRE, genres.toString());

        if (movieId == -1) {
            // Insert a new movie
            long newRowId = db.insert(DatabaseContract.MovieEntry.TABLE_NAME, null, values);
            if (newRowId == -1) {
                Toast.makeText(this, "Error saving movie", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Movie saved successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update an existing movie
            String selection = DatabaseContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(movieId)};
            int rowsAffected = db.update(DatabaseContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating movie", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Movie updated successfully", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }





}