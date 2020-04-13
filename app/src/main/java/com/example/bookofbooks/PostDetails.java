package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.Models.TestCollection;
import com.example.bookofbooks.Models.TestCollection2;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PostDetails extends AppCompatActivity {

    private TextView title, description, username, date, country;
    private Button price;
    private ImageView image;
    private String postID;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private static boolean ADDED_TO_WISHLIST=false;
    private Post displayedPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        title = (TextView) findViewById(R.id.details_titleOfBookTextView);
        description = (TextView) findViewById(R.id.details_descriptionTextView);
        username = (TextView) findViewById(R.id.details_userNameTextView);
        date = (TextView) findViewById(R.id.details_dateTextView);
        country = (TextView) findViewById(R.id.details_countryTextView);
        price = (Button) findViewById(R.id.details_priceButton);
        image = (ImageView) findViewById(R.id.postDetailsImageView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.details_floatingActionButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
        postID = getIntent().getStringExtra("postID");

        getPostDetails(postID);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ADDED_TO_WISHLIST){
                    ADDED_TO_WISHLIST = false;
                    floatingActionButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#88CFC8C8")));
                } else {
                    floatingActionButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#55E3EB")));
                    ADDED_TO_WISHLIST = true;
                    addToWishlist();
                    addToWishlist2();
                }
            }


        });

    }

    private void addToWishlist2() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TestCollection2 testFave = new TestCollection2(displayedPost);
        testFave.addToArrayOfFavoritedPosts(displayedPost);
        firebaseFirestore.collection("testCollection2").document(userID).set(testFave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Toast.makeText(getApplicationContext(),"Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void addToWishlist() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TestCollection testFave = new TestCollection(postID, displayedPost);
        testFave.addToArrayOfFavoritedPosts(postID+"682", displayedPost);
        firebaseFirestore.collection("testCollection").document(userID).set(testFave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Toast.makeText(getApplicationContext(),"Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        //firebaseFirestore.collection("testCollection").add()
    }

    private void getPostDetails(String id) {
        DocumentReference docRef = firebaseFirestore.collection("posts").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                displayedPost  = documentSnapshot.toObject(Post.class);
                title.setText(displayedPost.getTitle());
                price.setText(displayedPost.getPrice().toString()+" "+displayedPost.getValute());
                description.setText(displayedPost.getDescription());
                String dateString = TimestampConverter.timestampToDate(displayedPost.getDate().toString());
                date.setText(dateString);
                username.setText(displayedPost.getUser().getFirstName()+" "+displayedPost.getUser().getLastName());
                Picasso.get().load(displayedPost.getImageUri()).into(image);
            }
        });
    }
}
