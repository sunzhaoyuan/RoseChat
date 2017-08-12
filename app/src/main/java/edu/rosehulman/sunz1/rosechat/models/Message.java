package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Message {
    private String name;
    private String lastMessage;
    private String profilePicURL;
    private String senderUID;
    private String receiverUID;

    private String key;

    public Message() {

    }

    public Message(String newName, String newLastInteraction, String newPictureURL, String newSenderUID, String newReceiverUID) {
        name = newName;
        lastMessage = newLastInteraction;
        profilePicURL = newPictureURL;
        senderUID = newSenderUID;
        receiverUID = newReceiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public void setSenderUID(String senderUID) {

        this.senderUID = senderUID;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }
}
