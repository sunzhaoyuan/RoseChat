package edu.rosehulman.sunz1.rosechat.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * Created by sun on 7/21/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_CHAT_ME = 0;
    public static final int VIEW_TYPE_CHAT_OTHER = 1;

    List<Chat> mChats;
    FirebaseUser mUser;

    public ChatRecyclerAdapter(List<Chat> chats) {
        mChats = chats;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_CHAT_ME:
                View viewChatMe = layoutInflater.inflate(R.layout.my_chat_layout, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMe);
                break;
            case VIEW_TYPE_CHAT_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.other_chat_layout, parent, false);
                viewHolder = new OthersChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // if me: configureMyChatViewHolder
        // if others: configureOtherChatViewHolder
        if (TextUtils.equals(mChats.get(position).senderUid, mUser.getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OthersChatViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_CHAT_ME;
        } else {
            return VIEW_TYPE_CHAT_OTHER;
        }
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String profileNameString = chat.sender.substring(0, 1); // we other want one char

        myChatViewHolder.chatProfilePicMeTxt.setText(profileNameString);
        myChatViewHolder.chatMessageMeTxt.setText(chat.message);
    }

    private void configureOtherChatViewHolder(OthersChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String profileNameString = chat.sender.substring(0, 1); //TODO

        otherChatViewHolder.chatProfilePicOtherTxt.setText(profileNameString);
        otherChatViewHolder.chatMessageOtherTxt.setText(chat.message);

    }


    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatMessageMeTxt, chatProfilePicMeTxt;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            chatMessageMeTxt = (TextView) itemView.findViewById(R.id.chat_text_me);
            chatProfilePicMeTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic_me);
        }
    }

    private static class OthersChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatMessageOtherTxt, chatProfilePicOtherTxt;

        public OthersChatViewHolder(View itemView) {
            super(itemView);
            chatMessageOtherTxt = (TextView) itemView.findViewById(R.id.chat_text_other);
            chatProfilePicOtherTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic_other);
        }
    }
}
