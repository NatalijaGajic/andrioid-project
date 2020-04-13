package com.example.bookofbooks.Interface;

import com.example.bookofbooks.Models.Post;
import com.google.firebase.firestore.DocumentSnapshot;

public interface UsersPostsInterface {
    void editPost(Post post, int position);
    void deletePost(Post post, int position);
}
