package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by sun on 7/21/17.
 */

public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "ChatAdapter";

    public static final int VIEW_TYPE_CHAT_ME = 0;
    public static final int VIEW_TYPE_CHAT_OTHER = 1;

    private List<Message> mMessageList;
    private Context mContext;
    private float FontSize = 1;
    private String FontFamily = "Default";

    public MessageRecyclerAdapter(Context context, List<Message> messageList) {
        mMessageList = messageList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_CHAT_ME:
                View viewChatMe = layoutInflater.inflate(R.layout.my_chat_layout, parent, false);
                viewHolder = new ChatViewHolder(viewChatMe);
                break;
            case VIEW_TYPE_CHAT_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.other_chat_layout, parent, false);
                viewHolder = new ChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // if me: configureMyChatViewHolder
        // if others: configureOtherChatViewHolder
        if (TextUtils.equals(mMessageList.get(position).getSenderID(),
                SharedPreferencesUtils.getCurrentUser(mContext))) {
            Log.d(Constants.TAG_CHAT_ADAPTER, "CONFIGURE MY CHAT");
            configureChatViewHolder((ChatViewHolder) holder, position);
        } else {
            Log.d(Constants.TAG_CHAT_ADAPTER, "CONFIGURE OTHER CHAT");
            configureChatViewHolder((ChatViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String userID = SharedPreferencesUtils.getCurrentUser(mContext);
        Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE position " + position +
                "\n    SENDER ID: " + mMessageList.get(position).getSenderID() +
                "\n    mUserID: " + userID);
        if (TextUtils.equals(mMessageList.get(position).getSenderID(),
                userID)) {
            Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE : ME");
            return VIEW_TYPE_CHAT_ME;
        } else {
            Log.d(Constants.TAG_CHAT_ADAPTER, "VIEW TYPE : OTHER");
            return VIEW_TYPE_CHAT_OTHER;
        }
    }

    /**
     * return true if messageList contains MID
     *
     * @param MID
     * @return
     */
    public boolean contains(int MID) {
        for (int i = 0; i < mMessageList.size(); i++){
            if (mMessageList.get(i).getMID().equals(MID)){
                return true;
            }
        }
        return false;
    }

    /**
     * Add message into messageList
     *
     * @param message
     */
    public void add(Message message) {
        mMessageList.add(message);
        Log.d(Constants.TAG_CHAT_ADAPTER, "A new Chat has just been ADDED: \nsenderID: " +
                message.getSenderID());
        notifyItemInserted(mMessageList.size() - 1);
    }

    private void configureChatViewHolder(ChatViewHolder myChatViewHolder, int position) {
        Message chat = mMessageList.get(position);
        String myProfileNameString = chat.getSenderID().substring(0, 1); // we want one char
        myChatViewHolder.chatProfilePicTxt.setText(myProfileNameString);
        myChatViewHolder.chatTxt.setText(chat.getText());
        Log.d(TAG, chat.getText());
        ArrayList<Object> list = new ArrayList<>();
        list.add(myChatViewHolder);
        list.add(chat);
        new getSetting(chat.getMID()).execute(list);

    }

    private void step2(ChatViewHolder chatViewHolder, Message chat){
        chatViewHolder.setter(FontSize,FontFamily);

    }

    /*private void configureMyChatViewHolder(ChatViewHolder myChatViewHolder, int position) {
        Message chat = mMessageList.get(position);
        Log.d(TAG, chat.getText());
        String myProfileNameString = chat.getSenderID().substring(0, 1); // we want one char
        new getSetting(chat.getMID()).execute();
        myChatViewHolder.setter(FontSize,FontFamily);
//
        myChatViewHolder.chatProfilePicTxt.setText(myProfileNameString);
        myChatViewHolder.chatTxt.setText(chat.getText());
    }

    private void configureOtherChatViewHolder(ChatViewHolder otherChatViewHolder, int position) {
        Message chat = mMessageList.get(position);
        Log.d(TAG, chat.getText());
        String otherProfileNameString = chat.getSenderID().substring(0, 1); // we want one char
        new getSetting(chat.getMID()).execute();
        otherChatViewHolder.setter(FontSize,FontFamily);
//
        otherChatViewHolder.chatProfilePicTxt.setText(otherProfileNameString);
        otherChatViewHolder.chatTxt.setText(chat.getText());
    }*/

    private class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView chatTxt, chatProfilePicTxt;
        private float fontsize = 1;
        private String  fontfamily = "Default";

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatTxt = (TextView) itemView.findViewById(R.id.chat_text);
            chatProfilePicTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic);
            //font size
            chatTxt.setTextSize(15 * fontsize);
            chatProfilePicTxt.setTextSize(15 * fontsize);
            chatTxt.setTextSize(15 * fontsize);
            chatProfilePicTxt.setTextSize(15 * fontsize);
            if(fontfamily.equals("Monospace")){
                chatTxt.setTypeface(Typeface.MONOSPACE);
            }else{
                chatTxt.setTypeface(Typeface.DEFAULT);
            }
        }

        public void setter(float fontsize,String fontfamily){
            this.fontsize = fontsize;
            this.fontfamily = fontfamily;
            chatTxt.setTextSize(15* fontsize);
            if(fontfamily.equals("Monospace")){
                chatTxt.setTypeface(Typeface.MONOSPACE,1);
            }else{
                chatTxt.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    /*private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTxt, chatProfilePicTxt;
        private float fontsize;
        private String fontfamily;

        public void setter(float fontsize,String fontfamily){
            this.fontsize = fontsize;
            this.fontfamily = fontfamily;
        }

        public MyChatViewHolder(View itemView) {
            super(itemView);
            chatTxt = (TextView) itemView.findViewById(R.id.chat_text);
            chatProfilePicTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic);
            //font size
            chatTxt.setTextSize(20 * fontsize);
            chatProfilePicTxt.setTextSize(20 * fontsize);
            chatTxt.setTextSize(20 * fontsize);
            chatProfilePicTxt.setTextSize(20 * fontsize);
            if(fontfamily.equals("Monospace")){
                chatTxt.setTypeface(Typeface.MONOSPACE);
            }else{
                chatTxt.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    private static class OthersChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTxt, chatProfilePicTxt;
        private float fontsize;
        private String fontfamily;

        public void setter(float fontsize,String fontfamily){
            this.fontsize = fontsize;
            this.fontfamily = fontfamily;
        }

        public OthersChatViewHolder(View itemView) {
            super(itemView);
            chatTxt = (TextView) itemView.findViewById(R.id.chat_text);
            chatProfilePicTxt = (TextView) itemView.findViewById(R.id.chat_profile_pic);
            //font size
            chatTxt.setTextSize(20 * fontsize);
            chatProfilePicTxt.setTextSize(20 * fontsize);
            if(fontfamily.equals("Monospace")){
                chatTxt.setTypeface(Typeface.MONOSPACE);
            }else{
                chatTxt.setTypeface(Typeface.DEFAULT);
            }

        }
    }*/

    private class getSetting extends AsyncTask<ArrayList<Object>,Integer,ArrayList<Object>>{
        private  int MID;
        private ResultSet rs;

        public getSetting(int MID) {
            this.MID = MID;
        }

        @Override
        protected ArrayList<Object> doInBackground(ArrayList<Object>... objects) {
            try {
                Connection connection = DatabaseConnectionService.getInstance().getConnection();
                CallableStatement statement = connection.prepareCall("{call GetMessageSetting(?)}");
                statement.setInt(1,MID);
                statement.execute();
                rs = statement.getResultSet();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return objects[0];
        }

        @Override
        protected void onPostExecute(ArrayList<Object> objects) {
            super.onPostExecute(objects);
            try {
                while (rs.next()) {
                    FontSize = rs.getFloat("FontSize");
                    FontFamily = rs.getString("FontFamily");
                    ChatViewHolder viewHolder = (ChatViewHolder) objects.get(0);
                    Message chat = (Message) objects.get(1);
                    step2(viewHolder, chat);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
