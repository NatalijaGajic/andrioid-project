package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookofbooks.Models.Token;
import com.example.bookofbooks.Models.User;
import com.example.bookofbooks.Utility.UsersInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity  {

    private  Button signUpButton;
    private Button logInButton;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private FirebaseAuth mAuth;
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        logInButton  = (Button) findViewById(R.id.logInButton);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        mAuth = FirebaseAuth.getInstance();
        //ako je loginovan odmah ga redirektuje
        if(mAuth.getCurrentUser()!= null){
            //ovaj deo treba u home page
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
            getToken();
           startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        }
        logInButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void getToken() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String id = mAuth.getUid();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String tokenString = instanceIdResult.getToken();
               // Toast.makeText(getApplicationContext(), "Token " + tokenString, Toast.LENGTH_SHORT).show();
                Token token = new Token(tokenString);
                firebaseFirestore.collection("tokens").document(id).set(token);
            }
        });
    }

    private void userLogin() {
        if(validateForm()){
            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //get token
                      getToken();

                        Intent homePage = new Intent(MainActivity.this, HomePage.class);
                        //homePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homePage);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }

    private boolean validateEmail(){
        String email = emailLayout.getEditText().getText().toString();
        if(email.isEmpty()){
            emailLayout.setError("Enter email");
            return false;
        } else {
            if(!email.matches(emailPattern)){
                emailLayout.setError("Enter valid email");
                return false;
            } else {
                emailLayout.setError(null);
                return true;
            }

        }
    }

    private boolean validatePassword(){
        String password = passwordLayout.getEditText().getText().toString();
        if(password.isEmpty()){
            passwordLayout.setError("Enter a password");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    private boolean validateForm() {
        return (validateEmail() && validatePassword());
    }
}
