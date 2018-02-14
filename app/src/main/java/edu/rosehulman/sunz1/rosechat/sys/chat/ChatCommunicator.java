package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static edu.rosehulman.sunz1.rosechat.activities.SettingsActivity.NOTIFICATIONS;

/**
 * Created by sun on 7/25/17.
 */

public class ChatCommunicator implements ChatSystem.Communicator {

    private static final int notificationID = 3446;

    private ChatSystem.OnSendMessageListener mOnSendMessageListener;
    private ChatSystem.OnGetMessagesListener mOnGetMessagesListener;
    private DatabaseReference mChatReference;

    public ChatCommunicator(ChatSystem.OnSendMessageListener onSendMessageListener,
                            ChatSystem.OnGetMessagesListener onGetMessagesListener) {
        mOnSendMessageListener = onSendMessageListener;
        mOnGetMessagesListener = onGetMessagesListener;
        mChatReference = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CHAT);
    }

    @Override
    public void sendMessageToUser(Context context, Integer chatRoomID, String text, String UID) {
        //TODO: need a helper method to get all receivers' ids - Sprint 3

//        mChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mChatReference.push().setValue(chat);
//                chat.setKey(dataSnapshot.getKey());
//                Log.d(Constants.TAG_CHAT, "getMessage Fired");
////                getMessageFromUser(chat.getSenderUid(), chat.getReceiverUid(), chat.getMessageKey());
////                getMessageForSending();
////                sendPushNotificationToReceiver(); //TODO: waiting for the test
//                mOnSendMessageListener.onSendMessageSuccess();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
//            }
//        });
        new SendMessageTask(chatRoomID, text, UID).execute();
    }

    private class SendMessageTask extends AsyncTask<String, String, String> {

        private Integer chatRoomID;
        private String text;
        private String UID;

        public SendMessageTask(Integer chatRoomID, String text, String UID) {
            this.chatRoomID = chatRoomID;
            this.text = text;
            this.UID = UID;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection connection = DatabaseConnectionService.getInstance().getConnection();
            try {
                // 1: varchar(50) UID; 2: int ChatRoomID; 3: nvarchar(50) text
                CallableStatement cs = connection.prepareCall("call UserSendMessage(?, ?, ?)");
                cs.setString(1, UID);
                cs.setInt(2, chatRoomID);
                cs.setString(3, text);
                cs.execute();

                Log.d(Constants.TAG_CHAT, "Send message: " + text + "Success.");
                mOnSendMessageListener.onSendMessageSuccess();
            } catch (SQLException e) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void getMessageFromUser(final String UID, final Integer chatRoomID) {

        Log.d(Constants.TAG_CHAT, "IN CHAT COMMUNICATOR\n" + "sender ID: " + UID + "\nchatRoomID: " + chatRoomID + "\n");
//        Query query = mChatReference.orderByChild(Constants.ARG_MESSAGE_KEY).equalTo(messageKey);
//        query.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                Log.d(Constants.TAG_CHAT, "This dataSnapshot is " + dataSnapshot.getValue().toString() + "\n");
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    Log.d(Constants.TAG_CHAT, chat.toString());
//                    mOnGetMessagesListener.onGetMessagesSuccess(chat);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                mOnGetMessagesListener.onGetMessagesFailure("unable to get message " + databaseError.getMessage());
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    new GetMessageFromUserTask(UID, chatRoomID).execute();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private class GetMessageFromUserTask extends AsyncTask<String, String, Boolean> {

        private String UID;
        private Integer chatRoomID;
        private ArrayList<Message> messageList;

        public GetMessageFromUserTask(String UID, Integer chatRoomID) {
            this.UID = UID;
            this.chatRoomID = chatRoomID;
            messageList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Connection connection = DatabaseConnectionService.getInstance().getConnection();
            try {
                // 1: varchar(50) UID; 2: int ChatRoomID;
                CallableStatement cs = connection.prepareCall("call GetMessageInChatRoom(?, ?)");
                cs.setString(1, UID);
                cs.setInt(2, chatRoomID);
                cs.execute();
                Log.d(Constants.TAG_CHAT, "Get message from Chat Room: " + chatRoomID + "Success.");
                ResultSet messages = cs.getResultSet();
                while (messages.next()) {
                    Integer MID = messages.getInt("MID");
                    String text = messages.getString("Text");
                    String senderID = messages.getString("SenderUID");
                    Message message = new Message(MID, text, senderID);
                    Log.d(Constants.TAG_CHAT, "Get message: " + message + "Success.");
                    messageList.add(0, message); //in databse : DESC
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean getMessageSuccessful) {
            super.onPostExecute(getMessageSuccessful);
            if (getMessageSuccessful) {
                for (Message m : messageList)
                    mOnGetMessagesListener.onGetMessagesSuccess(m);
            } else {
                mOnGetMessagesListener.onGetMessagesFailure("unable to get message ");
            }
        }
    }

    private void sendPushNotificationToReceiver(String sender, String message, Context context) {
        if (!NOTIFICATIONS) {
            return;
        }
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
