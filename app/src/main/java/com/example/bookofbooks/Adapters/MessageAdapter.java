package com.example.bookofbooks.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.bookofbooks.Models.Message;
import com.example.bookofbooks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Message> messageList;
    private FirebaseAuth firebaseAuth;


    public MessageAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_message_layout, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String senderID = firebaseAuth.getCurrentUser().getUid();
        Message message = messageList.get(position);
        String from = message.getFrom();

        if(from.equals(senderID)){
            holder.sendingMessage.setText(message.getMessage());
            holder.sendingMessage.setVisibility(View.VISIBLE);
        } else {
            holder.recievedMessage.setText(message.getMessage());
            holder.recievedMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return messageList.size();
    }

    public class MessageViewHolder extends ViewHolder {

        public TextView sendingMessage, recievedMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendingMessage = itemView.findViewById(R.id.sendingMessage);
            recievedMessage = itemView.findViewById(R.id.recievedMessage);
        }
    }
}
