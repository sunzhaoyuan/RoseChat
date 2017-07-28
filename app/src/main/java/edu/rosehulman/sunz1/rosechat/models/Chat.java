package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by sun on 7/21/17.
 *
 * A Chat that holds everything that a "chat" needs.
 */

public class Chat {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public Long timeStamp;
//    public String firebaseToken;


    public Chat(){

    }

    // TODO: receiver ID should be in a ArrayList<String>
    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, Long timeStamp){
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timeStamp = timeStamp;
//        this.firebaseToken = firebaseToken;
    }
}
