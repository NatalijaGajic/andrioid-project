package com.example.bookofbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookofbooks.Models.Post;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PostDetails extends AppCompatActivity {

    private TextView title, description, username, date, country;
    private Button price;
    private ImageView image;
    private String postID;
    private FirebaseFirestore firebaseFirestore;
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

        firebaseFirestore = FirebaseFirestore.getInstance();
        postID = getIntent().getStringExtra("postID");

        getProductDetails(postID);

    }

    private void getProductDetails(String id) {
        DocumentReference docRef = firebaseFirestore.collection("posts").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                title.setText(post.getTitle());
                price.setText(post.getPrice().toString()+" "+post.getValute());
                description.setText(post.getDescription());
                String dateString = TimestampConverter.timestampToDate(post.getDate().toString());
                date.setText(dateString);
                username.setText(post.getUser().getFirstName()+" "+post.getUser().getLastName());
                Picasso.get().load(post.getImageUri()).into(image);
            }
        });
    }
}
