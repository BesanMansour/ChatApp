package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.chatapp2.MessageActivity;
import com.project.chatapp2.Model.Chat;
import com.project.chatapp2.Model.User;
import com.project.chatapp2.R;
import com.project.chatapp2.databinding.ItemChatLeftBinding;
import com.project.chatapp2.databinding.ItemChatRightBinding;
import com.project.chatapp2.databinding.ItemUserBinding;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser firebaseUser;
    List<Chat> chats;
    String imageURL;
    Context context;


    public MessageAdapter(Context context,List<Chat> chats,String imageURL) {
        this.chats = chats;
        this.context = context;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right,parent,false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left,parent,false);
            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(holder.getAdapterPosition());

        holder.showMsg.setText(chat.getMessage());

        if (imageURL.equals("default")){
            holder.img.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context)
                    .load(imageURL)
                    .circleCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.img);
        }

        if (holder.getAdapterPosition() == chats.size()-1){
            if (chat.isSeen()){
                holder.isSeen.setText("Seen");
            }else {
                holder.isSeen.setText("Delivered");
            }
        }
        else {
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView showMsg,isSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.ItemChatImg);
            showMsg = itemView.findViewById(R.id.ItemChatshowMsg);
            isSeen = itemView.findViewById(R.id.ItemTxtSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())){
            return  MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
