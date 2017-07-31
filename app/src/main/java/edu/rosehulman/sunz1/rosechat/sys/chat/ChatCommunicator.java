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
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static edu.rosehulman.sunz1.rosechat.activities.SettingsActivity.NOTIFICATIONS;

/**
 * Created by sun on 7/25/17.
 *
 */

public class ChatCommunicator implements ChatSystem.Communicator {


    private static final String TAG = "ChatCommunicator";
    private static final int notificationID = 3446;

    private ChatSystem.OnSendMessageListener mOnSendMessageListener;
    private ChatSystem.OnGetMessagesListener mOnGetMessagesListener;
    private DatabaseReference mDatabaseReference;

    public ChatCommunicator(ChatSystem.OnSendMessageListener onSendMessageListener,
                            ChatSystem.OnGetMessagesListener onGetMessagesListener) {
        mOnSendMessageListener = onSendMessageListener;
        mOnGetMessagesListener = onGetMessagesListener;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /*
    Say if we have two users A and B in a Chat x. In the FireBase, what would the key of this Chat x be?
    Would it be chat_A_B or chat_B_A? TODO: -Sun
    I think it should be chat_A_B, but there is no specific reason behind it. - Abu
     */
    @Override
    public void sendMessageToUser(Context context, final Chat chat, String receiverFirebaseToken) {
        final String message_type_1 = chat.senderUid + "_" + chat.receiverUid; //TODO: need a helper method to get all receivers' ids
        final String message_type_2 = chat.receiverUid + "_" + chat.senderUid;

        mDatabaseReference.child(Constants.ARG_MESSAGE).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(message_type_1)) {
                    Log.e(TAG, "sendMessageToUser: " + message_type_1 + " exists");
                    //TODO: query needed
                } else if (dataSnapshot.hasChild(message_type_2)) {
                    Log.e(TAG, "sendMessageToUser: " + message_type_2 + " exists");

                } else {
                    Log.e(TAG, "sendMessageToUser: success");


                    getMessageFromUser(chat.senderUid, chat.receiverUid);
                }

                sendPushNotificationToReceiver(); //TODO: add parameters that you like
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void getMessageFromUser(String senderUid, String receiverUid) { //TODO: receiverUid should be a ArrayList
        final String message_type_1 = senderUid + "_" + receiverUid; //TODO: need a helper method to get all receivers' ids
        final String message_type_2 = receiverUid + "_" + senderUid;
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child(Constants.ARG_MESSAGE).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(message_type_1)) {
                    //TODO: query needed
                } else if (dataSnapshot.hasChild(message_type_2)) {

                } else {
                    Log.e(TAG, "getMessageFromUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("unable to get message " + databaseError.getMessage());
            }
        });
    }

    private void sendPushNotificationToReceiver(String sender, String message, Context context) { //TODO: add parameters that you like - sun
        if(!NOTIFICATIONS){
            return;
        }
        //Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.rose_logo)
                .setContentTitle(sender)
                .setContentText(message);

        Intent intent = new Intent(context, ChatCommunicator.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        //Issue notification
        NotificationManager mNotificationMananger = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationMananger.notify(notificationID, mBuilder.build());
    }
}
