package com.example.bookofbooks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.util.Date;

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
                //holder je view, layout, setuju se vrednosti za TextView-ove
                holder.title.setText(model.getTitle());
                holder.country.setText("serbia");
                String[] dateParts = model.getDate().toString().split(" ");
                StringBuilder sb = new StringBuilder();
                for(int i=1; i<dateParts.length;i++){

                    if(i==4 && i==5 && i==dateParts.length-1){
                        Log.d("",dateParts[i].toString());
                        continue;
                    }
                    if(i==3) {
                        String[] timeParts = dateParts[i].split(":");
                        sb.append(timeParts[0]+":");
                        sb.append(timeParts[1]);
                    } else{
                        sb.append(dateParts[i]);
                    }
                    sb.append(" ");

                }
                sb.delete(12,22);
                String year = sb.substring(12,17);
                sb.delete(12,29);
                String[] strings = sb.toString().split(" ");
                sb.delete(0,sb.capacity());
                sb.append(strings[0]+" ");
                sb.append(strings[1]+" ");
                sb.append(year+" ");
                sb.append(strings[2]);
                //holder.date.setText(model.getDate().toString());
                holder.date.setText(sb.toString());
                holder.price.setText(model.getPrice()+"");
                Picasso.get().load(model.getImageUri()).fit().centerCrop().into(holder.image);
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
