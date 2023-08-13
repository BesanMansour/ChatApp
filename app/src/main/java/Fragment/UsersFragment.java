package Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.databinding.FragmentUsersBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;

public class UsersFragment extends Fragment {
    FragmentUsersBinding binding;
    List<User> userList;
    UserAdapter userAdapter;
    FirebaseUser firebaseUser;

    public UsersFragment() {
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();

        readUsers();

        binding.UserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Log.e("reference", reference.toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.e("dataSnapshot", dataSnapshot.toString());
                    Log.e("snapshot", snapshot.getChildren().toString());
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getId() != null && !user.getId().equals(firebaseUser.getUid())) {
                        userList.add(user);
                        Log.e("userList", userList.toString());
                        Log.e("user", user.toString());
                    }
                }

                userAdapter = new UserAdapter(userList, getContext(),false);
                binding.UserRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.UserRecycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUsers(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("search").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding.UserSearch.getText().toString().equals("")) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            userList.add(user);
                        }
                    }
                    userAdapter = new UserAdapter(userList, getContext(), false);
                    binding.UserRecycler.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}