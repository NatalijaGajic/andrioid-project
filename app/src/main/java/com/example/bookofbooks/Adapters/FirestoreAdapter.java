package com.example.bookofbooks.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.Interface.UsersPostsInterface;
import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.R;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class FirestoreAdapter extends FirestorePagingAdapter<Post, FirestoreAdapter.PostViewHolder> {

    private PostClickListener postClickListener;
    private UsersPostsInterface usersPostsInterface;


    private int card_layout;
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     * @param card_layout
     */
    public FirestoreAdapter(@NonNull FirestorePagingOptions<Post> options, PostClickListener postClickListener, int card_layout) {
        super(options);
        this.postClickListener = postClickListener;
        this.card_layout = card_layout;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(card_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, final int position, @NonNull final Post model) {
        //holder je view, layout, setuju se vrednosti za TextView-ove
        holder.title.setText(model.getTitle());
        holder.country.setText(model.getUser().getCountry());
        String date = TimestampConverter.timestampToDate(model.getDate().toString());
        //holder.date.setText(model.getDate().toString());
        holder.date.setText(date);
        holder.price.setText(model.getPrice()+" "+model.getValute());
        Picasso.get().load(model.getImageUri()).fit().centerCrop().into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postClickListener.onItemClick(getItem(position), position);
            }
        });

        if(card_layout == R.layout.card_layout_book_users){
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersPostsInterface.editPost(model, position);
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersPostsInterface.deletePost(model, position);
                }
            });
        }

    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_MORE:
                //TODO dodaj loader
                break;
            case LOADED:
                //removuj loader
                break;
        }

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date, country, price;
        public ImageView image, delete, edit;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.card_imageView);
            title = (TextView) itemView.findViewById(R.id.card_textViewTitle);
            date = (TextView) itemView.findViewById(R.id.card_textViewDate);
            country = (TextView) itemView.findViewById(R.id.textViewCountry);
            price = (TextView) itemView.findViewById(R.id.textViewPrice);

            if(card_layout == R.layout.card_layout_book_users){
                edit = itemView.findViewById(R.id.edit_button);
                delete = itemView.findViewById(R.id.delete_button);

            }
        }
    }

    public UsersPostsInterface getUsersPostsInterface() {
        return usersPostsInterface;
    }

    public void setUsersPostsInterface(UsersPostsInterface usersPostsInterface) {
        this.usersPostsInterface = usersPostsInterface;
    }

}
