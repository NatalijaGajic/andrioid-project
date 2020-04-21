package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookofbooks.Models.Chat;
import com.example.bookofbooks.Models.ChatCollection;
import com.example.bookofbooks.Models.Infos;
import com.example.bookofbooks.Models.Notification;
import com.example.bookofbooks.Models.User;
import com.example.bookofbooks.Utility.UsersInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomePosts.onFragmentButtonSelected {

    private Button logOutButton;
    private ImageView notificationsImageView;
    private TextView newNotifications;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        notificationsImageView = (ImageView) findViewById(R.id.notification_bell);
        newNotifications = (TextView) findViewById(R.id.notification_text_view);

        notificationsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked notifications", Toast.LENGTH_SHORT).show();
                showNotifications();
            }
        });
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerStateChanged(int newState) {
                if((newState == DrawerLayout.STATE_SETTLING || newState == DrawerLayout.STATE_DRAGGING) && drawerLayout.isDrawerOpen(GravityCompat.START)==false){
                    Toast.makeText(getApplicationContext(), "Opening", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                    onPrepareOptionsMenu(null);
                }
                super.onDrawerStateChanged(newState);
            }
        };


       drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();



        navigationView.setNavigationItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(UsersInfo.getUserInfo() == null) {
            final String id = mAuth.getUid();
            FirebaseFirestore.getInstance().collection("users").document(id)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    UsersInfo.setUsersInfo(user, id);
                }
            });
        } else if (UsersInfo.getUserID() != mAuth.getCurrentUser().getUid()){
            final String id = mAuth.getUid();
            FirebaseFirestore.getInstance().collection("users").document(id)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    UsersInfo.setUsersInfo(user, id);
                }
            });
        }

        //default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new HomePosts());
        fragmentTransaction.commit();

        setListenerForNotifications();


    }

    private void setListenerForNotifications() {
        String id = mAuth.getCurrentUser().getUid();
        //get notifications that are not seen
        firebaseFirestore.collection("users").document(id).collection("infos")
                .document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Infos info = documentSnapshot.toObject(Infos.class);
                    if(info!=null){
                        Integer i = info.getUnseenCount();
                        newNotifications.setText(i.toString());
                        newNotifications.setVisibility(View.VISIBLE);
                    }else {
                        newNotifications.setText("0");
                        newNotifications.setVisibility(View.GONE);
                    }
            }
        });


        DocumentReference documentReference = firebaseFirestore.collection("users").document(id).collection("infos")
                .document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("SNAPSHOT LISTENER", "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d("SNAPSHOT LISTENER", source + " data: " + snapshot.getData());
                    Infos document = snapshot.toObject(Infos.class);
                    Integer i = document.getUnseenCount();
                    if(i!=0){
                        newNotifications.setText(i.toString());
                        newNotifications.setVisibility(View.VISIBLE);
                    }else {
                        newNotifications.setText("0");
                        newNotifications.setVisibility(View.GONE);
                    }

                } else {
                    Log.d("SNAPSHOT LISTENER", source + " data: null");
                }
            }
        });
    }

    private void showNotifications() {
        newNotifications.setText("0");
        newNotifications.setVisibility(View.GONE);
        //TODO sve notifikacije treba da se postave da su seen u notifications activityju
        Infos info = new Infos(0);
        firebaseFirestore.collection("users").document(UsersInfo.getUserID())
                .collection("infos").document(UsersInfo.getUserID()).set(info);
       /* fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new UserPosts());
        fragmentTransaction.commit();*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Menu menuNav = navigationView.getMenu();
        final MenuItem myChats = menuNav.findItem(R.id.my_chats);
        final MenuItem myPostChats = menuNav.findItem(R.id.my_posts_chats);
        String id = mAuth.getUid();
        firebaseFirestore.collection("chats").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                        if(chatCollection!=null){
                            Integer messages = (Integer) chatCollection.getNewMessages();
                            if(messages!=0){
                                myChats.setTitle("Other chats "+"("+messages.toString()+")");
                            }else if(messages == 0){
                                myChats.setTitle("Other chats");
                            }
                        }
                    }
                });
        firebaseFirestore.collection("postChats").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ChatCollection chatCollection = documentSnapshot.toObject(ChatCollection.class);
                if(chatCollection!=null){
                    Integer messages = (Integer) chatCollection.getNewMessages();
                    if(messages!=0){
                        myPostChats.setTitle("My posts chats "+"("+messages.toString()+")");
                    }else if(messages == 0){
                        myPostChats.setTitle("My posts chats");

                    }
                }
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case R.id.home: {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new HomePosts());
                fragmentTransaction.commit();
                break;
            }
            case R.id.log_out:{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            }
            case R.id.my_post:{
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new UserPosts());
                fragmentTransaction.commit();
                break;
            }
            case R.id.make_post: {
                startActivity(new Intent(getApplicationContext(), CreatePost.class));
                break;
            }
            case R.id.my_wishlist: {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new WishlistPosts());
                fragmentTransaction.commit();
                break;
            }
            case R.id.user_settings: {
                startActivity(new Intent(getApplicationContext(), UserSettings.class));
                break;
            }
            case R.id.my_chats: {
                Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                intent.putExtra("mode", "my chats");
                startActivity(intent);
                break;
            }
            case R.id.my_posts_chats: {
                Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                intent.putExtra("mode", "my posts chats");
                startActivity(intent);
                break;
            }
        }
        return false;
    }

    @Override
    public void onButtonSelected() {
        //do something when button in fragment is cllicked
    }
}
