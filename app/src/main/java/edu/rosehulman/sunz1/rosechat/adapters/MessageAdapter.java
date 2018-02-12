package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.activities.ChatActivity;
import edu.rosehulman.sunz1.rosechat.models.ChatRoom;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 10-Jul-17.
 * Edit by Yifei Li and sunz1  on 2/10/18
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<ChatRoom> mChatRoomList;
    private DatabaseReference mMessageRef;
    private String mCurrentUID;



    public MessageAdapter(Context context) {
        mContext = context;
        mChatRoomList = new ArrayList<>();
        mMessageRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_MESSAGE);
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    new getChatroom().execute();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        ChatRoom chat = mChatRoomList.get(position);
        holder.mNameTextView.setText(chat.getName());
        holder.mLastInteraction.setText("not yet");
    }

    @Override
    public int getItemCount() {
        return mChatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mLastInteraction;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    messageOptions(getAdapterPosition());
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    enterChat(getAdapterPosition());
                }
            });

            mNameTextView = (TextView) itemView.findViewById(R.id.message_name);
            mLastInteraction = (TextView) itemView.findViewById(R.id.message_last_interaction);
        }


    }


    private void getMessage() {

    }

    public void removeChat(int position) {

    }

    private void enterChat(int adapterPosition) {
//        //TODO: link ChatFragment here
//        Message currentMessage = mMessageList.get(adapterPosition);
//        String messageName = currentMessage.getName();
//        String messageKey = currentMessage.getKey();
//        String receiversUID = currentMessage.getReceiverUID();
//        String senderUID = currentMessage.getSenderUID();
//        Log.d(Constants.TAG_CHAT, "IN MESSAGE_ADAPTER\nreceiverUID: " + receiversUID);
//        ChatActivity.startActivity(mContext, messageName, senderUID, receiversUID, messageKey);
//        ChatRoom currentChatRoom = mChatRoomList.get(adapterPosition);
//        ChatActivity.startActivity(mContext,currentChatRoom.getCID());
    }

    private void messageOptions(int adapterPosition) {
    }


    private class getChatroom extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... str) {
            Connection connection = DatabaseConnectionService.getInstance().getConnection();
            try {
                mChatRoomList.clear();
                CallableStatement cs = connection.prepareCall("call getChatRoom(?)");
                cs.setString(1,mCurrentUID);
                cs.execute();
                ResultSet rs = cs.getResultSet();
                while (rs.next()){
                    mChatRoomList.add(0,new ChatRoom(rs.getString("Name"),rs.getInt("CID")));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            notifyDataSetChanged();
        }
    }
}
