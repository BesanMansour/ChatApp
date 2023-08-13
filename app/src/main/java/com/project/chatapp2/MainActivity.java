package com.project.chatapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.chatapp2.Model.Chat;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.Model.ViewPagerAdapter;
import com.project.chatapp2.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;

import Fragment.ChatsFragment;
import Fragment.ProfileFragment;
import Fragment.UsersFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {  // التحقق من أن user ليس null
                    binding.MainUserName.setText(user.getUserName());

                    String imageURL = user.getImageURL();
                    if (imageURL != null && !imageURL.equals("default")) {  // التحقق من أن imageURL ليس null وليس "default"
                        Glide.with(getBaseContext())
                                .load(imageURL)
                                .circleCrop()
                                .error(R.mipmap.ic_launcher)
                                .into(binding.MainProfileImage);
                    } else {
                        binding.MainProfileImage.setImageResource(R.drawable.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this);
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    boolean e = chat != null && chat.getReceive() != null &&
                            chat.getReceive().equals(firebaseUser.getUid()) &&
                            !chat.isSeen();
                    Log.e("chatUnread",e+"");
                    if (chat != null && chat.getReceive() != null &&
                            chat.getReceive().equals(firebaseUser.getUid()) &&
                            !chat.isSeen()) {
                        unread++;
                    }
                }
                Log.d("unread",unread+"");
                if (unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "("+unread+") Chats");
                }

                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

                binding.viewPager.setAdapter(viewPagerAdapter);

                TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Chats");
                            break;
                        case 1:
                            tab.setText("Users");
                            break;
                        case 2 :
                            tab.setText("Profile");
                            break;
                        // قم بإضافة حالات أخرى إذا كنت تستخدم مزيد من الفراغات
                    }
                });
                tabLayoutMediator.attach();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(getBaseContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }
    private void status(String status){
        reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);

        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}