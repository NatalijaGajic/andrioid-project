package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.bookofbooks.Models.Post;
import com.squareup.picasso.Picasso;

public class CreatePost extends AppCompatActivity {

    Button createPost;
    TextInputLayout titleLayout, priceLayout;
    EditText description;
    Spinner valuteSpinner;
    String title, descriptionString, price, valute;
    Integer priceValue;
    ImageView imageView;
    private static final int galleryPick =1;
    Uri imageUri;
    StorageReference fileReference;
    StorageReference mStorageRef;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        valuteSpinner = findViewById(R.id.spinner_valute);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.valutes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        valuteSpinner.setAdapter(adapter);

        imageView = findViewById(R.id.select_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        createPost = findViewById(R.id.create_button);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()) {
                    valute = valuteSpinner.getSelectedItem().toString();
                    Log.d("CREATE POST CLICKED", "validated");
                    //TODO obezbedi da se ne dodaje 20 puta kada klikne na dugme
                    //Firebase Storage - Upload and Retrieve Images - Part 3 - UPLOAD IMAGE - Android Studio Tutoria 20. minut
                    uploadPost();
                    startActivity(new Intent(CreatePost.this, HomePage.class));
                    finish();
                   Toast.makeText(getApplicationContext(),"Post created", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).fit().centerCrop().into(imageView);
        }
    }

    private boolean validateForm() {
        return (validateTitle() && validatePrice());
    }

    private boolean validateTitle() {
        titleLayout = findViewById(R.id.create_book_title_layout);
        title = titleLayout.getEditText().getText().toString();
        if(title.isEmpty()){
            titleLayout.setError("Title must be given");
            return false;
        }else {
            titleLayout.setError(null);
            return true;
        }
    }

    private boolean validatePrice() {
        priceLayout = findViewById(R.id.create_price_layout);
        price = priceLayout.getEditText().getText().toString();
        if(price.isEmpty()){
            priceLayout.setError("Price must be added");
            return false;
        }else {
            priceValue = Integer.parseInt(price);
            if(priceValue > 0){
                priceLayout.setError(null);
                return true;
            } else
                priceLayout.setError("Price must be positive");
            return false;
        }
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPost() {
        Log.d("SAVING TO STORAGE","saving to storage");
        if (imageUri != null) {
            fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getImageExtension(imageUri));

            fileReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    Log.d("TASK CONTINUED","getting download url");
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                        //TODO ovde je pre pucao kod ko zna zasto
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        String downloadURL = task.getResult().toString();
                        String userID = mAuth.getCurrentUser().getUid();
                        Log.d("TASK COMPLETED","sending download url");
                        savePostToDB(userID, downloadURL);

                    } else
                    {
                        Toast.makeText(CreatePost.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void savePostToDB(String id, String downloadUlr) {
        Post post = new Post(downloadUlr,title, priceValue, valute, descriptionString);
        post.setUserID(id);
       /* final StringBuilder username = new StringBuilder();
        final StringBuilder country = new StringBuilder();
        DocumentReference docRef = mStore.collection("users").document(id);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                username.append(documentSnapshot.getString("firstName")+" ");
                username.append(documentSnapshot.getString("lastName"));
                country.append(documentSnapshot.getString("country"));
            }
        });
        post.setCountry(country.toString());
        post.setUserName(username.toString());*/
        mStore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error adding document", e);
                    }
                });
    }

}
