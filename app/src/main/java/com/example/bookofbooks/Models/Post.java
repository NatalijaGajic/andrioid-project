package com.example.bookofbooks.Models;


import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String title, description, valute, imageUri, userID, postID;
    private User user;
    private ArrayList<String> usersFollowing;
    private Integer price;
    private String searchTitle;

    private Date date;

    public Post(){

    }

    public Post(String imageUri, String title, Integer price, String valute, String description) {
        this.title = title.trim();
        this.description = description;
        this.valute = valute;
        this.imageUri = imageUri;
        this.price = price;
        this.date = new Date(System.currentTimeMillis());
        this.searchTitle = title.toLowerCase();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }


    public ArrayList<String> getUsersFollowing() {
        return usersFollowing;
    }

    public void setUsersFollowing(ArrayList<String> usersFollowing) {
        this.usersFollowing = usersFollowing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
        this.searchTitle = title.toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValute() {
        return valute;
    }

    public void setValute(String valute) {
        this.valute = valute;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle) {
        this.searchTitle = searchTitle;
    }
}
