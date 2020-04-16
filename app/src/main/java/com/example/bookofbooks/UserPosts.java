package com.example.bookofbooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookofbooks.Adapters.FirestoreAdapter;
import com.example.bookofbooks.Adapters.FirestoreAdapterListener;
import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.Interface.UsersPostClickListener;
import com.example.bookofbooks.Interface.UsersPostsInterface;
import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


//TODO za card view title da bude fiksne velicine, skrati teskst Titl...
public class UserPosts extends Fragment implements UsersPostClickListener, UsersPostsInterface {

    private RecyclerView firestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth mAuth;
    private FirestoreAdapterListener adapter;
    private ImageView edit, delete;
    private Query query;
    private PagedList.Config config;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirestoreRecyclerOptions<Post> options;
    ListenerRegistration registration;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_posts_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestoreList = getView().findViewById(R.id.user_posts_recyclerView);
        swipeRefreshLayout = getView().findViewById(R.id.users_posts_swiperefresher);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        //Query
       query = (Query) firebaseFirestore.collection("posts").whereEqualTo("userID", id)
                .orderBy("date", Query.Direction.DESCENDING);
         registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            adapter.notifyDataSetChanged();
                    }
                    // ...
                });

        //RecyclerOption
        options = new FirestoreRecyclerOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Post post = snapshot.toObject(Post.class);
                        post.setPostID(snapshot.getId());
                        return post;
                    }
                })
                .build();

        adapter = new FirestoreAdapterListener(options);
        adapter.setUsersPostsInterface(this);
        adapter.setPostClickListener(this);

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        firestoreList.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(Post post, int position) {
        String id = post.getPostID();
        Log.d("ON ITEM CLICK", "clicked item "+ id);
        Toast.makeText(getActivity().getApplicationContext(), "clicked item", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), PostDetails.class);
        intent.putExtra("postID", id);
        startActivity(intent);
    }

    @Override
    public void editPost(Post post, int position) {
        String id = post.getPostID();
        Intent intent = new Intent(getContext(), CreatePost.class);
        intent.putExtra("postID", id);
        Toast.makeText(getContext(), "Clicked edit "+id, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void deletePost(final Post post, int position) {
        final String postID = post.getPostID();
        new AlertDialog.Builder(getContext())
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete post "+ post.getTitle())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //delete
                        firebaseFirestore.collection("posts").document(postID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("", "DocumentSnapshot successfully deleted!");
                                        firebaseStorage.getReferenceFromUrl(post.getImageUri()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("", "Deleted from storage");
                                                    }
                                                }). addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        // refreshPage();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("", "Error deleting document", e);
                                        Toast.makeText(getContext(), "Error deleting", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });


                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }



}
