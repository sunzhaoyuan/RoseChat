package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;

import edu.rosehulman.sunz1.rosechat.models.Chat;

/**
 * Created by sun on 7/25/17.
 */

public interface ChatSystem {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat);

        void getMessage(String senderUid, String receiverUid);
    }

    interface Communicator {
        void sendMessageToUser(Context context, Chat chat);

        void getMessageFromUser(String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }
}
