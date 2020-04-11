package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.example.bookofbooks.Models.User;

public class SignUpActivity extends AppCompatActivity {

    public static final String STRING = "";
    // private static final String[] countries = new String[] {"Serbia", "Bosnia", "Ablbania", "Makedonia", "Hungary","Croatia", "Bulgary",
    //"Britain","Belgium"};
   private static ArrayList<String> countries = new ArrayList<String>(Arrays.asList("Serbia", "Bosnia", "Ablbania", "Makedonia", "Hungary","Croatia", "Bulgary",
            "Britain","Belgium"));
   // private TextInputEditText firstName, lastName, userEmail, userPassword, userConfirmPassword;
    private String fName, lName, inputCountry, email, password, passConfirmed;
    private TextInputLayout layoutFirstName, layoutLastName, layoutUserEmail, layoutUserPassword, layoutUserConfirmPassword, layoutCountry;
    private Button signUpButton;
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z]).{6,}$");
    private FirebaseAuth mAuth;
    private ProgressBar loader;
    FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

       AutoCompleteTextView selectCountry = findViewById(R.id.country);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, countries);
        selectCountry.setAdapter(adapter);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        loader = (ProgressBar) findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createNewAccount();
            }
        });

    }

    private void createNewAccount() {

        layoutFirstName = (TextInputLayout) findViewById(R.id.firstNameLayout);
        layoutLastName = (TextInputLayout) findViewById(R.id.lastNameLayout);
        layoutCountry = (TextInputLayout) findViewById(R.id.countryLayout);
        layoutUserEmail = (TextInputLayout) findViewById(R.id.signUpEmailLayout);
        layoutUserPassword = (TextInputLayout) findViewById(R.id.signUpPassLayout);
        layoutUserConfirmPassword = (TextInputLayout) findViewById(R.id.signUpConfirmPassLayout);
        String email = layoutUserEmail.getEditText().getText().toString();
        String password =layoutUserPassword.getEditText().getText().toString();
        String confirmPassword = layoutUserConfirmPassword.getEditText().getText().toString();
        if(validateForm()){
            loader.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loader.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        userID = mAuth.getCurrentUser().getUid();
                        saveUserToDB(userID);
                        sendUserToMainPage();
                        Toast.makeText(getApplicationContext(),"Account created", Toast.LENGTH_SHORT).show();
                    } else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(), "You already have an account",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occured",Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });

        }

    }

    private void saveUserToDB(final String id) {
        DocumentReference documentReference = fStore.collection("users").document(id);
        User user = new User(fName, lName, inputCountry, email);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //ctrl+alt+c
                Log.d(STRING, "User created");
            }
        });
    }

    private void sendUserToMainPage() {
        Intent mainPage = new Intent(this, HomePage.class);
        //mainPage.setFlags()
        startActivity(mainPage);
        finish();

    }

    private boolean validateFirstName(){
        fName = layoutFirstName.getEditText().getText().toString();
        if(fName.isEmpty()){
            layoutFirstName.setError("Enter first name");
            return false;
        } else {
            layoutFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName(){
        lName = layoutLastName.getEditText().getText().toString();
        if(lName.isEmpty()){
            layoutLastName.setError("Enter last name");
            return false;
        } else {
            layoutLastName.setError(null);
            return true;
        }
    }

    private boolean validateCountry(){
        inputCountry = layoutCountry.getEditText().getText().toString();
        if(inputCountry.isEmpty()){
            layoutCountry.setError("Select a country");
            return false;
        } else if(countries.indexOf(inputCountry)!= -1){
            layoutCountry.setError(null);
            return true;
        } else {
            layoutCountry.setError("Select a valid country");
            return false;
        }
    }

    private boolean validateEmail(){
        email = layoutUserEmail.getEditText().getText().toString();
        if(email.isEmpty()){
            layoutUserEmail.setError("Enter an email");
            return false;
        } else if (!email.matches(emailPattern)){
            layoutUserEmail.setError("Enter a valid email");
            return false;
        } else {
            layoutUserEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword () {
        password = layoutUserPassword.getEditText().getText().toString();
        if(password.isEmpty()){
            layoutUserPassword.setError("Enter a password");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()){
            layoutUserPassword.setError("Password should be 6 characters long");
            return false;
        } else {
            layoutUserPassword.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPasword(){
        String pass = layoutUserPassword.getEditText().getText().toString();
        passConfirmed = layoutUserConfirmPassword.getEditText().getText().toString();
        if(!pass.equals(passConfirmed)){
            layoutUserConfirmPassword.setError("Passwords don't match");
            return false;
        } else {
            layoutUserConfirmPassword.setError(null);
            return true;
        }
    }

    private boolean validateForm(){
        if(validateFirstName() && validateLastName() && validateCountry() && validateEmail()
                && validatePassword() && validateConfirmPasword()){
            return true;
        }else
            return false;
    }

}
