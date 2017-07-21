package edu.rosehulman.sunz1.rosechat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * Created by sun on 7/21/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Chat> mChats;

    public ChatRecyclerAdapter(List<Chat> chats) {
        mChats = chats;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case Constants.VIEW_TYPE_CHAT_ME:
                View viewChatMe = layoutInflater.inflate(R.layout.my_chat_layout, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMe);
                break;
            case Constants.VIEW_TYPE_CHAT_OTHER:
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
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position){
        Chat chat =  mChats.get(position);
        String profileNameString = chat.sender.substring(0,1); // we other want one char

        myChatViewHolder.chatProfilePicMeTxt.setText(profileNameString);
        myChatViewHolder.chatMessageMeTxt.setText(chat.message);
    }

    private void configureOtherChatViewHolder(OthersChatViewHolder otherChatViewHolder, int position){
        Chat chat = mChats.get(position);
        String profileNameString = chat.sender.substring(0,1);

        otherChatViewHolder.chatProfilePicOtherTxt.setText(profileNameString);
        otherChatViewHolder.chatMessageOtherTxt.setText(chat.message);

    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder{
        private TextView chatMessageMeTxt, chatProfilePicMeTxt;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            chatMessageMeTxt = (TextView) itemView.findViewById(R.id.chat_text_me);
            chatProfilePicMeTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic_me);
        }
    }

    private static class OthersChatViewHolder extends RecyclerView.ViewHolder{
        private TextView chatMessageOtherTxt, chatProfilePicOtherTxt;

        public OthersChatViewHolder(View itemView) {
            super(itemView);
            chatMessageOtherTxt = (TextView) itemView.findViewById(R.id.chat_text_other);
            chatProfilePicOtherTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic_other);
        }
    }
}
