package com.example.bookofbooks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.ViewHolder.PostViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePosts extends Fragment {

    private RecyclerView firestoreList;
    private onFragmentButtonSelected listener;


    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        //Query
        Query query = firebaseFirestore.collection("posts").orderBy("date", Query.Direction.DESCENDING);
        //RecyclerOption
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_book, parent, false);
                return new PostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                holder.title.setText(model.getTitle());
                holder.country.setText("Serbia");
                holder.description.setText(model.getDescription());
                holder.price.setText(model.getPrice()+"");
            }
        };

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        firestoreList.setAdapter(adapter);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentButtonSelected) {
            listener = (onFragmentButtonSelected) context;
        }



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

    public interface onFragmentButtonSelected {
        public void onButtonSelected();
    }
}
