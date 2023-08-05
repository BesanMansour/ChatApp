package com.project.chatapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.chatapp2.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.LoginBtnNext.setOnClickListener(view -> Login());

        binding.LoginForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),ResetPasswordActivity.class));
            }
        });

    }
    private void Login() {
        String email = binding.LoginEmail.getText().toString();
        String  pass = binding.LoginPass.getText().toString();
        // وهادا الريسبونس
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                // عشان نعرف انو العملية تمت بنجاح
                //onComplete : العملية نجحت بغض النظر نجحت ولا فشلت
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText( LoginActivity.this, Objects.requireNonNull( task.getException( ) ).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } )
                .addOnFailureListener( e -> Toast.makeText( LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show() );

    }

}