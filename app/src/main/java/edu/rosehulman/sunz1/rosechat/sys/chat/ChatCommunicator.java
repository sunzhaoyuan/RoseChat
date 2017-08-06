package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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


    private static final String TAG = Constants.TAG_CHAT_COMMUNICATOR;
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
    public void sendMessageToUser(Context context, final Chat chat) {
        //TODO: need a helper method to get all receivers' ids - Sprint 3

        mChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chat.setKey(dataSnapshot.getKey());
                mChatReference.push().setValue(chat);
                getMessageFromUser(chat.getSenderUid(), chat.getReceiverUid(), chat.getMessageKey());
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
    public void getMessageFromUser(final String senderUid, String receiverUid, String messageKey) { //TODO: receiverUid should be a ArrayList - Sprint 3

        Log.d(TAG, "sender ID: " + senderUid + "\nreceiver ID: " + receiverUid + "\ndata path: " + mChatReference.getRef().toString());
        Query query = mChatReference.orderByChild(Constants.ARG_MESSAGE_KEY).equalTo(messageKey);
        Log.d(TAG, "query: " + query.toString());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                mOnGetMessagesListener.onGetMessagesSuccess(chat);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO: Sprint 3 ??
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("unable to get message " + databaseError.getMessage());
            }
        });
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
