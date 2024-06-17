package com.heaven.moviereview;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchMovieActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {
// get this
private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;
    private DatabaseHelper dbHelper;
    private int selectedMovieId;
    private SearchView searchView;
    private static final int EDIT_MOVIE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_movie);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        registerForContextMenu(recyclerViewMovies);

        refreshMovieData();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits the query (e.g., by pressing Enter)
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        Cursor cursor;
        if (query.isEmpty()) {
            // If the search query is empty, retrieve all movies
            cursor = getMovieData();
        } else {
            // If the search query is not empty, perform a database query with the search query
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selection = DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + query + "%"};
            cursor = db.query(
                    DatabaseContract.MovieEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
        }

        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(cursor);
            movieAdapter.setOnItemClickListener(this);
            recyclerViewMovies.setAdapter(movieAdapter);
        } else {
            movieAdapter.swapCursor(cursor);
        }
    }

    private Cursor getMovieData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.MovieEntry._ID,
                DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME,
                DatabaseContract.MovieEntry.COLUMN_YEAR
        };
        String sortOrder = DatabaseContract.MovieEntry._ID + " ASC";
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

    private void refreshMovieData() {
        Cursor cursor = getMovieData();
        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(cursor);
            movieAdapter.setOnItemClickListener(this);
            recyclerViewMovies.setAdapter(movieAdapter);
        } else {
            movieAdapter.swapCursor(cursor);
        }
    }

    public void openAddMovieActivity(View view) {
        Intent intent = new Intent(this, AddMovieActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMovieData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movieAdapter != null) {
            movieAdapter.swapCursor(null);
        }
        dbHelper.close();
    }

    @Override
    public void onItemClick(int movieId) {
        openMovieDetailsActivity(movieId);
    }

    private void openMovieDetailsActivity(int movieId) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
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

    @Override
    public void onLongClickMovie(int movieId) {
        // Display context menu
        selectedMovieId = movieId;
        openContextMenu(recyclerViewMovies);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit) {
            // Handle edit menu item
            openEditMovieActivity(selectedMovieId);
            return true;
        } else if (itemId == R.id.menu_delete) {
            // Handle delete menu item
            showDeleteConfirmationDialog(selectedMovieId);
            return true;
        } else if (itemId == R.id.menu_share) {
            shareMovie(selectedMovieId);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void shareMovie(int movieId) {
        Cursor cursor = dbHelper.getMovie(movieId);
        if (cursor != null && cursor.moveToFirst()) {
            String movieName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_YEAR));

            String message = "Check out this movie:\n" +
                    "Name: " + movieName + "\n" +
                    "Year: " + year;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Recommendation");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else {
                Toast.makeText(this, "No app available to handle the share action", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditMovieActivity(int movieId) {
        Intent intent = new Intent(this, UpdateMovieActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOVIE_REQUEST && resultCode == RESULT_OK) {
            refreshMovieData();
        }
    }

    private void deleteMovie(int movieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movieId)};
        int deletedRows = db.delete(DatabaseContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
        if (deletedRows > 0) {
            Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete movie", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void showDeleteConfirmationDialog(final int movieId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the method to delete the item from the ListView
                        deleteMovie(movieId);
                        refreshMovieData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new android.app.AlertDialog.Builder(SearchMovieActivity.this)
                .setTitle("Exit APP!")
                .setMessage("Do you Really want to Exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "No", dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "Yes", close the activity and exit the app
                        dialog.dismiss();
                        finishAndRemoveTask();
                    }
                })
                .show();
    }
}
//no error