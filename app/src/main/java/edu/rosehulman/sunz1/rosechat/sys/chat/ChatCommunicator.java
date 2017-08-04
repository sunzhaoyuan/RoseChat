package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static edu.rosehulman.sunz1.rosechat.activities.SettingsActivity.NOTIFICATIONS;

/**
 * Created by sun on 7/25/17.
 */

public class ChatCommunicator implements ChatSystem.Communicator {


    private static final String TAG = "ChatsComm";
    private static final int notificationID = 3446;

    private ChatSystem.OnSendMessageListener mOnSendMessageListener;
    private ChatSystem.OnGetMessagesListener mOnGetMessagesListener;
    private DatabaseReference mChatReference;
    private Chat mChat;
//    private Query query;

    public ChatCommunicator(ChatSystem.OnSendMessageListener onSendMessageListener,
                            ChatSystem.OnGetMessagesListener onGetMessagesListener) {
        mOnSendMessageListener = onSendMessageListener;
        mOnGetMessagesListener = onGetMessagesListener;
        mChatReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT);

    }

    /*
    Say if we have two users A and B in a Chat x. In the FireBase, what would the key of this Chat x be?
    Would it be chat_A_B or chat_B_A?
    I think it should be chat_A_B, but there is no specific reason behind it. - Abu
     */
    @Override
    public void sendMessageToUser(Context context, final Chat chat) {
        //TODO: need a helper method to get all receivers' ids - Sprint 3

        mChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChat = chat;
                chat.setKey(dataSnapshot.getKey());
                mChatReference.push().setValue(chat);
                getMessageFromUser(chat.getSenderUid(), chat.getReceiverUid());
//                sendPushNotificationToReceiver(); //TODO: waiting for the test
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });


    }

    @Override
    public void getMessageFromUser(String senderUid, String receiverUid) { //TODO: receiverUid should be a ArrayList - Sprint 3

        Query sender1 = mChatReference.orderByChild("senderUid").equalTo(senderUid);
        Query sender2 = mChatReference.orderByChild("senderUid").equalTo(receiverUid);
        Query receiver1 = mChatReference.orderByChild("receiverUid").equalTo(senderUid);
        Query receiver2 = mChatReference.orderByChild("receiverUid").equalTo(receiverUid);

        Log.d(TAG, "sender ID: " + senderUid + "\nreceiver ID: " + receiverUid + "\ndata path: " + mChatReference.getRef().toString());

        mChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild())
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if (sender1 != null && receiver1 != null) {
//            Log.d(TAG, "SENDER_RECEIVER\n" + sender1.toString());
//        } else if (receiver2 != null && sender2 != null) {
//            Log.d(TAG, "RECEIVER_SENDER\n" + receiver1.toString());
//        } else {
//            // no chat yet
//            Log.d(TAG, "no chat yet");
//        }
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                mOnGetMessagesListener.onGetMessagesSuccess(chat);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                mOnGetMessagesListener.onGetMessagesFailure("unable to get message " + databaseError.getMessage());
//            }
//        });
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
