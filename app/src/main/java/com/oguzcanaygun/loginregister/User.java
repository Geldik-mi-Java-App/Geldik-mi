package com.oguzcanaygun.loginregister;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class User {
    private HashMap<String,Object> userData = new HashMap<>();
    private String imageUrl;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    private Context userContext;

    public User(Context userContext) {
        this.userContext = userContext;
    }

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
    public interface ImageUrlCallback{
        void onImageUrlReceived(String imageUrl);
    }

    public String getImageUrl(ImageUrlCallback callback) {
        String userID = auth.getUid();
        DocumentReference documentReferencePic = firebaseFirestore.collection("ProfilePic").document(userID);
        documentReferencePic.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(userContext, error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if (value!=null && value.exists()){
                    imageUrl = value.getString("imageUrl");
                    callback.onImageUrlReceived(imageUrl);
                }
            }
        });
        return imageUrl;
    }

    public HashMap<String, Object> getUserData(){
        return userData;
    }

}
