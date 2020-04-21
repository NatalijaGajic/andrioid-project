package com.example.bookofbooks.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookofbooks.Interface.ChatClickListener;
import com.example.bookofbooks.Models.Chat;
import com.example.bookofbooks.R;
import com.example.bookofbooks.Utility.UsersInfo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ChatClickListener chatClickListener;
    private ArrayList<Chat> chats;
    public ChatAdapter(ArrayList<Chat> chats, ChatClickListener chatClickListener){
        this.chatClickListener = chatClickListener;
        this.chats = chats;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_chat_holder, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, final int position) {
        final Chat chat = chats.get(position);
        Log.d("ChatAdapter", chat.toString());
        if(chat.getOtherUserName().equals(UsersInfo.getUser().getUsername())){
            holder.userName.setText(chat.getPostUserName());
        } else {
            holder.userName.setText(chat.getOtherUserName());
        }
        holder.message.setText(chat.getRecentText().getMessage());
        holder.dateText.setText(chat.getDate());
        holder.postTitle.setText(chat.getPostTitle());
        Picasso.get().load(chat.getImageUri()).fit().centerCrop().into(holder.imageView);
        if(chat.getNewMessages()!=0){
            holder.newMessages.setText(chat.getNewMessages().toString());
            holder.newMessages.setVisibility(View.VISIBLE);
        }else {
            holder.newMessages.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatClickListener.onItemClick(chats.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView postTitle, userName, dateText, message, newMessages;
        public ImageView imageView;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.chat_post_title);
            userName = itemView.findViewById(R.id.chat_username);
            dateText = itemView.findViewById(R.id.chat_date);
            message = itemView.findViewById(R.id.chat_message);
            imageView = itemView.findViewById(R.id.chat_image);
            newMessages = itemView.findViewById(R.id.new_messages_text);
        }
    }
}
