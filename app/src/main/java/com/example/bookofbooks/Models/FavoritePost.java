package com.example.bookofbooks.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FavoritePost {
    String userID, postID, favoritePostID, date, time;
    Date timestamp;
    Post post;

    public FavoritePost(){

    }

    public FavoritePost(String userID, String postID, Post post) {
        this.userID = userID;
        this.postID = postID;
        this.post = post;
        Calendar dateToCall = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        this.date = currentDateFormat.format(dateToCall.getTime());
        Calendar timeToCall = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        this.time = currentTimeFormat.format(timeToCall.getTime());
        this.timestamp = new Date(System.currentTimeMillis());
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getFavoritePostID() {
        return favoritePostID;
    }

    public void setFavoritePostID(String favoritePostID) {
        this.favoritePostID = favoritePostID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
