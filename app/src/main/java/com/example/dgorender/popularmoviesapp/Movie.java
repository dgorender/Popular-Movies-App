package com.example.dgorender.popularmoviesapp;

/**
 * Created by dgorender on 16/09/16.
 */
public class Movie {

    private String id, title, synopsis, userRating, posterPath, releaseDate;

    public Movie(String id, String title, String synopsis, String userRating, String posterPath, String releaseDate) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    public Movie(String id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}