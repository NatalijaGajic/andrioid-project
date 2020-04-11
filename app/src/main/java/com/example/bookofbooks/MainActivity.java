package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    private void userLogin() {
        if(validateForm()){
            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
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
