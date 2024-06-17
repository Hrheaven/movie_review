package com.heaven.moviereview;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Cursor cursor;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int movieId);
        void onLongClickMovie(int movieId);
    }
    public MovieAdapter(Cursor cursor) {
        this.cursor = cursor;
    }
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            int movieId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry._ID));
            String movieName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_MOVIE_NAME));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry.COLUMN_YEAR));

            holder.bind(movieId, movieName, year);
        }
    }
    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMovieName;
        private TextView textViewYear;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMovieName = itemView.findViewById(R.id.textViewMovieName);
            textViewYear = itemView.findViewById(R.id.textViewYear);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && cursor != null && cursor.moveToPosition(getAdapterPosition())) {
                        int movieId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry._ID));
                        listener.onItemClick(movieId);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null && cursor != null && cursor.moveToPosition(getAdapterPosition())) {
                        int movieId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MovieEntry._ID));
                        listener.onLongClickMovie(movieId);
                        return true;
                    }
                    return false;
                }
            });
        }
        public void bind(int movieId, String movieName, String year) {
            textViewMovieName.setText(" " + movieName);
            textViewYear.setText("Year: " + year);
        }
    }
}
