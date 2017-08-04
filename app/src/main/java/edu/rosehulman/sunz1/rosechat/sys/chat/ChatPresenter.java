package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;

import edu.rosehulman.sunz1.rosechat.models.Chat;

/**
 * Created by sun on 7/25/17.
 *
 * A wrap up Class for Getting and Sending Chats
 */

public class ChatPresenter implements ChatSystem.Presenter,
        ChatSystem.OnGetMessagesListener, ChatSystem.OnSendMessageListener{

    private ChatSystem.View mChatView;
    private ChatCommunicator mChatComm;

    public ChatPresenter(ChatSystem.View chatView){
        mChatView = chatView;
        mChatComm = new ChatCommunicator(this, this);
    }


    //////////////////// PRESENTER BEGINS ////////////////////
    @Override
    public void sendMessage(Context context, Chat chat) {
        mChatComm.sendMessageToUser(context, chat);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid) {
        mChatComm.getMessageFromUser(senderUid, receiverUid);
    }
    ///////////////////////// PRESENTER ENDS /////////////////////////


    //////////////////// OnSend BEGINS ////////////////////
    @Override
    public void onSendMessageSuccess() {
        mChatView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mChatView.onSendMessageFailure(message);
    }
    ///////////////////////// OnSend ENDS /////////////////////////


    //////////////////// OnGet BEGINS ////////////////////
    @Override
    public void onGetMessagesSuccess(Chat chat) {
        mChatView.onGetMessagesSuccess(chat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mChatView.onGetMessagesFailure(message);
    }
    ///////////////////////// OnGet ENDS /////////////////////////
}
