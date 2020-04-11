package com.example.bookofbooks.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.PostClickListener;
import com.example.bookofbooks.R;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title, date, country, price;
    public ImageView image;
    public PostClickListener listener;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        image = (ImageView)itemView.findViewById(R.id.card_imageView);
        title = (TextView) itemView.findViewById(R.id.card_textViewTitle);
        date = (TextView) itemView.findViewById(R.id.card_textViewDate);
        country = (TextView) itemView.findViewById(R.id.textViewCountry);
        price = (TextView) itemView.findViewById(R.id.textViewPrice);


    }

    public void setItemClickListener(PostClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
