package com.example.bookofbooks.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.UsersPostClickListener;
import com.example.bookofbooks.Interface.UsersPostsInterface;
import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.R;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class FirestoreAdapterListener extends FirestoreRecyclerAdapter<Post, FirestoreAdapterListener.PostViewHolder> {

    private UsersPostsInterface usersPostsInterface;
    private UsersPostClickListener postClickListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreAdapterListener(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_book_users, parent, false);
        return new PostViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, final int position, @NonNull final Post model) {
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

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersPostsInterface.editPost(getItem(position), position);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersPostsInterface.deletePost(getItem(position), position);
            }
        });

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date, country, price;
        public ImageView image;
        public ImageView delete, edit;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.card_imageView);
            title = (TextView) itemView.findViewById(R.id.card_textViewTitle);
            date = (TextView) itemView.findViewById(R.id.card_textViewDate);
            country = (TextView) itemView.findViewById(R.id.textViewCountry);
            price = (TextView) itemView.findViewById(R.id.textViewPrice);
            edit = itemView.findViewById(R.id.edit_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }

    public void setUsersPostsInterface(UsersPostsInterface usersPostsInterface) {
        this.usersPostsInterface = usersPostsInterface;
    }

    public void setPostClickListener(UsersPostClickListener postClickListener) {
        this.postClickListener = postClickListener;
    }
}
