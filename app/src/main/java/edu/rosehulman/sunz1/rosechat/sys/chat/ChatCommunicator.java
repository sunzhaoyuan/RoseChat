package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;
import android.os.AsyncTask;

import android.support.v4.app.NotificationCompat;
import android.telecom.Call;
import android.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * Created by sun on 7/25/17.
 */

public class ChatCommunicator implements ChatSystem.Communicator {

    private ChatSystem.OnSendMessageListener mOnSendMessageListener;
    private ChatSystem.OnGetMessagesListener mOnGetMessagesListener;

    public ChatCommunicator(ChatSystem.OnSendMessageListener onSendMessageListener,
                            ChatSystem.OnGetMessagesListener onGetMessagesListener) {
        mOnSendMessageListener = onSendMessageListener;
        mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToUser(Context context, Integer chatRoomID, String text, String UID) {
        new SendMessageTask(context, chatRoomID, text, UID).execute();
    }

    private class SendMessageTask extends AsyncTask<String, String, Boolean> {

        private Integer chatRoomID;
        private String text;
        private String UID;
        private Context context;

        public SendMessageTask(Context context, Integer chatRoomID, String text, String UID) {
            this.chatRoomID = chatRoomID;
            this.text = text;
            this.UID = UID;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Connection connection = DatabaseConnectionService.getInstance().getConnection();
            try {
                // 1: varchar(50) UID; 2: int ChatRoomID; 3: nvarchar(50) text
                CallableStatement cs = connection.prepareCall("call ? = UserSendMessage(?, ?, ?)");
                cs.registerOutParameter(1,Types.INTEGER);
                cs.setString(2, UID);
                cs.setInt(3, chatRoomID);
                cs.setString(4, text);
                cs.execute();
                int MID = cs.getInt(1);

                CallableStatement cs1 = connection.prepareCall("{call setMessageSetting(?,?,?)}");
                cs1.setInt(1,MID);
                cs1.setFloat(2,Constants.MESSAGE_FONT_SIZE);
                if(Constants.MEESSAGE_FONT_FAMILY  == 0){
                    cs1.setString(3,"Deafult");
                }else {
                    cs1.setString(3,"Monospace");
                }
                cs1.execute();
                Log.d(Constants.TAG_CHAT, "Send message: " + text + "Success.");

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sendMessageSuccessful) {
            super.onPostExecute(sendMessageSuccessful);
            if (sendMessageSuccessful) {
                mOnSendMessageListener.onSendMessageSuccess();
            } else
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: ");
        }
    }

    @Override
    public void getMessageFromUser(final Context context, final String UID, final Integer chatRoomID) {

        Log.d(Constants.TAG_CHAT, "IN CHAT COMMUNICATOR\n" + "sender ID: " + UID + "\nchatRoomID: " + chatRoomID + "\n");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    new GetMessageFromUserTask(context, UID, chatRoomID).execute();
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

        private Context context;

        public GetMessageFromUserTask(Context context, String UID, Integer chatRoomID) {
            this.UID = UID;
            this.chatRoomID = chatRoomID;
            messageList = new ArrayList<>();
            this.context = context;
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
                    messageList.add(0, message); //in databse : DESC; so latest message on the left end
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
                for (Message m : messageList) {
                    mOnGetMessagesListener.onGetMessagesSuccess(m);
                }
                Log.d("Notification", "should have notification");
//                Message lastM = messageList.get(messageList.size() - 1);
//                sendPushNotificationToReceiver(lastM.getSenderID(), lastM.getText(), context);

            } else {
                mOnGetMessagesListener.onGetMessagesFailure("unable to get message ");
            }
        }
    }
}
