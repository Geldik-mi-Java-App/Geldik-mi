package com.oguzcanaygun.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.oguzcanaygun.loginregister.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();


    }

    public void signUpClicked(View view){
       String userName= binding.username.getText().toString();
       String passWord1= binding.password1.getText().toString();
       String passWord2= binding.password2.getText().toString();
       String eMail= binding.eMailAdress.getText().toString();

       if (!userName.isEmpty() && !eMail.isEmpty() && passWord1.equals(passWord2)){
    auth.createUserWithEmailAndPassword(eMail,passWord1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            Toast.makeText(RegisterActivity.this, "Kayıt başarılı!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    });
       }
       else {
           Toast.makeText(this, "Girdiğiniz bilgiler hatalıdır.",Toast.LENGTH_LONG).show();
       }
    }
    public void backToLoginClicked(View view){
        Intent intent= new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}