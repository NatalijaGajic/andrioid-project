package com.example.bookofbooks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookofbooks.Adapters.MessageAdapter;
import com.example.bookofbooks.Models.Chat;
import com.example.bookofbooks.Models.ChatCollection;
import com.example.bookofbooks.Models.Message;
import com.example.bookofbooks.Models.MessagesSplitCollection;
import com.example.bookofbooks.Utility.UsersInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendMessage, backImageButton;
    private Toolbar toolbar;
    private TextView username, postTitle;
    private FirebaseFirestore firebaseFirestore;
    private String extraPostUserID, extraPostID, extraUsername, extraPostTitle, extraPostImage;
    private ArrayList<Message> messages = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.message_recycler_view);
        editText = findViewById(R.id.message_edit_text);
        sendMessage = findViewById(R.id.send_image_button);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chat_toolbar_design, null);
        actionBar.setCustomView(actionBarView);
        extraPostUserID = getIntent().getStringExtra("postUserId");
        extraPostID = getIntent().getStringExtra("postID");
        extraUsername = getIntent().getStringExtra("username");
        extraPostTitle = getIntent().getStringExtra("postTitle");
        extraPostImage = getIntent().getStringExtra("imageUri");


        username = (TextView) findViewById(R.id.username);
        postTitle = (TextView) findViewById(R.id.post_title);
        username.setText(getIntent().getStringExtra("username"));
        postTitle.setText(getIntent().getStringExtra("postTitle"));
        backImageButton = (ImageButton) findViewById(R.id.back_button);

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        messageAdapter = new MessageAdapter(messages);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String id = extraPostID+UsersInfo.getUserID();


        DocumentReference documentReference = firebaseFirestore.collection("MessagesSplitCollection").document(id);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                MessagesSplitCollection document = documentSnapshot.toObject(MessagesSplitCollection.class);
                if(document==null){

                } else {
                    Toast.makeText(getApplicationContext(), "Messages asigned", Toast.LENGTH_SHORT).show();
                    messages.addAll(document.getMessages());
                    messageAdapter.notifyDataSetChanged();
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
                    MessagesSplitCollection document = snapshot.toObject(MessagesSplitCollection.class);
                    messages.removeAll(messages);
                    messages.addAll(document.getMessages());
                    messageAdapter.notifyDataSetChanged();
                } else {
                    Log.d("SNAPSHOT LISTENER", source + " data: null");
                }
            }
        });
    }

    private void sendMessage() {
        String text = editText.getText().toString();
        if(text.isEmpty()){
            //not sending
        } else {
            final Message message = new Message(UsersInfo.getUserID(), text.trim());
            insertChatToDB(message);

            //send message

            final String id = extraPostID+UsersInfo.getUserID();
           firebaseFirestore.collection("MessagesSplitCollection").document(id)
                   .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   MessagesSplitCollection document = documentSnapshot.toObject(MessagesSplitCollection.class);
                    if(document!=null){
                        document.add(message);
                        firebaseFirestore.collection("MessagesSplitCollection").document(id)
                                .set(document);
                        //dodaj na kraj niza
                    }else {
                        ArrayList<Message> array = new ArrayList<>();
                        array.add(message);
                        MessagesSplitCollection input = new MessagesSplitCollection(array, extraPostUserID, UsersInfo.getUserID());
                        firebaseFirestore.collection("MessagesSplitCollection").document(id)
                                .set(input);
                    }
               }
           });
           editText.setText("");
        }
    }

    private void insertChatToDB(Message message) {
        final Chat chat = new Chat(extraPostUserID, UsersInfo.getUserID(),
                extraPostID, extraPostTitle, extraUsername, UsersInfo.getUser().getUsername(), extraPostImage, message);
        //make a chat, treba za oba usera
        firebaseFirestore.collection("postChats").document(extraPostUserID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                if(chatCollection!=null){
                    chatCollection.add(chat);
                    firebaseFirestore.collection("postChats").document(extraPostUserID)
                            .set(chatCollection);
                } else {
                    ArrayList<Chat> array = new ArrayList();
                    array.add(chat);
                    ChatCollection input = new ChatCollection(array);
                    firebaseFirestore.collection("postChats").document(extraPostUserID)
                            .set(input);
                }
            }
        });

        firebaseFirestore.collection("chats").document(UsersInfo.getUserID())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                if(chatCollection!=null){
                    chatCollection.add(chat);
                    firebaseFirestore.collection("chats").document(UsersInfo.getUserID())
                            .set(chatCollection);
                } else {
                    ArrayList<Chat> array = new ArrayList();
                    array.add(chat);
                    ChatCollection input = new ChatCollection(array);
                    firebaseFirestore.collection("chats").document(UsersInfo.getUserID())
                            .set(input);
                }
            }
        });

    }
}
