package com.example.bookofbooks.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.Interface.UsersPostClickListener;
import com.example.bookofbooks.Interface.UsersPostsInterface;
import com.example.bookofbooks.Interface.WishlistPostClickListener;
import com.example.bookofbooks.Models.FavoritePost;
import com.example.bookofbooks.R;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class WishlistAdapter extends FirestoreRecyclerAdapter<FavoritePost, WishlistAdapter.FavoriteHoldView> {

    private WishlistPostClickListener wishlistPostClickListener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public WishlistAdapter(@NonNull FirestoreRecyclerOptions<FavoritePost> options) {
        super(options);
    }

    public WishlistAdapter(@NonNull FirestoreRecyclerOptions<FavoritePost> options, WishlistPostClickListener wishlistPostClickListener) {
        super(options);
        this.wishlistPostClickListener = wishlistPostClickListener;
    }

    @NonNull
    @Override
    public FavoriteHoldView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_book, parent, false);
        return new WishlistAdapter.FavoriteHoldView(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FavoriteHoldView holder, final int position, @NonNull FavoritePost model) {
        holder.title.setText(model.getPost().getTitle());
        holder.country.setText(model.getPost().getUser().getCountry());
        String date = TimestampConverter.timestampToDate(model.getPost().getDate().toString());
        //holder.date.setText(model.getDate().toString());
        holder.date.setText(date);
        holder.price.setText(model.getPost().getPrice()+" "+model.getPost().getValute());
        Picasso.get().load(model.getPost().getImageUri()).fit().centerCrop().into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlistPostClickListener.onItemClick(getItem(position), position);
            }
        });

    }


    protected class FavoriteHoldView extends RecyclerView.ViewHolder {
        public TextView title, date, country, price;
        public ImageView image, delete, edit;

        public FavoriteHoldView(@NonNull View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.card_imageView);
            title = (TextView) itemView.findViewById(R.id.card_textViewTitle);
            date = (TextView) itemView.findViewById(R.id.card_textViewDate);
            country = (TextView) itemView.findViewById(R.id.textViewCountry);
            price = (TextView) itemView.findViewById(R.id.textViewPrice);

        }
    }
}
