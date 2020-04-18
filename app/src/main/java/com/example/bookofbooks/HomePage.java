package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.bookofbooks.Models.User;
import com.example.bookofbooks.Utility.UsersInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomePosts.onFragmentButtonSelected {

    private Button logOutButton;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

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
