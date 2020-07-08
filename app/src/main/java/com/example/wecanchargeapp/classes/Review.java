package com.example.wecanchargeapp.classes;

import java.sql.Time;

public class Review {

    public String rating, review, userUID;
    public String date;

    public Review(String date, String rating, String review, String userUID) {
        this.date = date;
        this.rating = rating;
        this.review = review;
        this.userUID = userUID;
    }

    public Review() {}

    public void setDate(String date) { this.date = date;}
    public void setRating(String rating) { this.rating = rating;}
    public void setReview(String review) { this.review = review;}
    public void setUserUID(String userUID) { this.userUID = userUID;}

    public String getDate() {
        return date;
    }
    public String getRating() {
        return rating;
    }
    public String getReview() {
        return review;
    }
    public String getUserUID() {
        return userUID;
    }

}
