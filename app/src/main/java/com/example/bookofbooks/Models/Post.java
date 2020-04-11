package com.example.bookofbooks.Models;


import java.util.Date;

public class Post {
    private String title, description, valute, imageUri, userID;
    private Integer price;
    private Date date;

    public Post(){

    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Post(String imageUri, String title, Integer price, String valute, String description) {
        this.title = title;
        this.description = description;
        this.valute = valute;
        this.imageUri = imageUri;
        this.price = price;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
