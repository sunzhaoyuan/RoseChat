package edu.rosehulman.sunz1.rosechat.sys.chat;

import android.content.Context;

import edu.rosehulman.sunz1.rosechat.models.Message;

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
    public void sendMessage(Context context, Integer chatRoomID, String text, String UID) {
        mChatComm.sendMessageToUser(context, chatRoomID, text, UID);
    }

    @Override
    public void getMessage(String UID, Integer chatRoomID) {
        mChatComm.getMessageFromUser(UID, chatRoomID);
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
    public void onGetMessagesSuccess(Message message) {
        mChatView.onGetMessagesSuccess(message);
    }

    @Override
    public void onGetMessagesFailure(String errorMessage) {
        mChatView.onGetMessagesFailure(errorMessage);
    }
    ///////////////////////// OnGet ENDS /////////////////////////
}
