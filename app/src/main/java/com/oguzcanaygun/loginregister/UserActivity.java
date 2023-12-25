package com.oguzcanaygun.loginregister;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.oguzcanaygun.loginregister.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.oguzcanaygun.loginregister.databinding.ActivityUserBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements UserIdCallback {




    ActivityUserBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    String userID;
    String userName;
    String email;
    String profilePicUrl;
    String password;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    List<String> groupList;
    List<HashMap<String, Object>> childList;
    Map<String, List<String>> mobileCollection;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> alarmisimler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        alarmisimler= new ArrayList<String>();

        userID = auth.getUid();
        System.out.println(userID);
        if (userID==null){
            retrieveUserID(this);
        }
        else {
            getdata();
            createGroupList();
            createCollection();
        }



        toolbar = binding.userToolbar.getRoot();
        binding.userToolbar.circularImageView.setImageResource(R.drawable.no_pp_100);
        binding.symbolView.setImageResource(R.drawable.no_pp_100);
        setSupportActionBar(toolbar);




        expandableListView= findViewById(R.id.expandableListView);
        expandableListAdapter = new MyExpandableListAdapter(this, groupList,mobileCollection);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition =-1;
            @Override
            public void onGroupExpand(int groupPosition) {
            if (lastExpandedPosition!=-1 && groupPosition!= lastExpandedPosition){
                expandableListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition=groupPosition;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object selectedObject = expandableListAdapter.getChild(groupPosition, childPosition);

                if (selectedObject != null) {
                    String selected = selectedObject.toString();
                    Toast.makeText(getApplicationContext(), selected + " Seçildi", Toast.LENGTH_LONG).show();
                } else {
                    // Handle the case where selectedObject is null
                    Toast.makeText(getApplicationContext(), "Null object selected", Toast.LENGTH_LONG).show();
                }


                return true;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupNavigationDrawer();

        int i = R.id.friends;
        System.out.println(i);
        System.out.println(R.id.exit);

    }
    @Override
    public void onUserIdReceived(String userID) {
        Log.d("UserActivity", "Received userID: " + userID);
        this.userID=userID;
        getdata();
        createCollection();
    }

    public void retrieveUserID(UserIdCallback callback){
        String uid = auth.getUid();
        if (uid == null) {
            // Handle the case where the user ID is null
            Toast.makeText(UserActivity.this, "User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userDocRef = firebaseFirestore.collection("UserInfo").document(auth.getUid());

        // Fetch the document
        userDocRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // If the document exists, get the userID and notify the callback
                            String userID = document.getId();
                            callback.onUserIdReceived(userID);
                        } else {
                            // Handle the case where the document does not exist
                            Toast.makeText(UserActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle exceptions or errors during the document fetch
                        Toast.makeText(UserActivity.this, "Error fetching user document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void createCollection() {
        if (userID == null) {
            // Handle the case where userID is null
            Toast.makeText(UserActivity.this, "User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = getIntent();
        String alarm = intent.getStringExtra("alarm_ismi");
        int radius = intent.getIntExtra("radius", 0);
        LatLng latLng = intent.getParcelableExtra("latlng");

        if (alarm == null || latLng == null) {
            // Handle the case where alarm or latLng is null
            Toast.makeText(UserActivity.this, "Alarm or LatLng is null", Toast.LENGTH_SHORT).show();
            return;
        }

        alarmisimler.add(alarm);

        HashMap<String, Object> alarmDetay = new HashMap<>();
        alarmDetay.put("latlng", latLng);
        alarmDetay.put("radius", radius);

        mobileCollection = new HashMap<>();
        for (String group : groupList) {
            if (group.equals("Alarmlar")) {
                loadChild(alarmDetay);
            }

            mobileCollection.put(group, alarmisimler);
        }

        HashMap<String, Object> alarmDetails = new HashMap<>();
        alarmDetails.put("alarmName", alarm);
        alarmDetails.put("radius", radius);
        alarmDetails.put("latlng", latLng);

        DocumentReference alarmlarDocRef = firebaseFirestore.collection("Alarmlar").document(userID);

        if (alarmlarDocRef == null) {
            // Handle the case where alarmlarDocRef is null
            Toast.makeText(UserActivity.this, "Alarmlar Document Reference is null", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference alarmsCollectionRef = alarmlarDocRef.collection("Alarms");
        alarmsCollectionRef.document(alarm)
                .set(alarmDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Alarm Kaydı Başarılı", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Alarm kaydı başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadChild(HashMap<String, Object> alarmDetay) {

        if (childList==null)
        {childList= new ArrayList<>();}
        childList.add(alarmDetay);
    }


    private void createGroupList() {
    groupList = new ArrayList<>();
    groupList.add("Alarmlar");



    }

    public void setupNavigationDrawer(){
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                View dummyView = new View(UserActivity.this);
                if (item.getItemId()==R.id.turnBack){
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
                else if (item.getItemId()==R.id.shareUS){

                }
                else if (item.getItemId()==R.id.friends){

                }
                else if (item.getItemId()==R.id.alarms){

                }
                else if (item.getItemId()==R.id.chatArchive){

                }
                else if (item.getItemId()==R.id.backGround){

                }
                else if (item.getItemId()==R.id.logOut){logOutClicked(dummyView);}
                else if (item.getItemId()==R.id.exit){System.exit(0);}
                return false;
            }
        });
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
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){ return true;}

        if (item.getItemId()==R.id.add_alarm){
            Intent intent = new Intent(UserActivity.this, MapAlarmAddActivity.class);
            startActivity(intent);
            finish();
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