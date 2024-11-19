package com.oguzcanaygun.loginregister;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oguzcanaygun.loginregister.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            startActivity(intent);
            finish();
        }
    }
    public void loginClicked(View view){
        String email = binding.eMailAdress.getText().toString();
        String password = binding.password.getText().toString();

        if (email.isEmpty()&&password.isEmpty()) {
            Toast.makeText(this, "giriş bilgileriniz hatalıdır.", Toast.LENGTH_LONG).show();
        }
        else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            MainActivity.this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                    );
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
    public void registerClicked(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        startActivity(intent);
        finish();
    }
}
