package edu.rosehulman.sunz1.rosechat.adapters;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.activities.ChatRoomActivity;
import edu.rosehulman.sunz1.rosechat.models.ChatRoom;
import edu.rosehulman.sunz1.rosechat.sys.chat.ChatCommunicator;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static edu.rosehulman.sunz1.rosechat.activities.SettingsActivity.NOTIFICATIONS;

/**
 * Created by agarwaa on 10-Jul-17.
 * Edit by Yifei Li and sunz1  on 2/10/18
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ChatRoom> mChatRoomList;
    private String mCurrentUID;
    private HashMap<Integer, String> mLastInteractionHolder; // for notification


    public ChatRoomAdapter(Context context) {
        mContext = context;
        mChatRoomList = new ArrayList<>();
        mLastInteractionHolder = new HashMap<>();
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    new GetChatRoomTask().execute();
                    try {
                        Thread.sleep(500);
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
    public void onBindViewHolder(ChatRoomAdapter.ViewHolder holder, int position) {
        ChatRoom chat = mChatRoomList.get(position);
        holder.mNameTextView.setText(chat.getName());
        holder.mLastInteraction.setText(chat.getLastText());
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
            //find size
            mNameTextView.setTextSize(15*(float)Constants.FONT_SIZE_FACTOR);
            mLastInteraction.setTextSize(15*(float)Constants.FONT_SIZE_FACTOR);
        }


    }


    private void getMessage() {

    }

    public void removeChat(int position) {

    }

    /**
     * Enter the ChatRoom at selected adapter position
     *
     * @param adapterPosition
     */
    private void enterChat(int adapterPosition) {
        ChatRoom currChatRoom = mChatRoomList.get(adapterPosition);
        Integer chatRoomID = currChatRoom.getCID();
        String chatRoomName = currChatRoom.getName();
        ChatRoomActivity.startActivity(mContext, chatRoomID, chatRoomName);
    }

    private void messageOptions(int adapterPosition) {
    }


    private class GetChatRoomTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... str) {
            Connection connection = DatabaseConnectionService.getInstance().getConnection();
            try {
                String name = null;
                int CID = 0;
                String lastText = null;
                String senderUID = null;
                mChatRoomList.clear();
                CallableStatement cs = connection.prepareCall("call getChatRoom(?)");
                cs.setString(1, mCurrentUID);
                cs.execute();
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    name = rs.getString("Name");
                    CID = rs.getInt("CID");
                    CallableStatement cs1 = connection.prepareCall("call GetMessageInChatRoom(?, ?)");
                    cs1.setString(1, mCurrentUID);
                    cs1.setInt(2, CID);
                    cs1.execute();
                    ResultSet messages = cs1.getResultSet();
                    messages.next();
                    try {
                        lastText = messages.getString("Text");
                        senderUID = messages.getString("SenderUID");
                    }catch (SQLException e){ //when first create chatRoom
                        lastText = "";
                        senderUID = "";
                    }
                    mChatRoomList.add(0, new ChatRoom(name, CID, lastText));
                    //first time has lastInterHolder
                    if (!mLastInteractionHolder.keySet().contains(CID)) mLastInteractionHolder.put(CID, lastText);
                    else {
                        if (!mLastInteractionHolder.get(CID).equals(lastText)) { //new message received
                            pushNotificationToReceiver(senderUID, lastText, mContext);
                            mLastInteractionHolder.put(CID, lastText); //refresh lastText
                        }
                    }
                    Log.d("NNoti", mLastInteractionHolder.toString());
                }


            } catch (SQLException e) {
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

    /**
     * Push Notification to the system
     *
     * @param sender
     * @param message
     * @param context
     */
    private void pushNotificationToReceiver(String sender, String message, Context context) {
        if (!NOTIFICATIONS) {
            return;
        }

        int notificationID = 3446;

        //Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.rose_logo)
                .setContentTitle(sender)
                .setContentText(message);

        Intent intent = new Intent(context, ChatCommunicator.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        //Issue notification
        NotificationManager mNotificationMananger = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationMananger.notify(notificationID, mBuilder.build());
    }

}

