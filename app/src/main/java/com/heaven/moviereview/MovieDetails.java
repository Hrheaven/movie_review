package com.heaven.moviereview;

public class MovieDetails {
    private int movieId;
    private String name;
    private String year;
    private String rating;
    private String genre;

    public MovieDetails(int movieId, String name, String year, String rating, String genre) {
        this.movieId = movieId;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
    }
    public int getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }
}
