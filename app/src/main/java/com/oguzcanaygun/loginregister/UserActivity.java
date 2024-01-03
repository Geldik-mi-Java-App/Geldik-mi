package com.oguzcanaygun.loginregister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oguzcanaygun.loginregister.databinding.ActivityUserBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements UserIdCallback, MapFragment.OnAlarmSelectedListener {




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
    Map<String, List<Alarm>> alarmCollection = new HashMap<>();

    ExpandableListView expandableListView;
    MyExpandableListAdapter expandableListAdapter;
    ArrayList<String> alarmisimler;
    private static List<Alarm> alarmList;
    private List<String> namesList = new ArrayList<>();
    private List<Double> latitudeList = new ArrayList<>();
    private List<Double> longitudeList = new ArrayList<>();
    private List<Double> radiusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkLocationPermissions()){
            Intent intent = new Intent(UserActivity.this, MapAlarmAddActivity.class);
            startActivity(intent);
            finish();

        }

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        alarmisimler= new ArrayList<String>();
        alarmList= new ArrayList<>();

        userID = auth.getUid();
        System.out.println(userID);
        if (userID==null){
            retrieveUserID(this);
        }
        else {
            getdata();
            getAlarmData();
            createGroupList();
            createCollection();
        }
        MapFragment mapFragment = new MapFragment();
        mapFragment.setOnAlarmSelectedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapFragmentContainer, mapFragment)
                .commit();


        toolbar = binding.userToolbar.getRoot();
        binding.userToolbar.circularImageView.setImageResource(R.drawable.no_pp_100);
        binding.symbolView.setImageResource(R.drawable.no_pp_100);
        setSupportActionBar(toolbar);

        setExpandableListView();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupNavigationDrawer();

        int i = R.id.friends;
        System.out.println(i);
        System.out.println(R.id.exit);

    }
    private boolean checkLocationPermissions() {
        // Check if the app has the necessary location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarsePermission = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int finePermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

            return coarsePermission == PackageManager.PERMISSION_GRANTED &&
                    finePermission == PackageManager.PERMISSION_GRANTED;
        }

        // If the SDK version is less than M, assume permissions are granted
        return true;
    }

    public void setExpandableListView(){
        expandableListView= findViewById(R.id.expandableListView);
        expandableListAdapter = new MyExpandableListAdapter(this, groupList, alarmCollection);
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
                String selectedGroup = groupList.get(groupPosition);

                if (alarmCollection.containsKey(selectedGroup)) {
                    List<Alarm> alarmsInGroup = alarmCollection.get(selectedGroup);

                    if (alarmsInGroup != null && childPosition < alarmsInGroup.size()) {
                        Alarm selectedAlarm = alarmsInGroup.get(childPosition);

                        String toastMessage = "Alarm Adı: " + selectedAlarm.getAlarmName() +
                                "\nEnlem: " + selectedAlarm.getLatitude() +
                                "\nBoylam: " + selectedAlarm.getLongitude() +
                                "\nÇap: " + selectedAlarm.getRadius();
                        onAlarmSelected(selectedAlarm.getLatitude(),selectedAlarm.getLongitude(),selectedAlarm.getRadius());
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                        expandableListView.collapseGroup(groupPosition);
                    } else {
                        // Handle the case where the alarmsInGroup is null or the index is out of bounds
                        Toast.makeText(getApplicationContext(), "Invalid selection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle the case where the selectedGroup is not in alarmCollection
                    Toast.makeText(getApplicationContext(), "Invalid group selection", Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

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
        Log.d("UserActivity", "Creating collection for user: " + userID);
        if (userID == null) {
            Toast.makeText(UserActivity.this, "User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = getIntent();
        String alarmName = intent.getStringExtra("alarm_ismi");
        int radius = intent.getIntExtra("radius", 0);
        LatLng latLng = intent.getParcelableExtra("latlng");

        if (alarmName == null || latLng == null) {
            Toast.makeText(UserActivity.this, "", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an Alarm object
        Alarm newAlarm = new Alarm(alarmName, latLng.latitude, latLng.longitude, radius);

        // Add the new alarm to the list
        alarmList.add(newAlarm);

        // Update the mobile collection for the "Alarmlar" group
        loadChild(newAlarm);

        if (alarmCollection == null) {
            alarmCollection = new HashMap<>();
        }

        if (!alarmCollection.containsKey("Alarmlar")) {
            alarmCollection.put("Alarmlar", new ArrayList<>());
        }

        // Add the new alarm to the list
        alarmList.add(newAlarm);

        // Update the mobile collection for the "Alarmlar" group
        loadChild(newAlarm);

        // Update the Firebase Firestore
        DocumentReference alarmlarDocRef = firebaseFirestore.collection("Alarmlar").document(userID);
        CollectionReference alarmsCollectionRef = alarmlarDocRef.collection("Alarms");

        alarmsCollectionRef.document(alarmName)
                .set(newAlarm.toMap())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Alarm Kaydı Başarılı", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Alarm kaydı başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

        // Notify the adapter about the data change
        if (expandableListAdapter != null) {
            expandableListAdapter.notifyDataSetChanged();
            Log.d("UserActivity", "Collection created successfully");
        }
    }

    private void loadChild(Alarm alarm) {
        if (alarmCollection == null) {
            alarmCollection = new HashMap<>();
        }

        if (alarmCollection.containsKey("Alarmlar")) {
            List<Alarm> alarms = alarmCollection.get("Alarmlar");
            if (alarms == null) {
                alarms = new ArrayList<>();
                alarmCollection.put("Alarmlar", alarms);
            }
            alarms.add(alarm);
        }

        if (expandableListAdapter != null) {
            expandableListAdapter.notifyDataSetChanged();
        }
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
                startBackGroundService(dummyView);


                }
                else if (item.getItemId()==R.id.logOut){logOutClicked(dummyView);}
                else if (item.getItemId()==R.id.exit){System.exit(0);}
                return false;
            }
        });
    }
    public void startBackGroundService(View view){
        Intent serviceIntent = new Intent(UserActivity.this, MyBackgroundService.class);
        startService(serviceIntent);
        finish();
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
    public void getAlarmData(){
        Log.d("UserActivity", "Getting alarm data for user: " + userID);

        String userId = userID;
        String path = "Alarmlar/" + userId + "/Alarms";

        CollectionReference collectionReference = firebaseFirestore.collection(path);

        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Log.d("UserActivity", "Query snapshot size: " + querySnapshot.size());

                            // Clear existing lists before updating with new data
                            namesList.clear();
                            latitudeList.clear();
                            longitudeList.clear();
                            radiusList.clear();

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Update lists with alarm information
                                namesList.add(document.getString("alarmName"));
                                latitudeList.add(document.getDouble("latitude"));
                                longitudeList.add(document.getDouble("longitude"));
                                radiusList.add(document.getDouble("radius"));
                            }

                            // Create Alarm objects and add them to the alarmCollection
                            List<Alarm> alarms = new ArrayList<>();
                            for (int i = 0; i < namesList.size(); i++) {
                                String name = namesList.get(i);
                                double latitude = latitudeList.get(i);
                                double longitude = longitudeList.get(i);
                                double radius = radiusList.get(i);

                                Alarm alarm = new Alarm(name, latitude, longitude, radius);
                                alarms.add(alarm);
                            }

                            // Update the alarmCollection with the new alarms
                            alarmCollection.put("Alarmlar", alarms);

                            Log.d("UserActivity", "Alarm data retrieval successful");
                            // Notify the adapter after processing all documents
                            if (expandableListAdapter != null) {
                                expandableListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("UserActivity", "Error getting documents: " + task.getException());
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
        else if (item.getItemId()==R.id.permissions){
            Intent intent = new Intent(UserActivity.this, PermissionActivity.class);
            startActivity(intent);
            finish();
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


    @Override
    public void onAlarmSelected(double latitude, double longitude, double radius) {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.drawCircleOnMap(latitude, longitude, radius);
        }
    }
}