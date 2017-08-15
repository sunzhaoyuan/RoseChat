package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

/**
 * Created by sun on 7/21/17.
 * <p>
 * A Chat that holds everything that a "chat" needs.
 */

public class Chat {
    // Used for the query in ChatCommunicator
    public static final String MESSAGE_KEY = "messageKey";

    private String key;

    private String sender;
    private String receiver; //TODO: change
    private String senderUid;
    private String receiverUid;
    private String text;
    private Long timeStamp;
    private String messageKey;
//    public String firebaseToken;


    public Chat() {

    }

    // TODO: receiver ID should be in a ArrayList<String>
    public Chat(String messageKey, String sender, String receiver, String senderUid, String receiverUid, String text, Long timeStamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.text = text;
        this.timeStamp = timeStamp;
        this.messageKey = messageKey;
//        this.firebaseToken = firebaseToken;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getTimeStamp() {

        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }


    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiverUid() {

        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderUid() {

        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiver() {

        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {

        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderUid='" + senderUid + '\'' +
                ", receiverUid='" + receiverUid + '\'' +
                ", text='" + text + '\'' +
                ", timeStamp=" + timeStamp +
                ", messageKey='" + messageKey + '\'' +
                '}';
    }
}
