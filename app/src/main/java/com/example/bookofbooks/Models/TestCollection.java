package com.example.bookofbooks.Models;

import java.util.HashMap;

public class TestCollection {
    HashMap<String, Post> postovi;

    public TestCollection(){

    }

    public TestCollection(String postID, Post post){
        postovi = new HashMap<>();
        postovi.put(postID, post);
    }

    public void addToArrayOfFavoritedPosts(String postID, Post post){
        postovi.put(postID, post);
    }

    public void setFavoritedPostsArray(){}


    public HashMap<String, Post> getPostovi() {
        return postovi;
    }

        public void setPostovi(HashMap<String, Post> postovi) {
            this.postovi = postovi;
        }

}
