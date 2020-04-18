package com.example.bookofbooks.Models;

import java.util.ArrayList;
import java.util.Date;

public class UserPostChat {
    private ArrayList<String> users;
    private Message recentText;
    private String postID;
    private String postTitle;
    private String postUserName;
    private String otherUserName;
    private String imageUri;
    private Date timestamp;

    public UserPostChat() {}

    public UserPostChat(ArrayList<String> users, String postID, String postTitle, String postUserName, String otherUserName, String imageUri, Message recentText) {
        this.users = users;
        this.postID = postID;
        this.postTitle = postTitle;
        this.postUserName = postUserName;
        this.otherUserName = otherUserName;
        this.imageUri = imageUri;
        this.recentText = recentText;
        this.timestamp = new Date(System.currentTimeMillis());
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
