package com.example.bookofbooks.Utility;

import android.util.Log;

import com.example.bookofbooks.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersInfo {

    private  static User user;
    private static String userID;


    public UsersInfo() {
    }

    public static User getUserInfo(){
        return user;
    }

    public static String getUserID(){
        return userID;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UsersInfo.user = user;
    }

    public static void setUserID(String userID) {
        UsersInfo.userID = userID;
    }

    public static void setUsersInfo(User userArg, String userIDArg){
        user = userArg;
        userID = userIDArg;
    }
}
