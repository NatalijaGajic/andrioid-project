package com.example.bookofbooks.Models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Chat {
    private ArrayList<String> users = new ArrayList<>();
    private Message recentText;
    private String postID;
    private String postTitle;
    private String postUserName, postUserID;
    private String otherUserName, otherUserID;
    private String imageUri;
    private String date;

    public Chat() {}

    public Chat(String postUserID, String otherUserID, String postID, String postTitle, String postUserName, String otherUserName, String imageUri, Message recentText) {
        this.otherUserID = otherUserID;
        this.postUserID = postUserID;
        this.users.add(postUserID);
        this.users.add(otherUserID);
        this.postID = postID;
        this.postTitle = postTitle;
        this.postUserName = postUserName;
        this.otherUserName = otherUserName;
        this.imageUri = imageUri;
        this.recentText = recentText;
        Calendar dateToCall = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        this.date = currentDateFormat.format(dateToCall.getTime());
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public Message getRecentText() {
        return recentText;
    }

    public void setRecentText(Message recentText) {
        this.recentText = recentText;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPostUserID() {
        return postUserID;
    }

    public void setPostUserID(String postUserID) {
        this.postUserID = postUserID;
    }

    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return postTitle +" " +otherUserName+ " " + postUserName;
    }
}
