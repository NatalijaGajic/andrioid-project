package com.example.bookofbooks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Adapters.FirestoreAdapter;
import com.example.bookofbooks.Adapters.WishlistAdapter;
import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.Interface.WishlistPostClickListener;
import com.example.bookofbooks.Models.FavoritePost;
import com.example.bookofbooks.Models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class WishlistPosts extends Fragment implements WishlistPostClickListener {

    RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private Query query;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wishlist_posts_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.wishlist_posts_recylerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        //Query
        Query query = firebaseFirestore.collection("wishlist")
                .whereEqualTo("userID", userID);
                //.orderBy("date", Query.Direction.DESCENDING);
       FirestoreRecyclerOptions<FavoritePost> options;
        options = new FirestoreRecyclerOptions.Builder<FavoritePost>()
                 .setLifecycleOwner(this)
                 .setQuery(query, new SnapshotParser<FavoritePost>() {
                     @NonNull
                     @Override
                     public FavoritePost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                         FavoritePost post = snapshot.toObject(FavoritePost.class);
                         post.setFavoritePostID(snapshot.getId());
                         return post;
                     }
                 })
                 .build();

        adapter = new WishlistAdapter(options, WishlistPosts.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);



    }


    @Override
    public void onItemClick(FavoritePost post, int position) {
        String id = post.getPostID();
        Log.d("ON ITEM CLICK", "clicked item "+ id);
        Intent intent = new Intent(getContext(), PostDetails.class);
        intent.putExtra("postID", id);
        startActivity(intent);
    }
}
