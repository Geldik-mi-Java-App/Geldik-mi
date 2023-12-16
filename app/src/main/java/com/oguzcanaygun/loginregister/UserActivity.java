package com.oguzcanaygun.loginregister;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oguzcanaygun.loginregister.databinding.ActivityUserBinding;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    String userID;
    String userName;
    String email;
    String profilePicUrl;
    String password;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = auth.getUid();
        getdata();

        toolbar = binding.userToolbar.getRoot();
        binding.userToolbar.circularImageView.setImageResource(R.drawable.no_pp_100);
        binding.symbolView.setImageResource(R.drawable.no_pp_100);
        setSupportActionBar(toolbar);

    }
    public void getdata(){

        DocumentReference documentReferenceDoc = firebaseFirestore.collection("UserInfo").document(userID);
        DocumentReference documentReferencePic = firebaseFirestore.collection("ProfilePic").document(userID);
        documentReferenceDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
           if (error!=null){
               Toast.makeText(UserActivity.this, error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
           }
           if (value!=null && value.exists()){
                userName = value.getString("username");
                email = value.getString("email");
                password = value.getString("password");
               toolbar.setTitle(userName);
               binding.textUserName.setText(userName);


           }
            }
        });
        documentReferencePic.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(UserActivity.this, error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if (value!=null && value.exists()){
                    profilePicUrl = value.getString("imageUrl");
                    Picasso.get().load(profilePicUrl).into(binding.symbolView);
                    Picasso.get().load(profilePicUrl).into(binding.userToolbar.circularImageView);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add_alarm){
<<<<<<< Updated upstream
        Intent intent = new Intent(UserActivity.this, MapAlarmAddActivity.class);
        startActivity(intent);
        finish();
=======
            Intent intent = new Intent(UserActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
>>>>>>> Stashed changes
        }
        else if (item.getItemId()==R.id.remove_alarm){

        }
        else if (item.getItemId()==R.id.userSettings){
            Intent intent = new Intent(UserActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void logOutClicked(View view){
        auth.signOut();
        Intent intent = new Intent(UserActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}