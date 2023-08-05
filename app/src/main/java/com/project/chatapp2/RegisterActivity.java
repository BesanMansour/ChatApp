package com.project.chatapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.chatapp2.databinding.ActivityRegisterBinding;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.RegisterBtnNext.setOnClickListener(view -> register());
    }
    private void register() {
        String email = binding.RegisterEmail.getText().toString();
        String password = binding.RegisterPass.getText().toString();
        String username = binding.RegisterUserName.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        // نحفظ البيانات في الريل تايم
                        Toast.makeText(this, "userId: "+userId, Toast.LENGTH_SHORT).show();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        Log.e("reference",reference.toString());
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", userId);
                        data.put("email", email);
                        data.put("userName" , username);
                        data.put("imageURL" , "default");
                        data.put("status","offline");
                        data.put("search",username.toLowerCase());

                        Log.e("data",data.toString());

                        reference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "done", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "errorRegister", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}