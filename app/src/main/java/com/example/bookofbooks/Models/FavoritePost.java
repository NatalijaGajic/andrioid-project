package com.example.bookofbooks.Models;

public class FavoritePost {
    String userID, postID;
    Post post;

    public FavoritePost(){

    }

    public FavoritePost(String userID, String postID, Post post) {
        this.userID = userID;
        this.postID = postID;
        this.post = post;
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
}
