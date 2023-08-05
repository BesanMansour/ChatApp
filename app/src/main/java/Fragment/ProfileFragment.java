package Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.R;
import com.project.chatapp2.databinding.FragmentProfileBinding;
import com.project.chatapp2.databinding.FragmentUsersBinding;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    public static String image;


    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate( inflater,container,false );
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.profileUserName.setText(user.getUserName());
                if (user.getImageURL().equals("default")){
                    binding.profileImg.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext())
                            .load(user.getImageURL())
                            .circleCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(binding.profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // فيديو 11
        if ( ContextCompat.checkSelfPermission( getContext() , Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( getActivity() , new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE } , IMAGE_REQUEST );
        }

        ActivityResultLauncher< String > al1 = registerForActivityResult( new ActivityResultContracts.GetContent( ) , result -> {
            if ( result != null ) {
                Glide.with(getContext())
                        .load(result)
                        .circleCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into(binding.profileImg);
//                StorageReference reference = storage.getReference( "users/" + "images/" + firebaseUser.getPhoneNumber( ) );

                StorageTask < UploadTask.TaskSnapshot > uploadTask = storageReference.putFile( result );

                uploadTask.addOnSuccessListener( taskSnapshot -> storageReference.getDownloadUrl( ).addOnCompleteListener( task -> {
                    if ( task.isSuccessful( ) ) {
                        image = task.getResult( ).toString( );

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imageURL",image);
                        reference.updateChildren(map);
                        Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    }
                } ) );
            }

        } );

        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.launch( "image/*" );
            }
        });


        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        return binding.getRoot();
    }
    void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

    }
}