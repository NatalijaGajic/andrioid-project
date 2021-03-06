package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bookofbooks.Models.User;
import com.example.bookofbooks.Utility.TimestampConverter;
import com.example.bookofbooks.Utility.ValuteGetter;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.StringReader;
import java.util.ArrayList;

public class CreatePost extends AppCompatActivity {

    Button createPost, editPost;
    TextInputLayout titleLayout, priceLayout;
    EditText description;
    Spinner valuteSpinner;
    String title, descriptionString, price, valute, postID, userID;
    Integer priceValue;
    Button takePicture, choosePicture;
    ImageView imageView;
    Uri imageUri, imageUriBeforeCrop;
    StorageReference fileReference;
    StorageReference mStorageRef;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    Post displayedPost;
    private static final int GALLERY_PICK_CODE = 1;
    private static final int PERMISSION_CODE = 1000;
    private static final int OPEN_CAMERA_CODE = 1000;
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
        takePicture = (Button) findViewById(R.id.button_take_picture);
        choosePicture = (Button) findViewById(R.id.button_choose_picture);
        titleLayout = findViewById(R.id.create_book_title_layout);
        priceLayout = findViewById(R.id.create_price_layout);
        description = findViewById(R.id.create_description);
        userID = mAuth.getCurrentUser().getUid();


        if(getIntent().getStringExtra("postID")== null){
            //create post
            createPost = findViewById(R.id.create_button);
            createPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validateForm()) {
                        valute = valuteSpinner.getSelectedItem().toString();
                        Log.d("CREATE POST CLICKED", "validated");
                        //TODO obezbedi da se ne dodaje 20 puta kada klikne na dugme
                        //Firebase Storage - Upload and Retrieve Images - Part 3 - UPLOAD IMAGE - Android Studio Tutoria 20. minut
                        uploadPost("create");
                        startActivity(new Intent(CreatePost.this, HomePage.class));
                        finish();
                        Toast.makeText(getApplicationContext(),"Post created", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            //edit post
            postID = getIntent().getStringExtra("postID");
            getPostDetails(postID);
            editPost = findViewById(R.id.create_button);
            editPost.setText("UPDATE POST");
            editPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateForm()) {
                        valute = valuteSpinner.getSelectedItem().toString();
                        uploadPost("edit");
                        finish();
                        Toast.makeText(getApplicationContext(), "Post updated", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(CreatePost.this, Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(CreatePost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        //permission denied
                        String[] permision  = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions(CreatePost.this, permision, PERMISSION_CODE);
                    }
                } else {
                    openCamera();
                }
                openCamera();
            }
        });



        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });





    }

    private void updatePost() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        imageUriBeforeCrop = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriBeforeCrop);
        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
    }

    private void getPostDetails(String id) {
        Log.d("ID POSTA KOJI TREBA EDITOVATI", "Id posta koji treba editovati "+id);
        DocumentReference docRef = mStore.collection("posts").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                displayedPost  = documentSnapshot.toObject(Post.class);
                Log.d("DISPLAY POST", displayedPost.toString());
                if(displayedPost.getDescription()!=null){
                    description.setText(displayedPost.getDescription());
                }
                priceLayout.getEditText().setText(displayedPost.getPrice().toString());
                titleLayout.getEditText().setText(displayedPost.getTitle());
                String[] array = getResources().getStringArray(R.array.valutes);
                int index = ValuteGetter.getIndexOfValute(displayedPost.getValute(), array);
                valuteSpinner.setSelection(index);
                Picasso.get().load(displayedPost.getImageUri()).into(imageView);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK_CODE && resultCode==RESULT_OK && data!=null){
            //imageUri = data.getData();
            CropImage.activity(data.getData())
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);
            //Picasso.get().load(imageUri).fit().centerCrop().into(imageView);
        } else if(requestCode==OPEN_CAMERA_CODE && resultCode==RESULT_OK){
            CropImage.activity(imageUriBeforeCrop)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
            //Picasso.get().load(imageUri).fit().centerCrop().into(imageView);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
                //Picasso.get().load(imageUri).fit().centerCrop().into(imageView);
                Picasso.get().load(imageUri).into(imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean validateForm() {
        return (validateTitle() && validatePrice());
    }

    private boolean validateTitle() {
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

    private void uploadPost(final String upload_mode) {
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
                        Log.d("TASK COMPLETED","sending download url");
                        if(upload_mode.equals("create")){
                            savePostToDB(userID, downloadURL);
                        } else if(upload_mode.equals("edit")){
                            updatePostToDB(userID, downloadURL, postID);
                        } else {
                            Log.d("TASK COMPLETED","what is going on");
                            //TODO kada se ne izabere slika pri udejtu, imageUri ostaje null
                        }

                    } else
                    {
                        Toast.makeText(CreatePost.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if(upload_mode.equals("edit")){
                updatePostToDB(userID, displayedPost.getImageUri(), postID);
            } else {
                savePostToDB(userID, getString(R.string.default_image_url));
            }
        }
    }

    private void updatePostToDB(final String id, final String downloadURL, final String postID) {
        Log.d("", "Stigao do ovde");
        DocumentReference docRef = mStore.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                descriptionString = description.getText().toString();
                final Post post = new Post(downloadURL, title, priceValue, valute, descriptionString);
                post.setUsersFollowing(displayedPost.getUsersFollowing());
               /* mStore.collection("posts").document(postID).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Post postWithID = documentSnapshot.toObject(Post.class);
                                post.setUsersFollowing(postWithID.getUsersFollowing());
                            }
                        });*/
                post.setUser(user);
                post.setUserID(id);
                Log.d("TAG", "Updated stigao do naredbe za update");
                mStore.collection("posts").document(postID)
                    .set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Toast.makeText(getApplicationContext(), "Post updated", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FAILED TO UPDATE", e.getMessage());

                    }
                });
            }
        });
    }

    private void savePostToDB(final String id, final String downloadUlr) {

        DocumentReference docRef = mStore.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                descriptionString = description.getText().toString();
                Post post = new Post(downloadUlr, title, priceValue, valute, descriptionString);
                post.setUsersFollowing(new ArrayList<String>());
                post.setUser(user);
                post.setUserID(id);
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
        });
    }

}
