package Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.chatapp2.Model.Chat;
import com.project.chatapp2.Model.ChatList;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.R;
import com.project.chatapp2.databinding.FragmentChatsBinding;
import com.project.chatapp2.databinding.FragmentUsersBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;
import Notification.Token;

public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    List<User> mUsers;
    UserAdapter userAdapter;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    List<ChatList> chatLists;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatLists = new ArrayList<>();


        reference = FirebaseDatabase.getInstance()
                .getReference("ChatList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chat = dataSnapshot.getValue(ChatList.class);
                    chatLists.add(chat);
                }
                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//
//        binding.ChatRecycler.setHasFixedSize(true);
//        binding.ChatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                usersList.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//
//                    if (chat.getSender().equals(firebaseUser.getUid())){
//                        usersList.add(chat.getReceive());
//                    }
//                    if (chat.getReceive().equals(firebaseUser.getUid())){
//                        usersList.add(chat.getSender());
//                    }
//
//                }
//                readChats();
//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // استخدم هذا السطر للحصول على الـ FCM token الجديد
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                updateToken(token);
            }
        });


        return binding.getRoot();
    }

    private void updateToken(String token){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);
        reference1.child(firebaseUser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {  // التحقق من أن user ليس قيمة null
                        for (ChatList chatList : chatLists) {
                            String userId = user.getId();
                            String chatListId = chatList.getId();
                            if (userId != null && chatListId != null && userId.equals(chatListId)) {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(mUsers, getContext(), true);
                binding.ChatRecycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


//    void readChats(){
//        mUsers = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User user = dataSnapshot.getValue(User.class);
//
//                    //display 1 user from chats
//                    for (String id : usersList){
//                        if (user.getId().equals(id)){
//                            if (mUsers.size()!=0){
//                                for (User user1 : mUsers){
//                                    if (!user.getId().equals(user1.getId())){
//                                        mUsers.add(user);
//                                    }
//                                }
//                            }else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(mUsers,getContext(),true);
//                binding.ChatRecycler.setAdapter(userAdapter);
//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}