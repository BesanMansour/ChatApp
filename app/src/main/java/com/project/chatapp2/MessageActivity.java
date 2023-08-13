package com.project.chatapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.chatapp2.Model.Chat;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.databinding.ActivityMessageBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.MessageAdapter;
import Notification.ApiService;
import Notification.Client;
import Notification.Data;
import Notification.MyResponse;
import Notification.Sender;
import Notification.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private List<Chat> chats;
    private MessageAdapter messageAdapter;
    private ValueEventListener seenListener;
    private String user_id;
    private ApiService apiService;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setSupportActionBar(binding.MessageToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.MessageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });

        apiService = Client.getClients("https://fcm.googleapis.com/").create(ApiService.class);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        binding.MessageBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = binding.MessageET.getText().toString();
                if (!message.equals("")) {
                    sendMessage(firebaseUser.getUid(), user_id, message);
                } else {
                    binding.MessageET.setError("يرجى ادخال هذا الحقل");
                }
                binding.MessageET.setText("");
            }
        });

        if (user_id != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    binding.MessageUserName.setText(user.getUserName());
                    if (user.getImageURL().equals("default")) {
                        binding.MessageProfileImage.setImageResource(R.drawable.user);
                    } else {
                        Glide.with(getBaseContext())
                                .load(user.getImageURL())
                                .circleCrop()
                                .error(R.drawable.ic_launcher_background)
                                .into(binding.MessageProfileImage);
                    }
                    readMessage(firebaseUser.getUid(), user_id, user.getImageURL());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        Chat chat = new Chat(sender, receiver, message, false);
        chat.setTimeSent(System.currentTimeMillis());

        reference1.child("Chats").push().setValue(chat);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList").child(firebaseUser.getUid()).child(user_id);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(user_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(user_id)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUserName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String userName, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),
                            R.mipmap.ic_launcher,
                            userName + ": " + msg,
                            "New Notification",
                            user_id);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(String myId, String userId, String imageURL) {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceive() != null && chat.getSender() != null &&
                            (chat.getReceive().equals(myId) && chat.getSender().equals(userId)
                                    || chat.getReceive().equals(userId) && chat.getSender().equals(myId))) {
                        chats.add(chat);
                        if (chat.getReceive().equals(myId) && chat.getSender().equals(userId)) {
                            seenMessage(dataSnapshot.getKey()); // تحديث حالة القراءة هنا
                        }
                    }
                }

                messageAdapter = new MessageAdapter(getBaseContext(), chats, imageURL);
                binding.MessageRecycler.setAdapter(messageAdapter);
                binding.MessageRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                        LinearLayoutManager.VERTICAL, false));
                binding.MessageRecycler.scrollToPosition(chats.size() - 1);

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void seenMessage(String messageId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(messageId);
        reference.child("seen").setValue(true);
    }

    private void currentUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userId);
        editor.apply();
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);

        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(user_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (reference != null && seenListener != null) {
            reference.removeEventListener(seenListener);
        }
        status("offline");
        currentUser("none");
    }

}
