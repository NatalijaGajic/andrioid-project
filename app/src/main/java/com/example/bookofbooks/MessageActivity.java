package com.example.bookofbooks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
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
    private String id;
    private String extraPostUserID, extraPostID, extraUsername, extraPostTitle, extraPostImage, extraOtherUserId, extraOtherUsername;
    private ArrayList<Message> messages = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int documentFromCollection = 0;
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
        extraOtherUsername = getIntent().getStringExtra("otherUserName");

        extraOtherUserId = getIntent().getStringExtra("otherUserId");

        username = (TextView) findViewById(R.id.username);
        postTitle = (TextView) findViewById(R.id.post_title);
        if(UsersInfo.getUser().getUsername().equals(getIntent().getStringExtra("username"))){
            username.setText(extraOtherUsername);
        } else {
            username.setText(getIntent().getStringExtra("username"));
        }
        postTitle.setText(getIntent().getStringExtra("postTitle"));
        backImageButton = (ImageButton) findViewById(R.id.back_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_message);

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNewMessagesForUser();
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //add messages from collection at id {documentFromCollection} to messages and notify
                //loadMoreMessages();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMoreMessages() {
        //dodaj na indeks nula
        //  recyclerView.scrollToPosition(messages.size()-documentFromCollection*10);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(extraPostUserID.equals(UsersInfo.getUserID())){
            //user koji je postovao salje poruku(do aktivnosti se doslo iz chat intenta)
            id = extraPostID + extraOtherUserId;
        } else {
            id = extraPostID + UsersInfo.getUserID();
            extraOtherUserId = UsersInfo.getUserID();
            extraOtherUsername = UsersInfo.getUser().getUsername();
        }

        loadMessages();
        clearNewMessagesForUser();

    }

    private void clearNewMessagesForUser() {
        if(extraOtherUserId.equals(UsersInfo.getUserID())){
            //salje otherUser, brisu se newMessages u chats kolekciji
            Toast.makeText(getApplicationContext(), "Brise new u chats", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection("chats").document(extraOtherUserId)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                    if(chatCollection!=null){
                        //index chata u kolekciji
                        int index = chatCollection.getChat(extraPostID);
                        if(index!=-1){
                            int newmess = chatCollection.getChats().get(index).getNewMessages();
                            //oduzimaju se poruke chata, poruke iz chata 0
                            chatCollection.setNewMessages(chatCollection.getNewMessages()-newmess);
                            chatCollection.getChats().get(index).setNewMessages(0);
                            firebaseFirestore.collection("chats").document(extraOtherUserId)
                                    .set(chatCollection);
                        }
                    }
                }
            });
        }else {
            //salje postUser, brisu se newMessages u postChats kolekciji
            Toast.makeText(getApplicationContext(), "Brise new u postchats", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection("postChats").document(UsersInfo.getUserID())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                    if(chatCollection!=null){
                        int index = chatCollection.getChat(extraPostID);
                        if(index!=-1){
                            int newmess = chatCollection.getChats().get(index).getNewMessages();
                            chatCollection.setNewMessages(chatCollection.getNewMessages()-newmess);
                            chatCollection.getChats().get(index).setNewMessages(0);
                            firebaseFirestore.collection("postChats").document(UsersInfo.getUserID())
                                    .set(chatCollection);
                        }
                    }
                }
            });
        }
    }

    private void loadMessages(){
        DocumentReference documentReference = firebaseFirestore.collection("MessagesSplitCollection").document(id);
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
                   // messages.removeAll(messages);
                    messages.clear();
                    messages.addAll(document.getMessages());
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size()-1);
                } else {
                    Log.d("SNAPSHOT LISTENER", source + " data: null");
                }
            }
        });
    }

    private void sendMessage() {
        String text = editText.getText().toString();
        if(text.isEmpty()){
            //ne salje se poruka
        } else {
            final Message message;
            //proveravamo od koga je, kome poruka
            if(UsersInfo.getUserID().equals(extraPostUserID)){
                message = new Message(UsersInfo.getUserID(), extraOtherUserId, text.trim());
            } else {
                message = new Message(UsersInfo.getUserID(), extraPostUserID, text.trim());
            }
            insertChatToDB(message);

            //send message
           firebaseFirestore.collection("MessagesSplitCollection").document(id)
                   .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   MessagesSplitCollection document = documentSnapshot.toObject(MessagesSplitCollection.class);
                    if(document!=null){
                        document.add(message);
                        firebaseFirestore.collection("MessagesSplitCollection").document(id)
                                .set(document);
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

        final Chat chat = new Chat(extraPostUserID, extraOtherUserId,
                extraPostID, extraPostTitle, extraUsername, extraOtherUsername, extraPostImage, message);
        //chat usera ciji je post u postChats
        firebaseFirestore.collection("postChats").document(extraPostUserID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                if(chatCollection!=null){
                    //vec postoje chatovi
                    if(!UsersInfo.getUserID().equals(extraPostUserID)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        chatCollection.add(chat, true);
                        chatCollection.setNewMessages(chatCollection.getNewMessages()+1);
                    } else {
                        //salje poruku isti user, poruke procitane, oduzeti newMessages
                        int oldMess = chatCollection.add(chat, false);
                        chatCollection.setNewMessages(chatCollection.getNewMessages()-oldMess);
                    }
                    firebaseFirestore.collection("postChats").document(extraPostUserID)
                            .set(chatCollection);
                } else {
                    //prvi chat koji se pravi
                    if(!UsersInfo.getUserID().equals(extraPostUserID)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        chat.setNewMessages(1);
                    }
                    ArrayList<Chat> array = new ArrayList();
                    array.add(chat);
                    ChatCollection input = new ChatCollection(array);
                    if(!UsersInfo.getUserID().equals(extraPostUserID)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        input.setNewMessages(1);
                    }
                    firebaseFirestore.collection("postChats").document(extraPostUserID)
                            .set(input);
                }
            }
        });

        //chat drugog usera
        firebaseFirestore.collection("chats").document(extraOtherUserId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                if(chatCollection!=null){
                    if(!UsersInfo.getUserID().equals(extraOtherUserId)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        chatCollection.add(chat, true);
                        chatCollection.setNewMessages(chatCollection.getNewMessages()+1);
                    } else {
                        //salje poruku isti user, poruke procitane, oduzeti newMessages
                        int oldMess = chatCollection.add(chat, false);
                        chatCollection.setNewMessages(chatCollection.getNewMessages()-oldMess);
                    }
                    firebaseFirestore.collection("chats").document(extraOtherUserId)
                            .set(chatCollection);
                } else {
                    if(!UsersInfo.getUserID().equals(extraOtherUserId)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        chat.setNewMessages(1);
                    }
                    ArrayList<Chat> array = new ArrayList();
                    array.add(chat);
                    ChatCollection input = new ChatCollection(array);
                    if(!UsersInfo.getUserID().equals(extraOtherUserId)){
                        //salje poruku drugi user, poruke neprocitane, dodati newMessages
                        input.setNewMessages(1);
                    }
                    firebaseFirestore.collection("chats").document(extraOtherUserId)
                            .set(input);
                }
            }
        });

    }

}
