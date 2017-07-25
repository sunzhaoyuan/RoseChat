package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.rosehulman.sunz1.rosechat.models.Chat;

/**
 * Created by sun on 7/25/17.
 */

public class ChatCommunicator implements ChatSystem.Communicator {

    private static final String TAG = "ChatCommunicator";

    private ChatSystem.OnSendMessageListener mOnSendMessageListener;
    private ChatSystem.OnGetMessagesListener mOnGetMessagesListener;
    private DatabaseReference mDatabaseReference;

    public ChatCommunicator(ChatSystem.OnSendMessageListener onSendMessageListener,
                            ChatSystem.OnGetMessagesListener onGetMessagesListener){
        mOnSendMessageListener = onSendMessageListener;
        mOnGetMessagesListener = onGetMessagesListener;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /*
    Say if we have two users A and B in a Chat x. In the FireBase, what would the key of this Chat x be?
    Would it be chat_A_B or chat_B_A? TODO: -Sun
     */
    @Override
    public void sendMessageToUser(Context context, Chat chat, String receiverFirebaseToken) {

    }

    @Override
    public void getMessageFromUser(String senderUid, String receiverUid) {

    }
}
