package com.example.bookofbooks.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.NotificationClickListener;
import com.example.bookofbooks.Models.Notification;
import com.example.bookofbooks.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.squareup.picasso.Picasso;

public class NotificationsAdapter extends FirestorePagingAdapter<Notification, NotificationsAdapter.NotificationHolder>{

   private NotificationClickListener notificationClickListener;
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public NotificationsAdapter(@NonNull FirestorePagingOptions options, NotificationClickListener notificationClickListener) {
        super(options);
        this.notificationClickListener = notificationClickListener;
    }


    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull final Notification model) {
        holder.postTitle.setText(model.getTitle());
        Integer count = model.getUsersFollowing();
        String string = model.getUsername()+" and "+ count.toString()+ " others are following";
        holder.userName.setText(string);
        holder.message.setText("New user is following this post");
        holder.dateText.setText(model.getDate());
        Picasso.get().load(model.getImageUri()).fit().centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationClickListener.onItemClick(model);
            }
        });
    }



    public class NotificationHolder extends RecyclerView.ViewHolder{

        public TextView postTitle, userName, dateText, message;
        public ImageView imageView;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.notification_post_title);
            userName = itemView.findViewById(R.id.notification_username);
            dateText = itemView.findViewById(R.id.notification_date);
            message = itemView.findViewById(R.id.notification_message);
            imageView = itemView.findViewById(R.id.notification_image);
        }
    }
}
