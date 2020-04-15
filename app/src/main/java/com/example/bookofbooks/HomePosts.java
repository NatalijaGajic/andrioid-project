package com.example.bookofbooks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookofbooks.Adapters.FirestoreAdapter;
import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.Models.Post;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomePosts extends Fragment implements PostClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView firestoreList;
    private SearchView searchView;
    private onFragmentButtonSelected listener;

    FirebaseFirestore firebaseFirestore;
    FirestoreAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_posts_fragment, container, false);
        /*Button fragmentButton = view.findViewById(R.id.fragment_button);
        fragmentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onButtonSelected();
            }
        });*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestoreList = getView().findViewById(R.id.home_posts_recyclerView);
        swipeRefreshLayout = getView().findViewById(R.id.home_posts_swipe_refresh);
        searchView = getView().findViewById(R.id.search_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        //Query
        Query query = firebaseFirestore.collection("posts").orderBy("date", Query.Direction.DESCENDING);
        //RecyclerOption
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(1)
                .build();
        FirestorePagingOptions<Post> options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Post post = snapshot.toObject(Post.class);
                        post.setPostID(snapshot.getId());
                        return post;
                    }
                })
                .build();

        adapter = new FirestoreAdapter(options, this, R.layout.card_layout_book);

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        firestoreList.setAdapter(adapter);

       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.stopListening();
                Query query = firebaseFirestore.collection("posts")
                       .orderBy("date", Query.Direction.DESCENDING);
                //RecyclerOption
                PagedList.Config config = new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(1)
                        .build();
                FirestorePagingOptions<Post> options = new FirestorePagingOptions.Builder<Post>()
                        .setLifecycleOwner(HomePosts.this)
                        .setQuery(query, config, new SnapshotParser<Post>() {
                            @NonNull
                            @Override
                            public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                Post post = snapshot.toObject(Post.class);
                                post.setPostID(snapshot.getId());
                                return post;
                            }
                        })
                        .build();

                adapter = new FirestoreAdapter(options, HomePosts.this, R.layout.card_layout_book);

                firestoreList.setHasFixedSize(true);
                firestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
                firestoreList.setAdapter(adapter);
                adapter.startListening();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

       searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               Toast.makeText(getContext(),"text submitted", Toast.LENGTH_SHORT ).show();
               searchPosts(query);
               return true;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               return false;
           }
       });

    }

    private void searchPosts(String search) {
       // adapter.stopListening();
        Toast.makeText(getContext(),search, Toast.LENGTH_SHORT).show();

        adapter.stopListening();
        Query query = firebaseFirestore.collection("posts")
                .whereEqualTo("searchTitle",search.trim().toLowerCase());
        //RecyclerOption
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(1)
                .build();
        FirestorePagingOptions<Post> options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(HomePosts.this)
                .setQuery(query, config, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Post post = snapshot.toObject(Post.class);
                        post.setPostID(snapshot.getId());
                        return post;
                    }
                })
                .build();

        adapter = new FirestoreAdapter(options, HomePosts.this, R.layout.card_layout_book);

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        firestoreList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentButtonSelected) {
            listener = (onFragmentButtonSelected) context;
        }
    }

   /* @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
        String id = documentSnapshot.getId();
        Log.d("ON ITEM CLICK", "clicked item "+ id);
        Toast.makeText(getActivity().getApplicationContext(), "clicked item", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), PostDetails.class);
        intent.putExtra("postID", id);
        startActivity(intent);

        //radimo nesto na klik
    }

    public interface onFragmentButtonSelected {
        public void onButtonSelected();
    }
}
