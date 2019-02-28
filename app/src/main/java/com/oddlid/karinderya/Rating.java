package com.oddlid.karinderya;

public class Rating {
    public Rating()
    {

    }

    public Rating(float rating, float rated)
    {
        this.rating = rating;
        this.rated = rated;
    }

    private float rating, rated;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRated() {
        return rated;
    }

    public void setRated(float rated) {
        this.rated = rated;
    }
}
