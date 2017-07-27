package edu.rosehulman.sunz1.rosechat.models;

import java.util.ArrayList;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Message {
    String name;
    String lastMessage;
    String profilePicURL;
    String senderUID;
    ArrayList<String> receiverUID;
    String key;

    public Message(){

    }

    public Message(String newName, String newLastInteraction, String newPictureURL, String newSenderUID, ArrayList<String> newReceiverUID) {
        name = newName;
        lastMessage = newLastInteraction;
        profilePicURL = newPictureURL;
        senderUID = newSenderUID;
        receiverUID = newReceiverUID;
    }

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

    public ArrayList<String> getReceiverUID() {
        return receiverUID;
    }
}
