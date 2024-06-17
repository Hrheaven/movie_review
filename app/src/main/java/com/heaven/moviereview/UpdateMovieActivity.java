package com.heaven.moviereview;

import android.content.ContentValues;
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
import java.util.List;

public class UpdateMovieActivity extends AppCompatActivity {

    private EditText editTextMovieName;
    private Spinner spinnerYear;
    private EditText editTextActorName;
    private Button buttonUpdate;
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
        setContentView(R.layout.activity_update_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextMovieName = findViewById(R.id.editTextMovieName);
        spinnerYear = findViewById(R.id.spinnerYear);
        editTextActorName = findViewById(R.id.editTextActorName);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        ratingBar = findViewById(R.id.ratingBar);
        checkboxHorror = findViewById(R.id.checkboxHorror);
        checkboxAction = findViewById(R.id.checkboxAction);
        checkboxSciFi = findViewById(R.id.checkboxSciFi);
        checkboxComedy = findViewById(R.id.checkboxComedy);
        checkboxAdventure = findViewById(R.id.checkboxAdventure);
        checkboxRomantic = findViewById(R.id.checkboxRomantic);

        dbHelper = new DatabaseHelper(this);

        movieId = getIntent().getIntExtra("movieId", -1);

        loadMovieData();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovie();
            }
        });
    }
    private void loadMovieData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
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
            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_RATING));
            String genres = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_GENRE));
            String thoughts = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME));

            editTextMovieName.setText(movieName);

            // Set the selection of spinnerYear based on the year value
            List<String> years = new ArrayList<>();
            // Add years to the list (e.g., 1900 to 2023)
            for (int l_year = 1990; l_year <= 2023; l_year++) {
                years.add(String.valueOf(year));
            }
            ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, years);
            spinnerYear.setAdapter(yearAdapter);
            ratingBar.setRating(rating);
            checkboxHorror.setChecked(genres.contains("Horror"));
            checkboxAction.setChecked(genres.contains("Action"));
            checkboxSciFi.setChecked(genres.contains("Sci-fi"));
            checkboxComedy.setChecked(genres.contains("Comedy"));
            checkboxAdventure.setChecked(genres.contains("Adventure"));
            checkboxRomantic.setChecked(genres.contains("Romantic"));

            editTextActorName.setText(thoughts);
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
    private void updateMovie() {
        String movieName = editTextMovieName.getText().toString().trim();
        String year = spinnerYear.getSelectedItem().toString().trim();
        float rating = ratingBar.getRating();
        String thoughts = editTextActorName.getText().toString().trim();

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
        String genreString = genres.toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        values.put(DatabaseContract.MovieEntry.COLUMN_YEAR, year);
        values.put(DatabaseContract.MovieEntry.COLUMN_RATING, rating);
        values.put(DatabaseContract.MovieEntry.COLUMN_GENRE, genreString);
        values.put(DatabaseContract.MovieEntry.COLUMN_ACTOR_NAME, thoughts);

        String selection = DatabaseContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};

        int count = db.update(
                DatabaseContract.MovieEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (count > 0) {
            Toast.makeText(this, "Movie updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show();
        }
    }

}