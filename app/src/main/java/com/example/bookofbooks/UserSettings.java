package com.example.bookofbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookofbooks.Models.User;
import com.example.bookofbooks.Utility.UsersInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class UserSettings extends AppCompatActivity {

    private TextInputLayout layoutFirstName, layoutLastName, layoutCountry;
    private static ArrayList<String> countries = new ArrayList<String>(Arrays.asList("Serbia", "Bosnia", "Ablbania", "Makedonia", "Hungary","Croatia", "Bulgary",
            "Britain","Belgium"));
    String firstName, lastName, country;
    Button saveChanges;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);

        layoutFirstName = (TextInputLayout) findViewById(R.id.firstNameLayout);
        layoutLastName = (TextInputLayout) findViewById(R.id.lastNameLayout);
        layoutCountry = (TextInputLayout) findViewById(R.id.countryLayout);
        saveChanges = (Button) findViewById(R.id.user_info_update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        AutoCompleteTextView selectCountry = findViewById(R.id.country);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, countries);
        selectCountry.setAdapter(adapter);
        Log.d("TAG", "Calling method");

        getUserInfo();
        Log.d("TAG", "Mehod called");

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(validateForm()) {
                   updateUser();
                   finish();
               }
            }
        });

    }

    private void updateUser() {
        String id = UsersInfo.getUserID();
        //Toast.makeText(getApplicationContext(), firstName +" " +lastName + " " +country, Toast.LENGTH_SHORT).show();
        User user = new User(firstName, lastName, country, UsersInfo.getUserInfo().getEmail());
        UsersInfo.setUser(user);
        firebaseFirestore.collection("users").document(id).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private boolean validateForm() {
        return (validateFirstName() && validateLastName() && validateCountry());
    }

    private boolean validateFirstName(){
        firstName = layoutFirstName.getEditText().getText().toString();
        if(firstName.isEmpty()){
            layoutFirstName.setError("Enter first name");
            return false;
        } else {
            layoutFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName(){
        lastName = layoutLastName.getEditText().getText().toString();
        if(lastName.isEmpty()){
            layoutLastName.setError("Enter last name");
            return false;
        } else {
            layoutLastName.setError(null);
            return true;
        }
    }

    private boolean validateCountry(){
        country = layoutCountry.getEditText().getText().toString();
        if(country.isEmpty()){
            layoutCountry.setError("Select a country");
            return false;
        } else if(countries.indexOf(country)!= -1){
            layoutCountry.setError(null);
            return true;
        } else {
            layoutCountry.setError("Select a valid country");
            return false;
        }
    }

    private void getUserInfo() {
        layoutFirstName.getEditText().setText(UsersInfo.getUserInfo().getFirstName());
        layoutLastName.getEditText().setText(UsersInfo.getUserInfo().getLastName());
        layoutCountry.getEditText().setText(UsersInfo.getUserInfo().getCountry());
    }
}
