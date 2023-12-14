package com.oguzcanaygun.loginregister;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class User {
    private HashMap<String,Object> userData = new HashMap<>();
    private String imageUrl;

    public void setUserName(String userName){
        userData.put("username", userName);
    }
    public void setUserEmail(String userEmail){
        userData.put("email", userEmail);
    }
    public void setPassword(String password){
        userData.put("password", password);
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl =imageUrl;
        userData.put("imageUrl", this.imageUrl);
    }
    public HashMap<String, Object> getUserData(){
        return userData;
    }

}
