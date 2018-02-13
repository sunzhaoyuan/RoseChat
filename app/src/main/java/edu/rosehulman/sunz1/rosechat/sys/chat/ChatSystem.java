package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;

/**
 * Created by sun on 7/25/17.
 */

public interface ChatSystem {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message); //Error Message

        void onGetMessagesSuccess(String message); //Text Message

        void onGetMessagesFailure(String message); //Error Message
    }

    interface Presenter {
        void sendMessage(Context context, Integer chatRoomID, String text, String UID);

        void getMessage(String senderUid, Integer chatRoomID);
    }

    interface Communicator {
        void sendMessageToUser(Context context, Integer chatRoomID, String text, String UID);

        void getMessageFromUser(String UID, Integer chatRoomID);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(String message);

        void onGetMessagesFailure(String errorMessage);
    }
}
