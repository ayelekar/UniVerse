package com.universe.ifr.universe;

/**
 * Created by Asim on 7/15/2016.
 */
public class Comment {
    String username;
    int rating;
    String date;
    String feedback;
    public Comment (String user, int rating, String date, String feedback) {
        this.username = user;
        this.rating = rating;
        this.date = date;
        this.feedback = feedback;
    }
}
