package com.example.bookofbooks.Models;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCollection2 {
    ArrayList<Post> postovi;

    public TestCollection2(){

    }

    public TestCollection2(Post post){
        postovi = new ArrayList<Post>();
        postovi.add(post);
    }

    public void addToArrayOfFavoritedPosts(Post post){
        postovi.add(post);
    }

    public void setFavoritedPostsArray(){}

    public ArrayList<Post> getPostovi() {
        return postovi;
    }

    public void setPostovi(ArrayList<Post> postovi) {
        this.postovi = postovi;
    }
}
