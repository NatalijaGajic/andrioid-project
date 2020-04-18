package com.example.bookofbooks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bookofbooks.Adapters.ChatAdapter;
import com.example.bookofbooks.Models.Chat;
import com.example.bookofbooks.Models.ChatCollection;
import com.example.bookofbooks.Utility.UsersInfo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatAdapter chatAdapter;
    private String mode, chatCollectionName;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        recyclerView = findViewById(R.id.chat_recycler_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mode = getIntent().getStringExtra("mode");

        chatAdapter = new ChatAdapter(chats);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        //swipe refresh
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mode.equals("my chats")){
            chatCollectionName = "chats";
            Toast.makeText(getApplicationContext(), "Chats collection", Toast.LENGTH_SHORT).show();
        } else {
            chatCollectionName = "postChats";
        }
        DocumentReference documentReference = firebaseFirestore.collection(chatCollectionName)
                .document(UsersInfo.getUserID());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection document = documentSnapshot.toObject(ChatCollection.class);
                if(document==null){
                    Toast.makeText(getApplicationContext(), "Collection empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Chats asigned", Toast.LENGTH_SHORT).show();
                    chats.addAll(document.getChats());
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("SNAPSHOT LISTENER", "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d("SNAPSHOT LISTENER", source + " data: " + snapshot.getData());
                    ChatCollection document = snapshot.toObject(ChatCollection.class);
                    chats.removeAll(chats);
                    chats.addAll(document.getChats());
                    chatAdapter.notifyDataSetChanged();
                } else {
                    Log.d("SNAPSHOT LISTENER", source + " data: null");
                }
            }
        });
    }
}
