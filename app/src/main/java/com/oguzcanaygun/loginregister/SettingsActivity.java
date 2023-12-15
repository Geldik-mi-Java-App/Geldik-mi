package com.oguzcanaygun.loginregister;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oguzcanaygun.loginregister.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity implements ItemClickInterface {
    Uri imageData;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    User user = new User(this);

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.settingsToolbar.getRoot();
        toolbar.setTitle("Ayarlar");
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference =firebaseStorage.getReference();



        auth = FirebaseAuth.getInstance();
        registerLauncher();
        visualSet();

        List<Item> items= new ArrayList<Item>();
        items.add(new Item("Profil Foto",R.drawable.baseline_add_a_photo_24));
        items.add(new Item("Ses Ayarı",R.drawable.baseline_surround_sound_24));
        items.add(new Item("Arkadaş Ekle",R.drawable.baseline_group_add_24));
        items.add(new Item("Alarm Sesi",R.drawable.baseline_library_music_24));


        RecyclerView recyclerView = binding.recyclerView;
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SettingsAdapter(this, getApplicationContext(),items));

    }
    public void visualSet(){
        String imageUrl = user.getImageUrl(new User.ImageUrlCallback() {
            @Override
            public void onImageUrlReceived(String imageUrl) {
                if (imageUrl==null){
                    binding.settingsToolbar.circularImageView.setImageResource(R.drawable.no_pp_100);
                } else {
                    Picasso.get().load(imageUrl).into(binding.settingsToolbar.circularImageView);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ayarlar_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.ayarlarıKaydet){
            Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onItemClick(int position) {
        switch (position){
            case 0: selectImage(findViewById(android.R.id.content));

                break;
            default: break;

    }
    }
    public void selectImage(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Galeriye Giriş İzni Gerekiyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }
    private void registerLauncher(){
        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult!=null) {
                       imageData= intentFromResult.getData();

                       /* Eğer hep tek resim yazdırmak istemiyorsak..
                       * UUID uuid = UUID.randomUUID();
                         String name=("images/"+uuid+".jpg");
                       * referans olarak bu name'i kullanırsak her seferinde farklı dosyayı ekleme yapar
                       *  */

                        String userID = auth.getUid();

                        storageReference.child("images/profileImages/"+userID+".jpg").putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newReference = firebaseStorage.getReference("images/profileImages/"+userID+".jpg");
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                String downloadURL= uri.toString();
                                user.setImageUrl(downloadURL);


                                    CollectionReference infoCollection= firebaseFirestore.collection("ProfilePic");
                                    infoCollection.document(userID).set(user.getUserData(), SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });


                                /*  firebaseFirestore.collection("Info").add(user.getUserData()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });  */

                                }
                            });

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                           }
                       });
                       //binding.imageView.setImageURI(imageData);  bu sekilde cekecez uri'yı ımageview'a

                    }
                }
            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
            if (o){
            Intent intentToGallery= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
            }
            else {
                Toast.makeText(SettingsActivity.this,"İzin Gerekli",Toast.LENGTH_LONG).show();
            }
            }
        });
    }

}