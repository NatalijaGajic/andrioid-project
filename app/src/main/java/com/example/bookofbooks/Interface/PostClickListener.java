package com.example.bookofbooks.Interface;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;

public interface PostClickListener {

    void onItemClick(DocumentSnapshot documentSnapshot, int position);
}
