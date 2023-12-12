package com.oguzcanaygun.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.firestore.auth.User;
import com.oguzcanaygun.loginregister.databinding.ActivitySettingsBinding;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.settingsToolbar.getRoot();
        toolbar.setTitle("Ayarlar");
        setSupportActionBar(toolbar);

        List<Item> items= new ArrayList<Item>();
        items.add(new Item("Profil Foto",R.drawable.baseline_add_a_photo_24));
        items.add(new Item("Ses Ayarı",R.drawable.baseline_surround_sound_24));
        items.add(new Item("Arkadaş Ekle",R.drawable.baseline_group_add_24));
        items.add(new Item("Alarm Sesi",R.drawable.baseline_library_music_24));


        RecyclerView recyclerView = binding.recyclerView;
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(new SettingsAdapter(getApplicationContext(),items));

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
}