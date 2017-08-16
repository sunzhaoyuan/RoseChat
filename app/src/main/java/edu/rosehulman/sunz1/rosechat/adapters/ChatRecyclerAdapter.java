package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by sun on 7/21/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "ChatAdapter";

    public static final int VIEW_TYPE_CHAT_ME = 0;
    public static final int VIEW_TYPE_CHAT_OTHER = 1;

    private List<Chat> mChats;
    private Context mContext;

    public ChatRecyclerAdapter(Context context, List<Chat> chats) {
        mChats = chats;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if (TextUtils.equals(mChats.get(position).getSenderUid(),
                SharedPreferencesUtils.getCurrentUser(mContext))) {
            Log.d(Constants.TAG_CHAT_ADAPTER, "CONFIGURE MY CHAT");
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            Log.d(Constants.TAG_CHAT_ADAPTER, "CONFIGURE OTHER CHAT");
            configureOtherChatViewHolder((OthersChatViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        String userID = SharedPreferencesUtils.getCurrentUser(mContext);
        Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE position " + position +
                "\n    SENDER ID: " + mChats.get(position).getSenderUid() +
                "\n    mUserID: " + userID);
        if (TextUtils.equals(mChats.get(position).getSenderUid(),
                userID)) {
            Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE : ME");
            return VIEW_TYPE_CHAT_ME;
        } else {
            Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE : OTHER");
            return VIEW_TYPE_CHAT_OTHER;
        }
    }

    public boolean contains(long chatTime) {
        for (int i = 0; i < mChats.size(); i++){
            if (mChats.get(i).getTimeStamp().equals(chatTime)){
                return true;
            }
        }
        return false;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        Log.d(Constants.TAG_CHAT_ADAPTER, "A new Chat has just been ADDED: \nsenderID: " +
                chat.getSenderUid() + "\nreceiverID: " + chat.getReceiverUid());
        notifyItemInserted(mChats.size() - 1);
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        Log.d(TAG, chat.getText());
        String myProfileNameString = chat.getSenderUid().substring(0, 1); // we other want one char
//
        myChatViewHolder.chatProfilePicTxt.setText(myProfileNameString);
        myChatViewHolder.chatTxt.setText(chat.getText());
    }

    private void configureOtherChatViewHolder(OthersChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        Log.d(TAG, chat.getText());
        String otherProfileNameString = chat.getSenderUid().substring(0, 1);
//
        otherChatViewHolder.chatProfilePicTxt.setText(otherProfileNameString);
        otherChatViewHolder.chatTxt.setText(chat.getText());
    }


    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTxt, chatProfilePicTxt;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            chatTxt = (TextView) itemView.findViewById(R.id.chat_text);
            chatProfilePicTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic);
        }
    }

    private static class OthersChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTxt, chatProfilePicTxt;

        public OthersChatViewHolder(View itemView) {
            super(itemView);
            chatTxt = (TextView) itemView.findViewById(R.id.chat_text);
            chatProfilePicTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic);
        }
    }
}
