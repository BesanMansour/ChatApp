package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.chatapp2.MessageActivity;
import com.project.chatapp2.Model.Chat;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.R;
import com.project.chatapp2.databinding.ItemUserBinding;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageHolder> {

    List<User> users;
    Context context;
    private boolean isChat;
    String theLastMsg;
    FirebaseUser firebaseUser;


    public UserAdapter(List<User> users, Context context, boolean isChat) {
        this.users = users;
        this.context = context;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {

        holder.userName.setText(users.get(holder.getAdapterPosition()).getUserName());

        Log.e("usersmf", users.get(holder.getAdapterPosition()).getUserName().toString());

        if (users.get(holder.getAdapterPosition()).getImageURL().equals("default")) {
            holder.img.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context)
                    .load(users.get(holder.getAdapterPosition()).getImageURL())
                    .circleCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.img);
        }

        if (isChat){
            lastMsg(users.get(holder.getAdapterPosition()).getId(),holder.lat_msg);
        }else {
            holder.lat_msg.setVisibility(View.GONE);
        }

        if (isChat) {
            if (users.get(holder.getAdapterPosition()).getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("user_id", users.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView img, img_on, img_off;
        TextView userName, lat_msg;

        public ImageHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            img = binding.ItemImg;
            userName = binding.ItemUserName;
            img_on = binding.ItemOn;
            img_off = binding.ItemOff;
            lat_msg = binding.ItemLastMsg;

        }
    }

    // check last msg
    private void lastMsg(final String userId, TextView last_msg) {
        theLastMsg = "default";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceive().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)
                            || chat.getReceive().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMsg = chat.getMessage();
                    }

                }
                switch (theLastMsg) {
                    case "default":
                        last_msg.setText("No message");
                        break;
                    default:
                        last_msg.setText(theLastMsg);
                        break;
                }
                theLastMsg = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
